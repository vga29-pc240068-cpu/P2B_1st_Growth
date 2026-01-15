package jp.ac._st_Growth.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jp.ac._st_Growth.entity.Application;
import jp.ac._st_Growth.entity.Chat;
import jp.ac._st_Growth.entity.User;
import jp.ac._st_Growth.repository.ApplicationsRepository;
import jp.ac._st_Growth.repository.ChatRepository;
import jp.ac._st_Growth.repository.UsersRepository;

@Controller
public class ChatController {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ApplicationsRepository applicationsRepository;

    @Autowired
    private UsersRepository usersRepository;

    // ✅ 画像保存先（固定）
    private static final Path CHAT_UPLOAD_DIR =
            Paths.get(System.getProperty("user.home"), "1st_growth_uploads", "chat");

    // ✅ 書類保存先（固定）
    private static final Path CHAT_FILE_UPLOAD_DIR =
            Paths.get(System.getProperty("user.home"), "1st_growth_uploads", "chat_files");

    // ✅ 許可する拡張子（必要なら増やしてOK）
    private static final Set<String> ALLOWED_FILE_EXT = Set.of(
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt"
    );

    // ✅ 画像の拡張子（最低限）
    private static final Set<String> ALLOWED_IMAGE_EXT = Set.of(
            "png", "jpg", "jpeg", "gif", "webp"
    );

    // ----------------------------------------------------
    // ① チャット一覧
    // ----------------------------------------------------
    @GetMapping("/user/chat/list")
    public String chatList(HttpSession session, Model model) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        var applications =
                applicationsRepository.findByUserUserIdOrRecruitmentUserUserId(userId, userId);

        model.addAttribute("applications", applications);
        model.addAttribute("loginUserId", userId);

