package jp.ac._st_Growth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.ac._st_Growth.entity.Chat;

@Repository

public interface ChatsRepository extends JpaRepository<Chat, Integer>  {

	}

