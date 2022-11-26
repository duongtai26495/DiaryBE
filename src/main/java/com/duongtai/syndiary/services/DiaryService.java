package com.duongtai.syndiary.services;

import com.duongtai.syndiary.entities.Diary;

import java.util.List;

public interface DiaryService {
    Diary saveNewDiary(Diary diary);
    void deleteDiaryById(Long id);
    Diary updateDiaryById(Diary diary);

    List<Diary> getAllDisplayDiary();
}
