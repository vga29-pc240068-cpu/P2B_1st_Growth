package jp.co.kikaku.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.co.kikaku.entity.User;

public interface UsersRepository extends JpaRepository<User, Integer> {
	//ログイン時に使用するメソッド(emailとpasswordで検索をかける)
	User findByEmailAndPassword(String email, String password);
}
