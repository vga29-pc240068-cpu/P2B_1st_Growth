//package jp.ac._st_Growth.controller;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import jakarta.servlet.http.HttpSession;
//import jp.ac._st_Growth.entity.Application;
//import jp.ac._st_Growth.entity.Chat;
//import jp.ac._st_Growth.entity.User;
//import jp.ac._st_Growth.repository.ApplicationsRepository;
//import jp.ac._st_Growth.repository.ChatsRepository;
//import jp.ac._st_Growth.repository.UsersRepository;
//
//@Controller
//@RequestMapping("/chat")
//public class ChatController {
//	 @Autowired
//	    private ChatsRepository chatRepository;
//
//	    @Autowired
//	    private ApplicationsRepository applicationRepository;
//
//	    @Autowired
//	    private UsersRepository userRepository;
//
//	    @GetMapping("/list")
//	    public String showChatList(HttpSession session, Model model) {
//	        try {
//	            Long userId = (Long) session.getAttribute("userId");
//	            if (userId == null) {
//	                return "redirect:/login";
//	            }
//
//	            // Get user's chats (both sent and received)
//	            List<Chat> userChats = chatRepository.findByUserId(userId);
//	            model.addAttribute("chats", userChats);
//	            
//	            return "user/chat/chat_list";
//
//	        } catch (Exception e) {
//	            model.addAttribute("error", "チャット一覧の読み込みに失敗しました");
//	            return "user/chat/chat_list";
//	        }
//	    }
//
//	    // 応募承認・拒否画面 - Application Approval/Rejection Screen
//	    @GetMapping("/list/possibility")
//	    public String showApprovalScreen(HttpSession session, Model model) {
//	        try {
//	            Long userId = (Long) session.getAttribute("userId");
//	            if (userId == null) {
//	                return "redirect:/login";
//	            }
//
//	            // Get pending applications for user's recruitments
//	            List<Application> pendingApplications = 
//	                applicationRepository.findPendingApplicationsForUserRecruitments(userId);
//	            model.addAttribute("applications", pendingApplications);
//	            
//	            return "user/chat/apply_Possibility";
//
//	        } catch (Exception e) {
//	            model.addAttribute("error", "承認画面の読み込みに失敗しました");
//	            return "user/chat/apply_Possibility";
//	        }
//	    }
//
//	    // 応募承認・拒否確認画面 - Approval/Rejection Confirmation Screen
//	    @GetMapping("/possibility/check")
//	    public String showApprovalConfirmation(@RequestParam("applicationId") Long applicationId,
//	                                         @RequestParam("action") String action,
//	                                         HttpSession session,
//	                                         Model model) {
//	        try {
//	            Long userId = (Long) session.getAttribute("userId");
//	            if (userId == null) {
//	                return "redirect:/login";
//	            }
//
//	            List<Application> application = applicationRepository.findById(applicationId);
//	            if (application.isPresent() && 
//	                application.get().getRecruitment().getUser().getUserId().equals(userId)) {
//	                
//	                model.addAttribute("application", application.get());
//	                model.addAttribute("action", action); // "approve" or "reject"
//	                return "user/chat/apply_Possibility_check";
//	            } else {
//	                model.addAttribute("error", "応募情報が見つかりません");
//	                return "redirect:/chat/list/possibility";
//	            }
//
//	        } catch (Exception e) {
//	            model.addAttribute("error", "確認画面の読み込みに失敗しました");
//	            return "redirect:/chat/list/possibility";
//	        }
//	    }
//
//	    // 応募承認・拒否処理 - Process Approval/Rejection
//	    @PostMapping("/possibility/complete")
//	    public String processApproval(@RequestParam("applicationId") Long applicationId,
//	                                @RequestParam("action") String action,
//	                                HttpSession session,
//	                                RedirectAttributes redirectAttributes) {
//	        try {
//	            Long userId = (Long) session.getAttribute("userId");
//	            if (userId == null) {
//	                return "redirect:/login";
//	            }
//
//	            List<Application> application = applicationRepository.findById(applicationId);
//	            if (application.isPresent() && 
//	                application.get().getRecruitment().getUser().getUserId().equals(userId)) {
//	                
//	                Application app = application.get();
//	                
//	                if ("approve".equals(action)) {
//	                    app.setStatus("APPROVED");
//	                    
//	                    // Create initial chat message
//	                    Chat chat = new Chat();
//	                    chat.setApplication(app);
//	                    chat.setRecruitment(app.getRecruitment());
//	                    chat.setSender(userRepository.findById(userId).get());
//	                    chat.setReceiver(app.getUser());
//	                    chat.setMessage("マッチングが成立しました！チャットを開始しましょう。");
//	                    chat.setTransmissionDate(LocalDateTime.now());
//	                    chat.setReadStatus("UNREAD");
//	                    
//	                    chatRepository.save(chat);
//	                    
//	                    redirectAttributes.addFlashAttribute("success", "応募を承認しました");
//	                } else if ("reject".equals(action)) {
//	                    app.setStatus("REJECTED");
//	                    
//	                    // Create rejection message
//	                    Chat chat = new Chat();
//	                    chat.setApplication(app);
//	                    chat.setRecruitment(app.getRecruitment());
//	                    chat.setSender(userRepository.findById(userId).get());
//	                    chat.setReceiver(app.getUser());
//	                    chat.setMessage("申し訳ありませんが、マッチングは成立しませんでした。");
//	                    chat.setTransmissionDate(LocalDateTime.now());
//	                    chat.setReadStatus("UNREAD");
//	                    
//	                    chatRepository.save(chat);
//	                    
//	                    redirectAttributes.addFlashAttribute("success", "応募を拒否しました");
//	                }
//	                
//	                applicationRepository.save(app);
//	                
//	                return "redirect:/chat/list/possibility/complete";
//
//	            } else {
//	                redirectAttributes.addFlashAttribute("error", "応募情報が見つかりません");
//	                return "redirect:/chat/list/possibility";
//	            }
//
//	        } catch (Exception e) {
//	            redirectAttributes.addFlashAttribute("error", "処理に失敗しました: " + e.getMessage());
//	            return "redirect:/chat/list/possibility";
//	        }
//	    }
//
//	    // 応募承認・拒否完了画面 - Approval/Rejection Completion Screen
//	    @GetMapping("/list/possibility/complete")
//	    public String showApprovalComplete() {
//	        return "user/chat/apply_Possibility_complete";
//	    }
//
//	    // チャット表示画面 - Chat Display Screen
//	    @GetMapping("/display")
//	    public String showChat(@RequestParam("applicationId") Long applicationId,
//	                         HttpSession session,
//	                         Model model) {
//	        try {
//	            Long userId = (Long) session.getAttribute("userId");
//	            if (userId == null) {
//	                return "redirect:/login";
//	            }
//
//	            List<Application> application = applicationRepository.findById(applicationId);
//	            if (application.isPresent() && 
//	                (application.get().getUser().getUserId().equals(userId) || 
//	                 application.get().getRecruitment().getUser().getUserId().equals(userId))) {
//	                
//	                // Get chat history for this application
//	                List<Chat> chatHistory = chatRepository.findByApplicationIdOrderByTransmissionDateAsc(applicationId);
//	                
//	                model.addAttribute("application", application.get());
//	                model.addAttribute("chats", chatHistory);
//	                model.addAttribute("currentUserId", userId);
//	                
//	                return "user/chat/chat";
//	            } else {
//	                model.addAttribute("error", "チャット情報が見つかりません");
//	                return "redirect:/chat/list";
//	            }
//
//	        } catch (Exception e) {
//	            model.addAttribute("error", "チャットの読み込みに失敗しました");
//	            return "redirect:/chat/list";
//	        }
//	    }
//
//	    // メッセージ送信 - Send Message
//	    @PostMapping("/send")
//	    public String sendMessage(@RequestParam("applicationId") Long applicationId,
//	                            @RequestParam("message") String message,
//	                            HttpSession session,
//	                            RedirectAttributes redirectAttributes) {
//	        try {
//	            Long userId = (Long) session.getAttribute("userId");
//	            if (userId == null) {
//	                return "redirect:/login";
//	            }
//
//	            List<Application> application = applicationRepository.findById(applicationId);
//	            if (application.isPresent() && 
//	                (application.get().getUser().getUserId().equals(userId) || 
//	                 application.get().getRecruitment().getUser().getUserId().equals(userId))) {
//	                
//	                Application app = application.get();
//	                User sender = userRepository.findById(userId).get();
//	                User receiver = app.getUser().getUserId().equals(userId) ? 
//	                    app.getRecruitment().getUser() : app.getUser();
//	                
//	                Chat chat = new Chat();
//	                chat.setApplication(app);
//	                chat.setRecruitment(app.getRecruitment());
//	                chat.setSender(sender);
//	                chat.setReceiver(receiver);
//	                chat.setMessage(message);
//	                chat.setTransmissionDate(LocalDateTime.now());
//	                chat.setReadStatus("UNREAD");
//	                
//	                chatRepository.save(chat);
//	                
//	                redirectAttributes.addFlashAttribute("success", "メッセージを送信しました");
//	            } else {
//	                redirectAttributes.addFlashAttribute("error", "メッセージ送信に失敗しました");
//	            }
//
//	            return "redirect:/chat/display?applicationId=" + applicationId;
//
//	        } catch (Exception e) {
//	            redirectAttributes.addFlashAttribute("error", "メッセージ送信に失敗しました");
//	            return "redirect:/chat/list";
//	        }
//	    }
//	}
package jp.ac._st_Growth.Controller;

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
    public String ChatRoom(@RequestParam("applyId") Integer applyId,
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
        

       
        
      //承認チェックを入れる　未確認→確認ページ 
        if (application.getStatus() == 0) {
            model.addAttribute("message", "この応募はまだ承認されていません。");
            model.addAttribute("application", application); 
            return "redirect:/user/chat/apply_possibility?applyId=" + applyId; // ← 未承認ビュー（apply_possibility.html）
        }

       //拒否された場合
        if (application.getStatus() == 2) {
            model.addAttribute("message", "この応募は拒否されました。");
            return "error";
        }

        //承認済み(status=1)だけチャットへ
        List<Chat> chats = chatRepository.findByApplicationApplyIdOrderByTransmissionDateAsc(applyId);

        model.addAttribute("application", application);
        model.addAttribute("chats", chats);
        model.addAttribute("loginUserId", userId);

        return "user/chat/chat";
    }


    // ② メッセージ送信
    @PostMapping("/user/chat/send")
    public String sendMessage(@RequestParam("applyId") Integer applyId,
                              @RequestParam("message") String message,
                              HttpSession session) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        
        

        User sender = usersRepository.findByUserId(userId).orElse(null);
        if (sender == null) return "error";

        Application application = applicationsRepository
                .findByApplyId(applyId)
                .orElse(null);
        if (application == null) return "error";
        

        // 新規チャット作成
        Chat chat = new Chat();
        chat.setApplication(application);
        chat.setRecruitment(application.getRecruitment());
       //ユーザーを送信者に設定する
        chat.setSender(sender);
        

        // 募集者 or 応募者 を判別して receiver を決める
        //三項演算子を起用（? true/: false)
     // sender = 送信者（今メッセージを送ってる人）
     // application.getUser() = 応募者
     // application.getRecruitment().getUser() = 募集者
     //
     // → sender が応募者なら receiver は募集者
     // → sender が募集者なら receiver は応募者
        User receiver =
                sender.getUserId().equals(application.getUser().getUserId())
                ? application.getRecruitment().getUser()
                : application.getUser();

        chat.setReceiver(receiver);
        chat.setMessage(message);
        chat.setTransmissionDate(new Timestamp(System.currentTimeMillis()));

        chatRepository.save(chat);

        return "redirect:/user/chat/room?applyId=" + applyId;
    }


 // ここに追加
    @GetMapping("/user/chat/apply_possibility")
    public String applyPossibility(@RequestParam("applyId") Integer applyId, Model model) {

        Application application = applicationsRepository
                .findByApplyId(applyId)
                .orElse(null);

        if (application == null) {
            model.addAttribute("error", "応募情報が見つかりません");
            return "error";
        }

        model.addAttribute("application", application);

        return "user/chat/apply_possibility"; 
    }

    
    @GetMapping("/user/chat/list")
    public String chatList(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        // そのユーザーに関係のある応募だけ持ってくる
        List<Application> applications = applicationsRepository
        		.findByUserUserIdOrRecruitmentUserUserId(userId, userId);

        model.addAttribute("applications", applications);
        model.addAttribute("loginUserId", userId); // ここ追加

        return "user/chat/list_chat";
    }
    
    
 // 承認処理
    @GetMapping("/user/chat/approve")
    public String approve(@RequestParam("applyId") Integer applyId) {

        applicationsRepository.updateStatus(applyId, 1);  // 1 = 承認
        return "redirect:/user/chat/room?applyId=" + applyId;
    }

    // 拒否処理
    @GetMapping("/user/chat/deny")
    public String deny(@RequestParam("applyId") Integer applyId) {

        applicationsRepository.updateStatus(applyId, 2); // 2 = 拒否
        return "redirect:/user/chat/room?applyId=" + applyId;
    }



}











































































































































































































































































































































