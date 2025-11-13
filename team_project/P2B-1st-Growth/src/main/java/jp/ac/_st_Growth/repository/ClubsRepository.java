package jp.ac._st_Growth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.ac._st_Growth.entity.Club;

public interface ClubsRepository extends JpaRepository<Club, Integer> {

	Optional<Club> findByClubId(Integer clubId);

}
