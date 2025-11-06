package jp.ac._st_Growth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import jp.ac._st_Growth.entity.Application;

public interface ApplicationsRepository extends JpaRepository<Application, Integer> {

   
	 

	List<Application> findByUserUserId(@Param("userId") Integer userId);
	
	
	List<Application> findByApplyId(Integer applyId);
	 List<Application> findByRecruitmentRecruitId(@Param("recruitId") Integer recruitId);
	
	}
	



