package com.duongtai.syndiary.services;

import com.duongtai.syndiary.entities.Comment;
import com.duongtai.syndiary.entities.Diary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DiaryService {
    Diary saveNewDiary(Diary diary);
    Diary findDiaryById(String id);
    Diary updateDiaryById(Diary diary);
    Page<Diary> getAllDisplayDiary(Pageable pageable);

    //Get all diary display by author when access by friend
    List<Diary> getAllDiaryByAuthor();

    //Get all diary display by author when access by myself
    List<Diary> findByMySelf(String username);
    List<Comment> loadCommentByDiaryId(String id);
    void deleteDiaryById(String id);
    Comment addComment(Comment comment);
    void deleteCommentById(String id);
    Comment updateCommentById(Comment comment);
    Comment loadCommentWithId(String id);
}
