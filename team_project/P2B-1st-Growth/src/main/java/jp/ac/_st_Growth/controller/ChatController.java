package jp.ac._st_Growth.controller;

import java.sql.Timestamp;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    // ----------------------------------------------------
    // ① チャット一覧
    // ----------------------------------------------------
    @GetMapping("/user/chat/list")
    public String chatList(HttpSession session, Model model) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        List<Application> applications =
                applicationsRepository.findByUserUserIdOrRecruitmentUserUserId(userId, userId);

        model.addAttribute("applications", applications);
        model.addAttribute("loginUserId", userId);

        return "user/chat/list_chat";
    }

    // ----------------------------------------------------
    // ② チャットルーム
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
        if (app == null) {
            model.addAttribute("error", "応募情報が見つかりません");
            return "error";
        }

        int status = (app.getStatus() == null) ? 0 : app.getStatus();

        // 未承認 → 承認画面
        if (status == 0) {
            return "redirect:/user/chat/apply_possibility?applyId=" + applyId;
        }

        // 拒否済み
        if (status == 2) {
            model.addAttribute("message", "この応募は拒否されています");
            return "error";
        }

        List<Chat> chats =
                chatRepository.findByApplicationApplyIdOrderByTransmissionDateAsc(applyId);

        model.addAttribute("app", app);
        model.addAttribute("chats", chats);
        model.addAttribute("loginUserId", userId);

        return "user/chat/chat";
    }

    // ----------------------------------------------------
    // ③ メッセージ送信
    // ----------------------------------------------------
    @PostMapping("/user/chat/send")
    public String sendMessage(
            @RequestParam("applyId") Integer applyId,
            @RequestParam("message") String message,
            HttpSession session,
            Model model) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        User sender = usersRepository.findById(userId).orElse(null);
        Application app = applicationsRepository.fetchDetailByApplyId(applyId).orElse(null);

        if (sender == null || app == null) {
            model.addAttribute("error", "データ取得に失敗しました");
            return "error";
        }

        int status = (app.getStatus() == null) ? 0 : app.getStatus();
        if (status != 1) {
            return "redirect:/user/chat/apply_possibility?applyId=" + applyId;
        }

        User receiver =
                sender.getUserId().equals(app.getUser().getUserId())
                        ? app.getRecruitment().getUser()
                        : app.getUser();

        Chat chat = new Chat();
        chat.setApplication(app);
        chat.setRecruitment(app.getRecruitment());
        chat.setSender(sender);
        chat.setReceiver(receiver);
        chat.setMessage(message);
        chat.setTransmissionDate(new Timestamp(System.currentTimeMillis()));

        chatRepository.save(chat);

        return "redirect:/user/chat/room?applyId=" + applyId;
    }

    // ----------------------------------------------------
    // ④ 承認 / 拒否 選択画面
    // ----------------------------------------------------
    @GetMapping("/user/chat/apply_possibility")
    public String applyPossibility(
            @RequestParam("applyId") Integer applyId,
            HttpSession session,
            Model model) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        Application app = applicationsRepository.fetchDetailByApplyId(applyId).orElse(null);
        if (app == null) {
            model.addAttribute("error", "応募情報が見つかりません");
            return "error";
        }

        model.addAttribute("app", app);
        model.addAttribute("loginUserId", userId);

        return "user/chat/apply_possibility";
    }

    // ----------------------------------------------------
    // ⑤ 承認チェック画面
    // ----------------------------------------------------
    @GetMapping("/user/chat/approve_check")
    public String approveCheck(
            @RequestParam("applyId") Integer applyId,
            HttpSession session,
            Model model) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        Application application = applicationsRepository.fetchDetailByApplyId(applyId).orElse(null);
        if (application == null) {
            model.addAttribute("error", "応募情報が見つかりません");
            return "error";
        }

        model.addAttribute("application", application);
        model.addAttribute("loginUserId", userId);

        return "user/chat/apply_possibility_check";
    }

    // ----------------------------------------------------
    // ⑥ 承認確定（POST）
    // ----------------------------------------------------
    @PostMapping("/user/chat/approve_do")
    public String approveDo(
            @RequestParam("applyId") Integer applyId,
            HttpSession session,
            Model model) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        Application app = applicationsRepository.fetchDetailByApplyId(applyId).orElse(null);
        if (app == null ||
            app.getRecruitment() == null ||
            !userId.equals(app.getRecruitment().getUser().getUserId())) {

            model.addAttribute("error", "募集者以外は承認できません");
            return "error";
        }

        applicationsRepository.updateStatus(applyId, 1);

        return "redirect:/user/chat/room?applyId=" + applyId;
    }

    // ----------------------------------------------------
    // ⑦ 拒否
    // ----------------------------------------------------
    @GetMapping("/user/chat/deny")
    public String deny(
            @RequestParam("applyId") Integer applyId,
            HttpSession session,
            Model model) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        Application app = applicationsRepository.fetchDetailByApplyId(applyId).orElse(null);
        if (app == null ||
            app.getRecruitment() == null ||
            !userId.equals(app.getRecruitment().getUser().getUserId())) {

            model.addAttribute("error", "募集者以外は拒否できません");
            return "error";
        }

        applicationsRepository.updateStatus(applyId, 2);

        return "redirect:/user/chat/list";
    }
}
