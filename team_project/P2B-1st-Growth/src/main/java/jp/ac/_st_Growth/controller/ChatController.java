package jp.ac._st_Growth.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import jp.ac._st_Growth.entity.Application;
import jp.ac._st_Growth.entity.Chat;
import jp.ac._st_Growth.repository.ApplicationsRepository;
import jp.ac._st_Growth.repository.ChatsRepository;
import jp.ac._st_Growth.repository.UsersRepository;

@Controller
@RequestMapping("/chat")
public class ChatController {
	 @Autowired
	    private ChatsRepository chatRepository;

	    @Autowired
	    private ApplicationsRepository applicationRepository;

	    @Autowired
	    private UsersRepository userRepository;

	    @GetMapping("/list")
	    public String showChatList(HttpSession session,Integer userId,Model model) { 
	    	 List<Chat> userChats = chatRepository.findByUserId(userId);
	            model.addAttribute("chats", userChats);
	            
	            return "user/chat/chat_list";
	    }
	   
	    @GetMapping("/list/possibility")
	    public String showApprovalScreen(HttpSession session,Integer userId, Model model) {
	    	 List<Application> pendingApplications = 
	                 applicationRepository.findPendingApplicationsForUserRecruitments(userId);
	             model.addAttribute("applications", pendingApplications);
	             
	             return "user/chat/apply_Possibility";

}}
