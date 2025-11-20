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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_recruitment")
    @SequenceGenerator(name = "seq_recruitment", sequenceName = "seq_recruit_id", allocationSize = 1)
    private Integer recruitId;

	@ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName ="user_id")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "club_id",referencedColumnName ="club_id")
    private Club club;
    
    @Column
    private LocalDate matchDate;
    
    @Column
    private String matchTime;
    
    @Column
    private String location;
    @Column
    private Integer scale; 
    
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

	public Integer getScale() {
		return scale;
	}

	public void setScale(Integer scale) {
		this.scale = scale;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	
	


}
