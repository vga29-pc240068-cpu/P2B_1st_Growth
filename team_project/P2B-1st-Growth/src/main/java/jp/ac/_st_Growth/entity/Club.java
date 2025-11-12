package jp.ac._st_Growth.entity;

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
	@Column(name="club_id")
    private Integer clubId;

	@ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName ="user_id",nullable=false)
    private User user;
	
    @Column(name="club_name",nullable=false)
    private String clubName;
    
    @Column(name="activity_date",nullable=false)
    private String activityDate;

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

	public String getClubName() {
		return clubName;
	}

	public void setClubName(String clubName) {
		this.clubName = clubName;
	}

	public String getActivityDate() {
		return activityDate;
	}

	public void setActivityDate(String activityDate) {
		this.activityDate = activityDate;
	}
    
    

}
