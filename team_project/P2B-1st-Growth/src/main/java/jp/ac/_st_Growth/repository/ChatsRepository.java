package jp.ac._st_Growth.repository;

<<<<<<< HEAD
import org.springframework.data.jpa.repository.JpaRepository;
=======
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
>>>>>>> db4076e618c08aab5fa5e5191570b4e75484f2a3
import org.springframework.stereotype.Repository;

import jp.ac._st_Growth.entity.Chat;

@Repository

public interface ChatsRepository extends JpaRepository<Chat, Integer>  {

<<<<<<< HEAD
=======
	
	    
	    // User ID နဲ့ chat history ရှာခြင်း
	    
	    List<Chat> findByUserId(@Param("userId") Long userId);
	    
	    // Application ID နဲ့ chat history ရှာခြင်း
	    
	    List<Chat> findByApplicationIdOrderByTransmissionDateAsc(@Param("applicationId") Long applicationId);
	    
	    // Recruitment ID နဲ့ chat history ရှာခြင်း
	    
	    List<Chat> findByRecruitmentIdOrderByTransmissionDateAsc(@Param("recruitmentId") Long recruitmentId);
	    
	    // Unread messages count
	   
	    Long countUnreadMessagesByUserId(@Param("userId") Long userId);
	    
	    // Mark messages as read
	    
	    void markMessagesAsRead(@Param("userId") Long userId);
>>>>>>> db4076e618c08aab5fa5e5191570b4e75484f2a3
	}

