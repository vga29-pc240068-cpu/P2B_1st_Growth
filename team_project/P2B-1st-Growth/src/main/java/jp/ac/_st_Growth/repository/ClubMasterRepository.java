package jp.ac._st_Growth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.ac._st_Growth.entity.ClubMaster;

@Repository
public interface ClubMasterRepository extends JpaRepository<ClubMaster, Integer> {

    // ★ club_id で検索するメソッドを追加
    Optional<ClubMaster> findByClubId(Integer clubId);
}
