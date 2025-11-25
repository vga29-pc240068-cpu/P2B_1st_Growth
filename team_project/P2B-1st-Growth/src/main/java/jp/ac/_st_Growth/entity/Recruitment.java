package jp.ac._st_Growth.entity;

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
@Table(name = "recruitments")
public class Recruitment {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RECRUIT_SEQ")
	@SequenceGenerator(name = "RECRUIT_SEQ", sequenceName = "RECRUIT_SEQ", allocationSize = 1)
	@Column(name = "RECRUIT_ID")
	private Integer recruitId;

	@ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName ="user_id")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "club_id",referencedColumnName ="club_id")
    private Club club;
    
    //希望日時
    @Column(name = "match_date", nullable = true) 
    private LocalDate matchDate;
    
    //希望時間
    @Column(name = "match_time", nullable = true)
    private String matchTime;
    
    //希望場所
    @Column(nullable = true) 
    private String location;
    
    //-----------------------------追記
    //募集の目的
    @Column(nullable=true)
    private String purpose;
    
    //活動レベル
    @Column(name="skill_level",nullable=true)
    private String skilllevel;
    
    //参加条件
    @Column(nullable=true)
    private String conditions;
    
    //備考
    @Column(length = 4000)
    private String remarks;

	public Integer getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(Integer recruitId) {
		this.recruitId = recruitId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Club getClub() {
		return club;
	}

	public void setClub(Club club) {
		this.club = club;
	}

	public LocalDate getMatchDate() {
		return matchDate;
	}

	public void setMatchDate(LocalDate matchDate) {
		this.matchDate = matchDate;
	}

	public String getMatchTime() {
		return matchTime;
	}

	public void setMatchTime(String matchTime) {
		this.matchTime = matchTime;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}


	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getSkill_level() {
		return skilllevel;
	}

	public void setSkill_level(String skill_level) {
		this.skilllevel = skill_level;
	}

	public String getConditions() {
		return conditions;
	}

	public void setConditions(String conditions) {
		this.conditions = conditions;
	}
	

	
	


}
