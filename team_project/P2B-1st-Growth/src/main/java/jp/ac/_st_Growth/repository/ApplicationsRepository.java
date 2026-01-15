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

    // =========================
    // 詳細：応募者・募集・募集者・部活までまとめて取得いたします
    // =========================
    @Query(
        "SELECT a FROM Application a " +
        "JOIN FETCH a.user au " +
        "JOIN FETCH a.recruitment r " +
        "JOIN FETCH r.user ru " +
        "JOIN FETCH r.clubMaster cm " +
        "WHERE a.applyId = :applyId"
    )
    Optional<Application> fetchDetailByApplyId(@Param("applyId") Integer applyId);

    // =========================
    // 応募一覧（募集者側）：自分の募集に届いた応募を取得いたします（応募者/募集/募集者/部活込み）
    // =========================
    @Query(
        "SELECT DISTINCT a FROM Application a " +
        "JOIN FETCH a.user au " +
        "JOIN FETCH a.recruitment r " +
        "JOIN FETCH r.user ru " +
        "JOIN FETCH r.clubMaster cm " +
        "WHERE r.user.userId = :ownerUserId " +
        "ORDER BY a.applyId DESC"
    )
    List<Application> fetchReceivedApplicationsOrderByApplyIdDesc(@Param("ownerUserId") Integer ownerUserId);

    // =========================
    // 応募一覧（応募者側）：自分が応募した一覧を取得いたします（応募者/募集/募集者/部活込み）
    // =========================
    @Query(
        "SELECT DISTINCT a FROM Application a " +
        "JOIN FETCH a.user au " +
        "JOIN FETCH a.recruitment r " +
        "JOIN FETCH r.user ru " +
        "JOIN FETCH r.clubMaster cm " +
        "WHERE a.user.userId = :userId " +
        "ORDER BY a.applyId DESC"
    )
    List<Application> fetchMyApplicationsOrderByApplyIdDesc(@Param("userId") Integer userId);

    // =========================
    // ステータス更新（更新件数を返します）
    // =========================
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE Application a SET a.status = :status WHERE a.applyId = :applyId")
    int updateStatus(@Param("applyId") Integer applyId, @Param("status") Integer status);

    // =========================
    // 二重応募チェック（Controller側の判定で使用いたします）
    // =========================
    boolean existsByUserUserIdAndRecruitmentRecruitId(Integer userId, Integer recruitId);

    // =========================
    // 単体取得（必要に応じて使用できます）
    // =========================
    Optional<Application> findByApplyId(Integer applyId);

    // =========================
    // 応募一覧（派生クエリ：必要に応じて使用できます）
    // =========================
    List<Application> findByUserUserId(Integer userId);
    List<Application> findByUserUserIdOrderByApplyIdDesc(Integer userId);

    List<Application> findByRecruitmentRecruitId(Integer recruitId);

    List<Application> findByRecruitmentUserUserId(Integer ownerUserId);
    List<Application> findByRecruitmentUserUserIdOrderByApplyIdDesc(Integer ownerUserId);

    List<Application> findByUserUserIdOrRecruitmentUserUserId(Integer userId1, Integer userId2);
    List<Application> findByUserUserIdOrRecruitmentUserUserIdOrderByApplyIdDesc(Integer userId1, Integer userId2);
}
