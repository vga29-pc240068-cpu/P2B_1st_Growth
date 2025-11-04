package jp.ac._st_Growth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.ac._st_Growth.entity.Recruitment;

public interface RecruitmentsRepository extends JpaRepository<Recruitment, Integer> {
	 Optional<Recruitment> findById(Long recruitId);
}
