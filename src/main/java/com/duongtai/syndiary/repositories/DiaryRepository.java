package com.duongtai.syndiary.repositories;

import com.duongtai.syndiary.entities.Diary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, String> {

    @Query("SELECT d FROM Diary d WHERE d.author.username = :username")
    Page<Diary> findByAuthor (String username, Pageable pageable);

    @Query("SELECT d FROM Diary d ")
    Page<Diary> getAllDisplay (Pageable pageable);

    @Query(value = "" +
            "SELECT * " +
            "FROM diary d " +
            "INNER JOIN diary_category dc " +
            "ON d.id = dc.diary_id " +
            "INNER JOIN category c " +
            "ON dc.category_id = c.id " +
            "WHERE c.id = :id",
            nativeQuery = true)
    Page<Diary> getAllByCategoryId (String id, Pageable pageable);

    @Query(value = "" +
            "SELECT * " +
            "FROM diary d " +
            "WHERE d.title " +
            "LIKE %:keyword% " +
            "AND d.display = 1 " +
            "OR d.content " +
            "LIKE %:keyword% "+
            "AND d.display = 1", nativeQuery = true )
    Page<Diary> searchDiary (String keyword, Pageable pageable);

}
