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


    // ① チャットルーム表示
    @GetMapping("/user/chat/room")
    public String chatRoom(
            @RequestParam("applyId") Integer applyId,
            HttpSession session,
            Model model) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Application application = applicationsRepository
                .findByApplyId(applyId)
                .orElse(null);

        if (application == null) {
            model.addAttribute("error", "応募情報が見つかりません");
            return "error";
        }
        
        // ここでapplicationが正しく取得できているか確認
        System.out.println("Application: " + application);

        // 未承認 → 確認画面へ
        if (application.getStatus() == 0) {
            model.addAttribute("application", application);
            return "user/chat/apply_possibility";

        }

        // 拒否
        if (application.getStatus() == 2) {
            model.addAttribute("message", "この応募は拒否されました。");
            return "error";
        }

        // 承認済み → チャット画面表示
        List<Chat> chats = chatRepository.findByApplicationApplyIdOrderByTransmissionDateAsc(applyId);

        model.addAttribute("application", application);
        model.addAttribute("chats", chats);
        model.addAttribute("loginUserId", userId);

        return "user/chat/chat";
    }


    // ② メッセージ送信
    @PostMapping("/user/chat/send")
    public String sendMessage(
            @RequestParam("applyId") Integer applyId,
            @RequestParam("message") String message,
            HttpSession session) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        User sender = usersRepository.findById(userId).orElse(null);
        if (sender == null) return "error";

        Application application = applicationsRepository.findByApplyId(applyId).orElse(null);
        if (application == null) return "error";

        // 相手（受信者）を決定
        User receiver =
                sender.getUserId().equals(application.getUser().getUserId())
                        ? application.getRecruitment().getUser()
                        : application.getUser();

        // 新規チャット作成
        Chat chat = new Chat();
        chat.setApplication(application);
        chat.setRecruitment(application.getRecruitment());
        chat.setSender(sender);
        chat.setReceiver(receiver);
        chat.setMessage(message);
        chat.setTransmissionDate(new Timestamp(System.currentTimeMillis()));

        chatRepository.save(chat);

        return "redirect:/user/chat/room?applyId=" + applyId;
    }


    // 応募承認確認ページの表示
    @GetMapping("/user/chat/apply_possibility")
    public String applyPossibility(
            @RequestParam("applyId") Integer applyId,
            Model model) {

        Application application = applicationsRepository.findByApplyId(applyId).orElse(null);

        if (application == null) {
            model.addAttribute("error", "応募情報が見つかりません");
            return "error";
        }

        model.addAttribute("application", application);

        return "user/chat/apply_possibility";


    }


    // チャット一覧
    @GetMapping("/user/chat/list")
    public String chatList(HttpSession session, Model model) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        List<Application> applications =
                applicationsRepository.findByUserUserIdOrRecruitmentUserUserId(userId, userId);

        model.addAttribute("applications", applications);
        model.addAttribute("loginUserId", userId);

        return "user/chat/list_chat";
    }


    // 承認処理
    @GetMapping("/user/chat/approve")
    public String approve(@RequestParam("applyId") Integer applyId) {
        applicationsRepository.updateStatus(applyId, 1);
        return "redirect:/user/chat/room?applyId=" + applyId;
    }

    // 拒否処理
    @GetMapping("/user/chat/deny")
    public String deny(@RequestParam("applyId") Integer applyId) {
        applicationsRepository.updateStatus(applyId, 2);
        return "redirect:/user/chat/room?applyId=" + applyId;
    }
}
