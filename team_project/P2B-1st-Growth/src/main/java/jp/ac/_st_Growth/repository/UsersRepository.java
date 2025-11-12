

package jp.ac._st_Growth.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import jp.ac._st_Growth.entity.User;

public interface UsersRepository extends JpaRepository<User, Integer> {
	
List<User> findByEmailAndPassword(String email, String password);

List<User> findByEmail(String email);
Optional<User> findByUserId(@Param("userId") Integer userId);


}