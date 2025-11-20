package jp.ac._st_Growth.entity;

import java.sql.Date;
import java.time.LocalDateTime;
import java.sql.Timestamp;

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
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_chat")
    @SequenceGenerator(name = "seq_chat", sequenceName = "seq_chat_id", allocationSize = 1)
    
    
	private Integer chatId;
    private Integer chatId;

	@ManyToOne
     @JoinColumn(name = "apply_id",referencedColumnName ="applyId")
    private  Application application;
	
    @ManyToOne
    @JoinColumn(name = "recruitment_id",referencedColumnName ="recruitId")
    private  Recruitment recruitment;
    
    @JoinColumn(name = "apply_id", referencedColumnName = "applyId")
    private Application application;

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
    @JoinColumn(name = "recruitment_id", referencedColumnName = "recruitId")
    private Recruitment recruitment;











	public Integer getChatId() {
		return chatId;
	}
    @ManyToOne
    @JoinColumn(name = "receiver_id", referencedColumnName = "user_id")
    private User receiver;













	public void setChatId(Integer chatId) {
		this.chatId = chatId;
	}
    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "user_id")
    private User sender;

	public Application getApplication() {
		return application;
	}
    @Column
    private String message;

	public void setApplication(Application application) {
		this.application = application;
	}
    @Column(name = "transmission_date", nullable = false)
    private Timestamp transmissionDate = new Timestamp(System.currentTimeMillis());

	public Recruitment getRecruitment() {
		return recruitment;
	}
    // --- Getter / Setter ---
    public Integer getChatId() { return chatId; }
    public void setChatId(Integer chatId) { this.chatId = chatId; }

	public void setRecruitment(Recruitment recruitment) {
		this.recruitment = recruitment;
	}
    public Application getApplication() { return application; }
    public void setApplication(Application application) { this.application = application; }

	public User getReceiver() {
		return receiver;
	}
    public Recruitment getRecruitment() { return recruitment; }
    public void setRecruitment(Recruitment recruitment) { this.recruitment = recruitment; }

	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}
    public User getReceiver() { return receiver; }
    public void setReceiver(User receiver) { this.receiver = receiver; }

	public User getSender() {
		return sender;
	}
    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

	public void setSender(User sender) {
		this.sender = sender;
	}
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

	public String getMessage() {
		return message;
	}
    public Timestamp getTransmissionDate() { return transmissionDate; }
    public void setTransmissionDate(Timestamp transmissionDate) { this.transmissionDate = transmissionDate; }

	public void setMessage(String message) {
		this.message = message;
	}
    // ----------- 既読機能（将来使う）-----------
    // public void setReadStatus(String status) { }
    // public void setTransmissionDate(LocalDateTime now) { }

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
}









