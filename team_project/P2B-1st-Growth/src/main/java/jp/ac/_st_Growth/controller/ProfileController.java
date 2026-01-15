package jp.ac._st_Growth.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jp.ac._st_Growth.entity.CompetitionRecord;
import jp.ac._st_Growth.entity.User;
import jp.ac._st_Growth.repository.CompetitionRecordRepository;
import jp.ac._st_Growth.repository.UsersRepository;

@Controller
public class ProfileController {

    @Autowired
    private CompetitionRecordRepository recordRepo;

    @Autowired
    private UsersRepository usersRepository;

    // ===== 保存先（実行中でも反映される場所）=====
    private static final String RECORD_DIR = "uploads/records";
    private static final String ICON_DIR  = "uploads/icon";

    // =========================
    // プロフィール表示
    // =========================
    @GetMapping("/user/profile")
    public String profile(HttpSession session, Model model) {

        User loginUser = (User) session.getAttribute("loginUser");
        model.addAttribute("user", loginUser);

        if (loginUser != null) {
            model.addAttribute(
                "records",
                recordRepo.findByUserIdOrderByUploadedAtDesc(loginUser.getUserId())
            );
        } else {
            model.addAttribute("records", List.of());
        }

        return "user/profile/profile";
    }

    // =========================
    // ✅ プロフィールアイコンアップロード
    // =========================
    @PostMapping("/user/icon/upload")
    public String uploadIcon(
            @RequestParam("iconFile") MultipartFile file,
            HttpSession session
    ) throws IOException {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";
        if (file == null || file.isEmpty()) return "redirect:/user/profile";

        // 画像だけ許可（雑でもOK）
        String ct = file.getContentType();
        if (ct == null || !ct.startsWith("image/")) return "redirect:/user/profile";

        Files.createDirectories(Paths.get(ICON_DIR));

        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
        }
        if (ext.isBlank()) ext = ".png";

        String fileName = loginUser.getUserId() + "_" + UUID.randomUUID() + ext;

        Path savePath = Paths.get(ICON_DIR, fileName);
        Files.copy(file.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);

        // DB & セッション更新（表示URLは /icon/xxxx）
        loginUser.setIconPath("/icon/" + fileName);
        usersRepository.save(loginUser);
        session.setAttribute("loginUser", loginUser);

        return "redirect:/user/profile";
    }

    // =========================
    // 大会記録アップロード
    // =========================
    @PostMapping("/user/profile/record/upload")
    public String uploadRecord(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title,
            HttpSession session
    ) throws IOException {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        String originalName = file.getOriginalFilename();
        String lower = (originalName == null) ? "" : originalName.toLowerCase();

        boolean allowed =
                lower.endsWith(".jpg") ||
                lower.endsWith(".jpeg") ||
                lower.endsWith(".png") ||
                lower.endsWith(".pdf");

        if (file.isEmpty() || !allowed) return "redirect:/user/profile";

        Files.createDirectories(Paths.get(RECORD_DIR));

        String storedName = UUID.randomUUID() + "_" + originalName.replaceAll("[\\\\/]", "_");
        Path savePath = Paths.get(RECORD_DIR, storedName);
        Files.copy(file.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);

        CompetitionRecord r = new CompetitionRecord();
        r.setUserId(loginUser.getUserId());
        r.setTitle(title == null ? "" : title);
        r.setOriginalName(originalName);
        r.setStoredName(storedName);
        r.setContentType(file.getContentType());
        r.setUploadedAt(new Timestamp(System.currentTimeMillis()));

        recordRepo.save(r);

        return "redirect:/user/profile";
    }

    // =========================
    // 記録ファイル表示
    // =========================
    @GetMapping("/user/profile/record/file/{id}")
    public void recordFile(
            @PathVariable("id") Long id,
            HttpSession session,
            HttpServletResponse res
    ) throws IOException {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        CompetitionRecord r = recordRepo.findById(id).orElse(null);
        if (r == null || !r.getUserId().equals(loginUser.getUserId())) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Path p = Paths.get(RECORD_DIR, r.getStoredName());
        if (!Files.exists(p)) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        res.setContentType(
            r.getContentType() != null ? r.getContentType() : "application/octet-stream"
        );
        res.setHeader(
            "Content-Disposition",
            "inline; filename=\"" + r.getOriginalName() + "\""
        );

        Files.copy(p, res.getOutputStream());
    }

    // =========================
    // 記録削除
    // =========================
    @PostMapping("/user/profile/record/delete/{id}")
    public String deleteRecord(@PathVariable("id") Long id, HttpSession session) throws IOException {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        CompetitionRecord r = recordRepo.findById(id).orElse(null);
        if (r == null) return "redirect:/user/profile";

        if (!r.getUserId().equals(loginUser.getUserId())) {
            return "redirect:/user/profile";
        }

        Path p = Paths.get(RECORD_DIR, r.getStoredName());
        Files.deleteIfExists(p);
        recordRepo.deleteById(id);

        return "redirect:/user/profile";
    }
}
