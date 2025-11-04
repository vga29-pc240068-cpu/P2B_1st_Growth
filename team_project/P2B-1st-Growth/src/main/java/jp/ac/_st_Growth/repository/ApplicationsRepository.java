package jp.ac._st_Growth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.ac._st_Growth.entity.Application;

public interface ApplicationsRepository extends JpaRepository<Application, Integer> {
	List<Application> findByUserId(Long userId);
}
