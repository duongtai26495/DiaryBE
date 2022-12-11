package com.duongtai.syndiary.services;

import com.duongtai.syndiary.entities.Comment;
import com.duongtai.syndiary.entities.Diary;

import java.util.List;

public interface DiaryService {
    Diary saveNewDiary(Diary diary);
    Diary findDiaryById(String id);
    Diary updateDiaryById(Diary diary);
    List<Diary> getAllDisplayDiary();
    List<Diary> getAllDiaryByAuthor();
    List<Diary> findByAuthor(String username);
    List<Comment> loadCommentByDiaryId(String id);
    void deleteDiaryById(String id);
    Comment addComment(Comment comment);
    void deleteCommentById(Long id);
    Comment updateCommentById(Comment comment);
}
