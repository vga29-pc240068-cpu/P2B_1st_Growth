
package jp.ac._st_Growth.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import jp.ac._st_Growth.entity.User;

public interface UsersRepository extends JpaRepository<User, Integer> {
	List<User> findByEmailAndPassword(String email, String password);
List<User> findByEmail(String email);
    
    List<User> findByNameContaining(String name);
    
    
    List<User> findBySchoolId(@Param("schoolId") Long schoolId);
    
    
    List<User> findByClubId(@Param("clubId") Long clubId);
    
    
    List<User> findByUserRole(@Param("role") String role);
    
    boolean existsByEmail(String email);
    List<User> findAllAdmins();

}