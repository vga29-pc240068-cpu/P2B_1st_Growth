package jp.ac._st_Growth.entity;

import java.sql.Date;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "APPLICATIONS")
public class Application {

    @Id
    @Column(name = "APPLY_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_application")
    @SequenceGenerator(
        name = "seq_application",
        sequenceName = "SEQ_APPLY_ID",
        allocationSize = 1
    )
    private Integer applyId;

    /**
     * コメント：応募したユーザーを保持いたします。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID", nullable = false)
    private User user;

    /**
     * コメント：応募対象の募集を保持いたします。
     * コメント：APPLICATIONS.RECRUIT_ID → RECRUITMENTS.RECRUIT_ID を参照いたします。
     * コメント：参照先が削除されている場合は null として扱います。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECRUIT_ID", referencedColumnName = "RECRUIT_ID", nullable = false)
    @NotFound(action = NotFoundAction.IGNORE) // ✅ 参照切れでも例外にせず null 扱いにいたします
    private Recruitment recruitment;

    /**
     * コメント：応募日を保持いたします。
     */
    @Column(name = "APPLY_DATE")
    private Date applyDate;

    /**
     * コメント：応募ステータスを保持いたします。
     * コメント：0=未承認、1=承認、2=拒否です。
     */
    @Column(name = "STATUS")
    private Integer status;

    public Application() {
        // コメント：デフォルトコンストラクタです。
    }

    public Integer getApplyId() {
        return applyId;
    }

    public void setApplyId(Integer applyId) {
        this.applyId = applyId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Recruitment getRecruitment() {
        return recruitment;
    }

    public void setRecruitment(Recruitment recruitment) {
        this.recruitment = recruitment;
    }

    public Date getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(Date applyDate) {
        this.applyDate = applyDate;
    }

    /**
     * コメント：LocalDate から応募日を設定するための補助メソッドです。
     */
    public void setApplyDate(LocalDate date) {
        this.applyDate = Date.valueOf(date);
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
