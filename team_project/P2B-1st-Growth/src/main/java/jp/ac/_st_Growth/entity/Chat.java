package jp.ac._st_Growth.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "CHATS")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_chat")
    @SequenceGenerator(
        name = "seq_chat",
        sequenceName = "SEQ_CHAT_ID",
        allocationSize = 1
    )
    @Column(name = "CHAT_ID")
    private Integer chatId;

    // --------------------
    // 関連（FK）
    // --------------------

    @ManyToOne
    @JoinColumn(name = "APPLY_ID", referencedColumnName = "APPLY_ID", nullable = false)
    private Application application;

    /**
     * 募集情報です。
     * DB上は RECRUIT_ID を参照します。
     */
    @ManyToOne
    @JoinColumn(name = "RECRUIT_ID", referencedColumnName = "RECRUIT_ID", nullable = false)
    private Recruitment recruitment;

    /**
     * 送信者です。
     */
    @ManyToOne
    @JoinColumn(name = "SENDER_ID", referencedColumnName = "USER_ID", nullable = false)
    private User sender;

    /**
     * 受信者です。
     */
    @ManyToOne
    @JoinColumn(name = "RECEIVER_ID", referencedColumnName = "USER_ID", nullable = false)
    private User receiver;

    // --------------------
    // メッセージ内容
    // --------------------

    /**
     * メッセージ本文です。
     * 画像/ファイルのみ送信できるよう nullable=true としています。
     */
    @Column(name = "MESSAGE", nullable = true)
    private String message;

    /**
     * 画像ファイルのパスです。
     */
    @Column(name = "IMAGE_PATH", nullable = true)
    private String imagePath;

    // ✅ 追加：書類ファイル
    @Column(name = "FILE_PATH", nullable = true)
    private String filePath;

    @Column(name = "FILE_NAME", nullable = true)
    private String fileName;

    @Column(name = "FILE_MIME", nullable = true)
    private String fileMime;

    @Column(name = "FILE_SIZE", nullable = true)
    private Integer fileSize;

    // --------------------
    // 送信時刻
    // --------------------

    /**
     * 送信日時です。
     * 登録時に自動で現在時刻が設定されます。
     */
    @Column(name = "TRANSMISSION_DATE", nullable = false)
    private Timestamp transmissionDate;

    @PrePersist
    public void onCreate() {
        this.transmissionDate = new Timestamp(System.currentTimeMillis());
    }

    // --------------------
    // Getter / Setter
    // --------------------

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

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileMime() { return fileMime; }
    public void setFileMime(String fileMime) { this.fileMime = fileMime; }

    public Integer getFileSize() { return fileSize; }
    public void setFileSize(Integer fileSize) { this.fileSize = fileSize; }

    public Timestamp getTransmissionDate() { return transmissionDate; }
    public void setTransmissionDate(Timestamp transmissionDate) { this.transmissionDate = transmissionDate; }
}
