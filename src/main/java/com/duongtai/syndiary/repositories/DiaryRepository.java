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


}
