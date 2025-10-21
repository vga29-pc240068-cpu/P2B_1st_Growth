package jp.ac._st_Growth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.ac._st_Growth.entity.Application;

public interface ApplicationsRepository extends JpaRepository<Application, Integer> {

}
