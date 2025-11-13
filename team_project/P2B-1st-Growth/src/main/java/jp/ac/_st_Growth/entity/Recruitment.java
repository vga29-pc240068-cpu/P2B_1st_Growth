package jp.ac._st_Growth.entity;

import java.sql.Date;

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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_recruitment")
    @SequenceGenerator(name = "seq_recruitment", sequenceName = "seq_recruit_id", allocationSize = 1)
    private Integer recruitId;

	@ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName ="user_id",nullable=false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "club_id",referencedColumnName ="club_id",nullable=false)
    private Club club;
    
    @Column(name="match_date",nullable=false)
    private Date matchDate;
    
    @Column(name="match_time",nullable=false)
    private String matchTime;
    
    @Column(name="location",nullable=false)
    private String location;
    
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

	public Integer getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(Integer recruitId) {
		this.recruitId = recruitId;
	}

	

	public Date getMatchDate() {
		return matchDate;
	}

	public void setMatchDate(Date matchDate) {
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


    



}
