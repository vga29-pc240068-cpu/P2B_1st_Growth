package jp.ac._st_Growth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import jp.ac._st_Growth.entity.Recruitment;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Integer> {
	 

	
//主キー検索（１件だけ返す）
	Optional<Recruitment> findByRecruitId(@Param("recruitId")Integer recruitId);
//ユーザーIDで募集一覧を取得（例：特定の先生が出した募集一覧）
	 List<Recruitment> findByUserUserId(@Param("userId") Integer userId);
	 
}


// これでできること
//メソッド名	内容
//findByRecruitId()	募集IDで1件検索（詳細表示などに使う）
//findByUserUserId()	ログイン中ユーザーの募集一覧を取得（マイ募集リストなど）
//findAll()	全件取得（トップページ一覧用）