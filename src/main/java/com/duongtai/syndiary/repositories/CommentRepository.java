package com.duongtai.syndiary.repositories;

import com.duongtai.syndiary.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {

    @Query("SELECT c FROM Comment c WHERE c.diary_id = :diary_id AND c.display = true")
    List<Comment> getAllCommentOfDiary(@Param("diary_id") String id);
}
