package jp.ac._st_Growth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import jp.ac._st_Growth.entity.Application;

public interface ApplicationsRepository extends JpaRepository<Application, Integer> {

	@Modifying
	@Transactional
	@Query("UPDATE Application a SET a.status = :status WHERE a.applyId = :applyId")
	void updateStatus(@Param("applyId") Integer applyId,
	                  @Param("status") Integer status);

	 
	// ログインユーザーの応募一覧
	List<Application> findByUserUserId(@Param("userId") Integer userId);
	
	// applyId は主キー → Optional<Application>
	Optional<Application> findByApplyId(Integer applyId);
	
	 // 募集IDから応募一覧を取る
	 List<Application> findByRecruitmentRecruitId(@Param("recruitId") Integer recruitId);
	
	 //自分が応募したapply(userUserId=自分)
	 //自分の募集に応募されたapply(recruitment.user.userId=自分)を両方まとめて受け取れる
	 List<Application> findByUserUserIdOrRecruitmentUserUserId(Integer userId1, Integer userId2);

	}
	



