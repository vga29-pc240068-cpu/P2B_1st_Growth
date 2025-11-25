package jp.ac._st_Growth.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "CLUB_MASTER")
public class ClubMaster {

    @Id
    @Column(name = "CLUB_ID")
    private Integer clubId;

    @Column(name = "CLUB_NAME")
    private String clubName;

	public Integer getClubId() {
		return clubId;
	}

	public void setClubId(Integer clubId) {
		this.clubId = clubId;
	}

	public String getClubName() {
		return clubName;
	}

	public void setClubName(String clubName) {
		this.clubName = clubName;
	}


}
