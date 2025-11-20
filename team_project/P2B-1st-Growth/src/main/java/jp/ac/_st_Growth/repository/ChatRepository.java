package jp.ac._st_Growth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.ac._st_Growth.entity.Chat;

public interface ChatRepository extends JpaRepository<Chat, Integer> {
    List<Chat> findByApplicationApplyIdOrderByTransmissionDateAsc(Integer applyId);
}
