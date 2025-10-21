package jp.ac._st_Growth.entity;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

public class Recruitment {
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_recrument")
    @SequenceGenerator(name = "seq_recrument", sequenceName = "seq_recrument_id", allocationSize = 1)
    private Integer recruitId;

	@ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName ="userId")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "club_id",referencedColumnName ="clubId")
    private Club club;
    
    @Column
    private Date matchDate;
    
    @Column
    private String matchTime;
    
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

	@Column
    private String location;
    



}
