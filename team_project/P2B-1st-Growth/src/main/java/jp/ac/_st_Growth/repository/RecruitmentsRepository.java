package jp.ac._st_Growth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import jp.ac._st_Growth.entity.Recruitment;

public interface RecruitmentsRepository extends JpaRepository<Recruitment, Integer> {
	 

	

	List<Recruitment> findByRecruitId(Integer recruitId);
	 List<Recruitment> findByUserUserId(@Param("userId") Integer userId);
	 
}
