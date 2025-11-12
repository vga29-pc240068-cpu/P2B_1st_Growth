package jp.ac._st_Growth.entity;

import java.sql.Date;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
@Entity
@Table(name = "chats")
public class Chat {

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_chat")
    @SequenceGenerator(name = "seq_chat", sequenceName = "seq_chat_id", allocationSize = 1)
    
    
	private Integer chatId;

	@ManyToOne
     @JoinColumn(name = "apply_id",referencedColumnName ="applyId")
    private  Application application;
	
    @ManyToOne
    @JoinColumn(name = "recruitment_id",referencedColumnName ="recruitId")
    private  Recruitment recruitment;
    
    @ManyToOne
   @JoinColumn(name = "receiver_id",referencedColumnName ="user_id")
    private  User receiver;
    
   @ManyToOne
   @JoinColumn(name = "sender_id",referencedColumnName ="user_id")
   private  User sender;
    
    @Column
    private String message;
    
    @Column
    private Date transmissionDate;

	public Integer getChatId() {
		return chatId;
	}

	public void setChatId(Integer chatId) {
		this.chatId = chatId;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public Recruitment getRecruitment() {
		return recruitment;
	}

	public void setRecruitment(Recruitment recruitment) {
		this.recruitment = recruitment;
	}

	public User getReceiver() {
		return receiver;
	}

	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}

	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getTransmissionDate() {
		return transmissionDate;
	}

	public void setTransmissionDate(Date transmissionDate) {
		this.transmissionDate = transmissionDate;
	}

	public void setReadStatus(String string) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	public void setTransmissionDate(LocalDateTime now) {
		// TODO 自動生成されたメソッド・スタブ
		
	}
    
   
}