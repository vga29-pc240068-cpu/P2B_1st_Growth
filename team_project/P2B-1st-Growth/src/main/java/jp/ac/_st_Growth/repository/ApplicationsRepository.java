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

    // 応募詳細（応募者・募集・募集者・部活まで全部まとめて取得）
    @Query("SELECT a FROM Application a "
         + "JOIN FETCH a.user "
         + "JOIN FETCH a.recruitment r "
         + "JOIN FETCH r.user "
         + "JOIN FETCH r.clubMaster "
         + "WHERE a.applyId = :applyId")
    Optional<Application> fetchDetailByApplyId(@Param("applyId") Integer applyId);

    // ステータス更新（更新件数を返す / 反映を確実にする）
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE Application a SET a.status = :status WHERE a.applyId = :applyId")
    int updateStatus(@Param("applyId") Integer applyId,
                     @Param("status") Integer status);

    // ログインユーザーが応募した一覧
    List<Application> findByUserUserId(Integer userId);

    // 募集IDから応募一覧を取得
    List<Application> findByRecruitmentRecruitId(Integer recruitId);

    // 自分が応募した応募 & 自分の募集に来た応募をまとめて取得
    List<Application> findByUserUserIdOrRecruitmentUserUserId(Integer userId1, Integer userId2);

    // 単体取得（主キー）
    Optional<Application> findByApplyId(Integer applyId);
}
