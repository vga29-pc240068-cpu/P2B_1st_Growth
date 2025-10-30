package jp.ac._st_Growth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.ac._st_Growth.entity.Chat;



public interface ChatsRepository extends JpaRepository<Chat, Integer>  {

}
