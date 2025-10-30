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
@Table(name = "clubs")
public class Club {
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_club")
    @SequenceGenerator(name = "seq_club", sequenceName = "seq_club_id", allocationSize = 1)
    private Integer clubId;

	@ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName ="userId")
    private User user;
	
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

	
	

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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
