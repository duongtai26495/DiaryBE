package com.duongtai.syndiary.services.impl;

import com.duongtai.syndiary.configs.Snippets;
import com.duongtai.syndiary.entities.Diary;
import com.duongtai.syndiary.entities.User;
import com.duongtai.syndiary.repositories.DiaryRepository;
import com.duongtai.syndiary.services.DiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.duongtai.syndiary.configs.MyUserDetail.getUsernameLogin;

@Service
public class DiaryServiceImpl implements DiaryService {

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private UserServiceImpl userService;

    @Override
    public Diary saveNewDiary(Diary diary) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(Snippets.TIME_PATTERN);
        diary.setCreated_at(sdf.format(date));
        diary.setLast_edited(sdf.format(date));
        diary.setDisplay(true);
        return diaryRepository.save(diary);
    }

    @Override
    public void deleteDiaryById(Long id) {

    }

    @Override
    public Diary updateDiaryById(Diary diary) {
        return null;
    }

    @Override
    public List<Diary> getAllDisplayDiary() {
        List<Diary> displayDiary = new ArrayList<>();
        for (Diary diary: diaryRepository.findAll()) {
            if(diary.isDisplay()){
               displayDiary.add(diary);
            }
        }
        System.out.println("Return: "+displayDiary.size()+" diary");
        return displayDiary;
    }
}
