package jp.ac._st_Growth.repository;

import jp.ac._st_Growth.entity.User;

public interface UsersRepository {

	User findByUserIdAndPassword(String userId, String password);

}
