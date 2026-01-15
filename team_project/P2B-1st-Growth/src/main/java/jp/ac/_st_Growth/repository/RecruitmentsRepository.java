package jp.ac._st_Growth.repository;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jp.ac._st_Growth.entity.Recruitment;

public interface RecruitmentsRepository extends JpaRepository<Recruitment, Integer> {

    // =========================
    // 基本
    // =========================
    Optional<Recruitment> findByRecruitId(Integer recruitId);

    Optional<Recruitment> findByRecruitIdAndUserUserId(Integer recruitId, Integer userId);

    List<Recruitment> findAllByOrderByRecruitIdDesc();

    // 自分の募集（募集メニュー用）
    List<Recruitment> findByUserUserIdOrderByRecruitIdDesc(Integer userId);

    // 他人の募集（トップ用）
    List<Recruitment> findByUserUserIdNotOrderByRecruitIdDesc(Integer userId);

    List<Recruitment> findByClubMasterClubIdOrderByRecruitIdDesc(Integer clubId);

    // =========================
    // 🔍 検索：トップ用（他人だけ）
    // =========================
    @Query("""
        select r
        from Recruitment r
        left join r.clubMaster c
        where r.user.userId <> :userId

          and (
                :keyword is null or :keyword = ''
             or lower(coalesce(r.title, ''))        like lower(concat('%', :keyword, '%'))
             or lower(coalesce(r.notes, ''))        like lower(concat('%', :keyword, '%'))
             or lower(coalesce(r.scheduleText, '')) like lower(concat('%', :keyword, '%'))
             or lower(coalesce(r.matchFormat, ''))  like lower(concat('%', :keyword, '%'))
             or lower(coalesce(r.locationText, '')) like lower(concat('%', :keyword, '%'))
             or lower(coalesce(c.clubName, ''))     like lower(concat('%', :keyword, '%'))
          )

          and (
                :clubEmpty = true
             or c.clubId in :clubIds
          )

          and (
                :altEmpty = true
             or r.altDateOption in :altDateOptions
          )

          and (
                :capEmpty = true
             or r.capacityRange in :capacityRanges
          )

          and (
                :genEmpty = true
             or r.genderPref in :genderPrefs
          )

          and (
                :trvEmpty = true
             or r.travelOption in :travelOptions
          )

          and (
                :noPreference = 1
             or (
                    (:tgtEmpty = true or r.targetTeam in :targetTeams)
                and (:mcEmpty  = true or r.matchContent in :matchContents)
                and (:lvEmpty  = true or r.teamLevel in :teamLevels)
             )
          )

        order by r.recruitId desc
    """)
    List<Recruitment> filterSearchExcludeMineMulti(
            @Param("keyword") String keyword,

            @Param("clubEmpty") boolean clubEmpty,
            @Param("clubIds") List<Integer> clubIds,

            @Param("altEmpty") boolean altEmpty,
            @Param("altDateOptions") List<Recruitment.AltDateOption> altDateOptions,

            @Param("capEmpty") boolean capEmpty,
            @Param("capacityRanges") List<Recruitment.CapacityRange> capacityRanges,

            @Param("genEmpty") boolean genEmpty,
            @Param("genderPrefs") List<Recruitment.GenderPref> genderPrefs,

            @Param("trvEmpty") boolean trvEmpty,
            @Param("travelOptions") List<Recruitment.TravelOption> travelOptions,

            @Param("tgtEmpty") boolean tgtEmpty,
            @Param("targetTeams") List<Recruitment.TargetTeam> targetTeams,

            @Param("mcEmpty") boolean mcEmpty,
            @Param("matchContents") List<Recruitment.MatchContent> matchContents,

            @Param("lvEmpty") boolean lvEmpty,
            @Param("teamLevels") List<Recruitment.TeamLevel> teamLevels,

            @Param("noPreference") Integer noPreference,

            @Param("userId") Integer userId
    );

    // =========================
    // 🔍 検索：募集メニュー用（自分だけ）
    // =========================
    @Query("""
        select r
        from Recruitment r
        left join r.clubMaster c
        where r.user.userId = :userId

          and (
                :keyword is null or :keyword = ''
             or lower(coalesce(r.title, ''))        like lower(concat('%', :keyword, '%'))
             or lower(coalesce(r.notes, ''))        like lower(concat('%', :keyword, '%'))
             or lower(coalesce(r.scheduleText, '')) like lower(concat('%', :keyword, '%'))
             or lower(coalesce(r.matchFormat, ''))  like lower(concat('%', :keyword, '%'))
             or lower(coalesce(r.locationText, '')) like lower(concat('%', :keyword, '%'))
             or lower(coalesce(c.clubName, ''))     like lower(concat('%', :keyword, '%'))
          )

          and (
                :clubEmpty = true
             or c.clubId in :clubIds
          )

          and (
                :altEmpty = true
             or r.altDateOption in :altDateOptions
          )

          and (
                :capEmpty = true
             or r.capacityRange in :capacityRanges
          )

          and (
                :genEmpty = true
             or r.genderPref in :genderPrefs
          )

          and (
                :trvEmpty = true
             or r.travelOption in :travelOptions
          )

          and (
                :noPreference = 1
             or (
                    (:tgtEmpty = true or r.targetTeam in :targetTeams)
                and (:mcEmpty  = true or r.matchContent in :matchContents)
                and (:lvEmpty  = true or r.teamLevel in :teamLevels)
             )
          )

        order by r.recruitId desc
    """)
    List<Recruitment> filterSearchMineMulti(
            @Param("keyword") String keyword,

            @Param("clubEmpty") boolean clubEmpty,
            @Param("clubIds") List<Integer> clubIds,

            @Param("altEmpty") boolean altEmpty,
            @Param("altDateOptions") List<Recruitment.AltDateOption> altDateOptions,

            @Param("capEmpty") boolean capEmpty,
            @Param("capacityRanges") List<Recruitment.CapacityRange> capacityRanges,

            @Param("genEmpty") boolean genEmpty,
            @Param("genderPrefs") List<Recruitment.GenderPref> genderPrefs,

            @Param("trvEmpty") boolean trvEmpty,
            @Param("travelOptions") List<Recruitment.TravelOption> travelOptions,

            @Param("tgtEmpty") boolean tgtEmpty,
            @Param("targetTeams") List<Recruitment.TargetTeam> targetTeams,

            @Param("mcEmpty") boolean mcEmpty,
            @Param("matchContents") List<Recruitment.MatchContent> matchContents,

            @Param("lvEmpty") boolean lvEmpty,
            @Param("teamLevels") List<Recruitment.TeamLevel> teamLevels,

            @Param("noPreference") Integer noPreference,

            @Param("userId") Integer userId
    );

    // =========================
    // 削除
    // =========================
    @Transactional
    @Modifying
    @Query("delete from Recruitment r where r.recruitId = :recruitId")
    void deleteByRecruitId(@Param("recruitId") Integer recruitId);
}