        return "user/chat/list_chat";
    }

    // ----------------------------------------------------
    // ② チャットルーム（未承認でも表示OK）
    // ----------------------------------------------------
    @GetMapping("/user/chat/room")
    public String chatRoom(
            @RequestParam(value = "applyId", required = false) Integer applyId,
            HttpSession session,
            Model model) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        if (applyId == null) return "redirect:/user/chat/list";

        Application app = applicationsRepository.fetchDetailByApplyId(applyId).orElse(null);
        if (app == null) return "redirect:/user/chat/list";

        int status = (app.getStatus() == null) ? 0 : app.getStatus();

        boolean isRecruitOwner =
                app.getRecruitment() != null
                && app.getRecruitment().getUser() != null
                && userId.equals(app.getRecruitment().getUser().getUserId());

        boolean isApplicant =
                app.getUser() != null
                && userId.equals(app.getUser().getUserId());

        if (!isRecruitOwner && !isApplicant) return "redirect:/user/chat/list";
        if (status == 2) return "redirect:/user/chat/list";

        var chats = chatRepository.findByApplicationApplyIdOrderByTransmissionDateAsc(applyId);

        model.addAttribute("app", app);
        model.addAttribute("chats", chats);
        model.addAttribute("loginUserId", userId);

        model.addAttribute("status", status);
        model.addAttribute("isRecruitOwner", isRecruitOwner);
        model.addAttribute("isApplicant", isApplicant);

        return "user/chat/chat";
    }

    // ----------------------------------------------------
    // ③ メッセージ送信（画像 + 書類対応）
    //  - 承認済み(1)：両者OK
    //  - 未承認(0)：応募者のみOK（募集者は不可）
    // ----------------------------------------------------
    @PostMapping("/user/chat/send")
    public String sendMessage(
            @RequestParam(value = "applyId", required = false) Integer applyId,
            @RequestParam(value = "message", required = false) String message,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "file", required = false) MultipartFile file,
            HttpSession session,
            Model model) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        if (applyId == null) return "redirect:/user/chat/list";

        boolean hasText = (message != null && !message.trim().isEmpty());
        boolean hasImage = (image != null && !image.isEmpty());
        boolean hasFile = (file != null && !file.isEmpty());

        // ✅ テキスト/画像/書類 全部なしは送信しません
        if (!hasText && !hasImage && !hasFile) {
            return "redirect:/user/chat/room?applyId=" + applyId;
        }

        User sender = usersRepository.findById(userId).orElse(null);
        Application app = applicationsRepository.fetchDetailByApplyId(applyId).orElse(null);
        if (sender == null || app == null) return "redirect:/user/chat/list";

        int status = (app.getStatus() == null) ? 0 : app.getStatus();

        boolean isRecruitOwner =
                app.getRecruitment() != null
                && app.getRecruitment().getUser() != null
                && userId.equals(app.getRecruitment().getUser().getUserId());

        boolean isApplicant =
                app.getUser() != null
                && userId.equals(app.getUser().getUserId());

        if (!isRecruitOwner && !isApplicant) return "redirect:/user/chat/list";
        if (status == 2) return "redirect:/user/chat/list";

        // ✅ 未承認(0)は応募者のみ送信OKです
        if (status == 0 && !isApplicant) {
            return "redirect:/user/chat/room?applyId=" + applyId;
        }

        // ✅ 承認済み(1)以外は弾きます
        if (status != 0 && status != 1) return "redirect:/user/chat/list";

        // ✅ 送信相手（応募者→募集者、募集者→応募者）
        User receiver =
                (app.getUser() != null && sender.getUserId().equals(app.getUser().getUserId()))
                        ? (app.getRecruitment() != null ? app.getRecruitment().getUser() : null)
                        : app.getUser();

        if (receiver == null) {
            model.addAttribute("error", "送信相手が取得できません");
            return "error";
        }

        String imagePath = null;

        // ✅ 書類情報
        String filePath = null;
        String fileName = null;
        String fileMime = null;
        Integer fileSize = null;

        // ==========================
        // 画像保存
        // ==========================
        if (hasImage) {
            try {
                Files.createDirectories(CHAT_UPLOAD_DIR);

                String original = image.getOriginalFilename();
                String extLower = getExtLower(original);

                // ✅ 拡張子チェック（拡張子なしも弾きます）
                if (extLower.isEmpty() || !ALLOWED_IMAGE_EXT.contains(extLower)) {
                    return "redirect:/user/chat/room?applyId=" + applyId;
                }

                String savedName = UUID.randomUUID().toString() + "." + extLower;
                Path saveTo = CHAT_UPLOAD_DIR.resolve(savedName);

                image.transferTo(saveTo.toFile());

                // ✅ 表示用URL（WebConfigの /uploads/chat/** と一致させます）
                imagePath = "/uploads/chat/" + savedName;

            } catch (IOException e) {
                e.printStackTrace();
                return "redirect:/user/chat/room?applyId=" + applyId;
            }
        }

        // ==========================
        // 書類保存
        // ==========================
        if (hasFile) {
            try {
                Files.createDirectories(CHAT_FILE_UPLOAD_DIR);

                String original = file.getOriginalFilename();
                String extLower = getExtLower(original);

                // ✅ 拡張子チェック（拡張子なしも弾きます）
                if (extLower.isEmpty() || !ALLOWED_FILE_EXT.contains(extLower)) {
                    return "redirect:/user/chat/room?applyId=" + applyId;
                }

                String savedName = UUID.randomUUID().toString() + "." + extLower;
                Path saveTo = CHAT_FILE_UPLOAD_DIR.resolve(savedName);

                file.transferTo(saveTo.toFile());

                // ✅ 表示用URL（WebConfigの /uploads/chat_files/** と一致させます）
                filePath = "/uploads/chat_files/" + savedName;

                // ✅ DB用メタ情報（表示名は元のファイル名を使います）
                fileName = (original != null && !original.isBlank()) ? original : savedName;
                fileMime = file.getContentType();

                long size = file.getSize();
                fileSize = (size > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) size;

            } catch (IOException e) {
                e.printStackTrace();
                return "redirect:/user/chat/room?applyId=" + applyId;
            }
        }

        Chat chat = new Chat();
        chat.setApplication(app);
        chat.setRecruitment(app.getRecruitment());
        chat.setSender(sender);
        chat.setReceiver(receiver);

        // ✅ DBがMESSAGE NOT NULLでも落ちないように、テキスト無しは空文字にします
        chat.setMessage(hasText ? message.trim() : "");

        chat.setImagePath(imagePath);

        // ✅ 書類（Chatエンティティに項目がある前提です）
        chat.setFilePath(filePath);
        chat.setFileName(fileName);
        chat.setFileMime(fileMime);
        chat.setFileSize(fileSize);

        chat.setTransmissionDate(new Timestamp(System.currentTimeMillis()));

        chatRepository.save(chat);

        return "redirect:/user/chat/room?applyId=" + applyId;
    }

    // ✅ 拡張子を安全に取り出して小文字で返します
    private String getExtLower(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) return "";
        return filename.substring(dot + 1).toLowerCase();
    }

    // ----------------------------------------------------
    // ⑥ 承認確定（GET）
    // ----------------------------------------------------
    @GetMapping("/user/chat/approve_do")
    public String approveDo(
            @RequestParam(value = "applyId", required = false) Integer applyId,
            HttpSession session) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        if (applyId == null) return "redirect:/user/chat/list";

        Application app = applicationsRepository.fetchDetailByApplyId(applyId).orElse(null);
        if (app == null
                || app.getRecruitment() == null
                || app.getRecruitment().getUser() == null
                || !userId.equals(app.getRecruitment().getUser().getUserId())) {
            return "redirect:/user/chat/list";
        }

        app.setStatus(1);
        applicationsRepository.saveAndFlush(app);

        return "redirect:/user/chat/room?applyId=" + applyId;
    }

    // ----------------------------------------------------
    // ⑦ 拒否（募集者のみ）
    // ----------------------------------------------------
    @GetMapping("/user/chat/deny")
    public String deny(
            @RequestParam(value = "applyId", required = false) Integer applyId,
            HttpSession session) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        if (applyId == null) return "redirect:/user/chat/list";

        Application app = applicationsRepository.fetchDetailByApplyId(applyId).orElse(null);
        if (app == null
                || app.getRecruitment() == null
                || app.getRecruitment().getUser() == null
                || !userId.equals(app.getRecruitment().getUser().getUserId())) {
            return "redirect:/user/chat/list";
        }

        app.setStatus(2);
        applicationsRepository.saveAndFlush(app);

        return "redirect:/user/chat/list";
    }
}
