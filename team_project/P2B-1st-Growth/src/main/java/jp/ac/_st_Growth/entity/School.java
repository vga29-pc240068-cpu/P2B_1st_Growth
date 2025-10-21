package jp.ac._st_Growth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "schools")
public class School {
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_school")
	    @SequenceGenerator(name = "seq_school", sequenceName = "seq_school_id", allocationSize = 1)
	    private Integer schoolId;

	    @Column
	    private String schoolName;

	    @Column
	    private String location;

		public Integer getSchoolId() {
			return schoolId;
		}

		public void setSchoolId(Integer schoolId) {
			this.schoolId = schoolId;
		}

		public String getSchoolName() {
			return schoolName;
		}

		public void setSchoolName(String schoolName) {
			this.schoolName = schoolName;
		}

		public String getLocation() {
			return location;
		}

		public void setLocation(String location) {
			this.location = location;
		}

	    

}
