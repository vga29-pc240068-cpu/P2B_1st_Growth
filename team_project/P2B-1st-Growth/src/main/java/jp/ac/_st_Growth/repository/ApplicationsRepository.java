package jp.ac._st_Growth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jp.ac._st_Growth.entity.Application;

public interface ApplicationsRepository extends JpaRepository<Application, Integer> {

    @Query
    List<Application> findByUserId(@Param("userId") Long userId);
    
    // Method 2: Using property path (if User entity has getId() method)
    List<Application> findByUser_Id(Long userId);
}

