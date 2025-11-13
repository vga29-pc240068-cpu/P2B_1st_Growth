//
//
//package jp.ac._st_Growth.controller;
//
//
//import java.time.LocalDateTime;
//import java.util.List;
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
//    @Autowired
//    private ChatsRepository chatsRepository;
//
//    @Autowired
//    private ApplicationsRepository applicationRepository;
//
//    @Autowired
//    private UsersRepository userRepository;
//
//    @GetMapping("/list")
//    public String showChatList(HttpSession session, Model model) {
//        try {
//            Integer userId = (Integer) session.getAttribute("userId"); // Changed to Integer
//            if (userId == null) {
//                return "redirect:/login";
//            }
//
//            // Get user's chats (both sent and received)
//            List<Chat> userChats = chatRepository.findByUserId(userId); // This method needs to exist
//            model.addAttribute("chats", userChats);
//            
//            return "user/chat/chat_list";
//
//        } catch (Exception e) {
//            model.addAttribute("error", "チャット一覧の読み込みに失敗しました");
//            return "user/chat/chat_list";
//        }
//    }
//
//    // 応募承認・拒否画面 - Application Approval/Rejection Screen
//    @GetMapping("/list/possibility")
//    public String showApprovalScreen(HttpSession session, Model model) {
//        try {
//            Integer userId = (Integer) session.getAttribute("userId"); // Changed to Integer
//            if (userId == null) {
//                return "redirect:/login";
//            }
//
//            // Get applications for user's recruitments
//            List<Application> userApplications = 
//                applicationRepository.findByRecruitmentOwnerUserId(userId); // Use existing method
//            model.addAttribute("applications", userApplications);
//            
//            return "user/chat/apply_Possibility";
//
//        } catch (Exception e) {
//            model.addAttribute("error", "承認画面の読み込みに失敗しました");
//            return "user/chat/apply_Possibility";
//        }
//    }
//
//    // 応募承認・拒否確認画面 - Approval/Rejection Confirmation Screen
//    @GetMapping("/possibility/check")
//    public String showApprovalConfirmation(@RequestParam("applicationId") Integer applicationId, // Changed to Integer
//                                         @RequestParam("action") String action,
//                                         HttpSession session,
//                                         Model model) {
//        try {
//            Integer userId = (Integer) session.getAttribute("userId"); // Changed to Integer
//            if (userId == null) {
//                return "redirect:/login";
//            }
//
//            List<Application> applicationList = applicationRepository.findByApplyId(applicationId); // Use List, not Optional
//            if (!applicationList.isEmpty()) {
//                Application application = applicationList.get(0);
//                // Check if current user owns the recruitment
//                if (application.getRecruitment().getUser().getUserId().equals(userId)) {
//                    model.addAttribute("application", application);
//                    model.addAttribute("action", action); // "approve" or "reject"
//                    return "user/chat/apply_Possibility_check";
//                }
//            }
//            
//            model.addAttribute("error", "応募情報が見つかりません");
//            return "redirect:/chat/list/possibility";
//
//        } catch (Exception e) {
//            model.addAttribute("error", "確認画面の読み込みに失敗しました");
//            return "redirect:/chat/list/possibility";
//        }
//    }
//
//    // 応募承認・拒否処理 - Process Approval/Rejection
//    @PostMapping("/possibility/complete")
//    public String processApproval(@RequestParam("applicationId") Integer applicationId, // Changed to Integer
//                                @RequestParam("action") String action,
//                                HttpSession session,
//                                RedirectAttributes redirectAttributes) {
//        try {
//            Integer userId = (Integer) session.getAttribute("userId"); // Changed to Integer
//            if (userId == null) {
//                return "redirect:/login";
//            }
//
//            List<Application> applicationList = applicationRepository.findByApplyId(applicationId);
//            if (!applicationList.isEmpty()) {
//                Application app = applicationList.get(0);
//                
//                // Check if current user owns the recruitment
//                if (app.getRecruitment().getUser().getUserId().equals(userId)) {
//                    
//                    List<User> senderList = userRepository.findByUserId(userId);
//                    if (senderList.isEmpty()) {
//                        redirectAttributes.addFlashAttribute("error", "ユーザー情報が見つかりません");
//                        return "redirect:/chat/list/possibility";
//                    }
//                    
//                    if ("approve".equals(action)) {
//                        // REMOVED: app.setStatus("APPROVED"); - no status field
//                        
//                        // Create initial chat message
//                        Chat chat = new Chat();
//                        chat.setApplication(app);
//                        chat.setRecruitment(app.getRecruitment());
//                        chat.setSender(senderList.get(0));
//                        chat.setReceiver(app.getUser());
//                        chat.setMessage("マッチングが成立しました！チャットを開始しましょう。");
//                        chat.setTransmissionDate(LocalDateTime.now());
//                        // REMOVED: chat.setReadStatus("UNREAD"); - check if this field exists
//                        
//                        chatRepository.save(chat);
//                        
//                        redirectAttributes.addFlashAttribute("success", "応募を承認しました");
//                    } else if ("reject".equals(action)) {
//                        // REMOVED: app.setStatus("REJECTED"); - no status field
//                        
//                        // Create rejection message
//                        Chat chat = new Chat();
//                        chat.setApplication(app);
//                        chat.setRecruitment(app.getRecruitment());
//                        chat.setSender(senderList.get(0));
//                        chat.setReceiver(app.getUser());
//                        chat.setMessage("申し訳ありませんが、マッチングは成立しませんでした。");
//                        chat.setTransmissionDate(LocalDateTime.now());
//                        // REMOVED: chat.setReadStatus("UNREAD"); - check if this field exists
//                        
//                        chatRepository.save(chat);
//                        
//                        redirectAttributes.addFlashAttribute("success", "応募を拒否しました");
//                    }
//                    
//                    applicationRepository.save(app);
//                    
//                    return "redirect:/chat/list/possibility/complete";
//                }
//            }
//            
//            redirectAttributes.addFlashAttribute("error", "応募情報が見つかりません");
//            return "redirect:/chat/list/possibility";
//
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error", "処理に失敗しました: " + e.getMessage());
//            return "redirect:/chat/list/possibility";
//        }
//    }
//
//    // 応募承認・拒否完了画面 - Approval/Rejection Completion Screen
//    @GetMapping("/list/possibility/complete")
//    public String showApprovalComplete() {
//        return "user/chat/apply_Possibility_complete";
//    }
//
//    // チャット表示画面 - Chat Display Screen
//    @GetMapping("/display")
//    public String showChat(@RequestParam("applicationId") Integer applicationId, // Changed to Integer
//                         HttpSession session,
//                         Model model) {
//        try {
//            Integer userId = (Integer) session.getAttribute("userId"); // Changed to Integer
//            if (userId == null) {
//                return "redirect:/login";
//            }
//
//            List<Application> applicationList = applicationRepository.findByApplyId(applicationId);
//            if (!applicationList.isEmpty()) {
//                Application application = applicationList.get(0);
//                // Check if current user is either applicant or recruitment owner
//                if (application.getUser().getUserId().equals(userId) || 
//                    application.getRecruitment().getUser().getUserId().equals(userId)) {
//                    
//                    // Get chat history for this application
//                    List<Chat> chatHistory = chatRepository.findByApplicationApplyIdOrderByTransmissionDateAsc(applicationId);
//                    
//                    model.addAttribute("application", application);
//                    model.addAttribute("chats", chatHistory);
//                    model.addAttribute("currentUserId", userId);
//                    
//                    return "user/chat/chat";
//                }
//            }
//            
//            model.addAttribute("error", "チャット情報が見つかりません");
//            return "redirect:/chat/list";
//
//        } catch (Exception e) {
//            model.addAttribute("error", "チャットの読み込みに失敗しました");
//            return "redirect:/chat/list";
//        }
//    }
//
//    // メッセージ送信 - Send Message
//    @PostMapping("/send")
//    public String sendMessage(@RequestParam("applicationId") Integer applicationId, // Changed to Integer
//                            @RequestParam("message") String message,
//                            HttpSession session,
//                            RedirectAttributes redirectAttributes) {
//        try {
//            Integer userId = (Integer) session.getAttribute("userId"); // Changed to Integer
//            if (userId == null) {
//                return "redirect:/login";
//            }
//
//            List<Application> applicationList = applicationRepository.findByApplyId(applicationId);
//            if (!applicationList.isEmpty()) {
//                Application app = applicationList.get(0);
//                // Check if current user is either applicant or recruitment owner
//                if (app.getUser().getUserId().equals(userId) || 
//                    app.getRecruitment().getUser().getUserId().equals(userId)) {
//                    
//                    List<User> senderList = userRepository.findByUserId(userId);
//                    if (senderList.isEmpty()) {
//                        redirectAttributes.addFlashAttribute("error", "ユーザー情報が見つかりません");
//                        return "redirect:/chat/list";
//                    }
//                    
//                    User sender = senderList.get(0);
//                    User receiver = app.getUser().getUserId().equals(userId) ? 
//                        app.getRecruitment().getUser() : app.getUser();
//                    
//                    Chat chat = new Chat();
//                    chat.setApplication(app);
//                    chat.setRecruitment(app.getRecruitment());
//                    chat.setSender(sender);
//                    chat.setReceiver(receiver);
//                    chat.setMessage(message);
//                    chat.setTransmissionDate(LocalDateTime.now());
//                    // REMOVED: chat.setReadStatus("UNREAD"); - check if this field exists
//                    
//                    chatRepository.save(chat);
//                    
//                    redirectAttributes.addFlashAttribute("success", "メッセージを送信しました");
//                } else {
//                    redirectAttributes.addFlashAttribute("error", "メッセージ送信に失敗しました");
//                }
//            } else {
//                redirectAttributes.addFlashAttribute("error", "メッセージ送信に失敗しました");
//            }
//
//            return "redirect:/chat/display?applicationId=" + applicationId;
//
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error", "メッセージ送信に失敗しました");
//            return "redirect:/chat/list";
//        }
//    }
//}
//
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
//>>>>>>> db4076e618c08aab5fa5e5191570b4e75484f2a3
