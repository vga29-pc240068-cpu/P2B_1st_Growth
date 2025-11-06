package jp.ac._st_Growth.entity;

import java.sql.Date;
import java.time.LocalDate;

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
@Table(name = "Applications")
public class Application {
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_application")
    @SequenceGenerator(name = "seq_application", sequenceName = "seq_application_id", allocationSize = 1)
    private Integer applyId;

	@ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName ="userId")
    private User user;
	
	@ManyToOne
    @JoinColumn(name = "recruit_id",referencedColumnName ="recruitId")
    private  Recruitment recruitment;
	
    @Column
    private Date applyDate;
    
    public Application() {}

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
	public void setApplyDate(LocalDate now) {
        this.applyDate = java.sql.Date.valueOf(now);
    }
	
	}



    
	


