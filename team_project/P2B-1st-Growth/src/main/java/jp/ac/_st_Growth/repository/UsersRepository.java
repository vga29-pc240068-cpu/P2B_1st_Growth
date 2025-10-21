package jp.ac._st_Growth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.ac._st_Growth.entity.User;

public interface UsersRepository extends JpaRepository<User, Integer> {
	User findByEmailAndPassword(String email, String password);

}
