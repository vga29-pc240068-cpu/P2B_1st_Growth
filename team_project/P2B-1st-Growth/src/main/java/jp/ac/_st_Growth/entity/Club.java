package jp.ac._st_Growth.entity;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

public class Club {
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_club")
    @SequenceGenerator(name = "seq_club", sequenceName = "seq_club_id", allocationSize = 1)
    private Integer clubId;

    @Column
    private Integer userId;
    
    @Column
    private Integer clubName;
    
    @Column
    private Date activityDate;

	public Integer getClubId() {
		return clubId;
	}

	public void setClubId(Integer clubId) {
		this.clubId = clubId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getClubName() {
		return clubName;
	}

	public void setClubName(Integer clubName) {
		this.clubName = clubName;
	}

	public Date getActivityDate() {
		return activityDate;
	}

	public void setActivityDate(Date activityDate) {
		this.activityDate = activityDate;
	}
    
    

}
