package jp.ac._st_Growth.entity;

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
@Table(name = "CHATS")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_chat")
    @SequenceGenerator(name = "seq_chat", sequenceName = "SEQ_CHAT_ID", allocationSize = 1)
    @Column(name = "CHAT_ID")
    private Integer chatId;

    // ---- 関連（FK） ----

    @ManyToOne
    @JoinColumn(name = "APPLY_ID", referencedColumnName = "APPLY_ID", nullable = false)
    private Application application;

    // ✅ ここが最重要：DBは RECRUIT_ID
    @ManyToOne
    @JoinColumn(name = "RECRUIT_ID", referencedColumnName = "RECRUIT_ID", nullable = false)
    private Recruitment recruitment;

    @ManyToOne
    @JoinColumn(name = "SENDER_ID", referencedColumnName = "USER_ID", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "RECEIVER_ID", referencedColumnName = "USER_ID", nullable = false)
    private User receiver;

    // ---- メッセージ内容 ----
    @Column(name = "MESSAGE", nullable = false)
    private String message;

    // ---- 送信時刻 ----
    @Column(name = "TRANSMISSION_DATE", nullable = false)
    private Timestamp transmissionDate = new Timestamp(System.currentTimeMillis());

    // ---- Getter / Setter ----
    public Integer getChatId() { return chatId; }
    public void setChatId(Integer chatId) { this.chatId = chatId; }

    public Application getApplication() { return application; }
    public void setApplication(Application application) { this.application = application; }

    public Recruitment getRecruitment() { return recruitment; }
    public void setRecruitment(Recruitment recruitment) { this.recruitment = recruitment; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public User getReceiver() { return receiver; }
    public void setReceiver(User receiver) { this.receiver = receiver; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Timestamp getTransmissionDate() { return transmissionDate; }
    public void setTransmissionDate(Timestamp transmissionDate) { this.transmissionDate = transmissionDate; }
}
