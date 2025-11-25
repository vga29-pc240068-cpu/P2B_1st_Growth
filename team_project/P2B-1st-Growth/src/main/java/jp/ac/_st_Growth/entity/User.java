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
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_user_id")
    @SequenceGenerator(name = "seq_user_id", sequenceName = "seq_user_id", allocationSize = 1)
    @Column(name="user_id")
    
    private Integer userId;

    @Column(name="name",nullable=false)
    private String name;

    @Column(name="email",nullable=false,unique=true)
    private String email;
    
    @Column(name="phone_number",nullable=false)
    private String phoneNumber;
    
    @Column(name="password",nullable=false)
    private String password;
    
    @Column(name="school",nullable=false)
    private String school;
    
    
    @ManyToOne
    @JoinColumn(name = "club_id",referencedColumnName ="club_id",nullable=false)
    private ClubMaster club;

    public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public ClubMaster getClub() {
		return club;
	}

	public void setClub(ClubMaster club) {
		this.club = club;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}



	

}
