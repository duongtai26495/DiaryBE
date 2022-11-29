package com.duongtai.syndiary.services.impl;

import com.duongtai.syndiary.configs.Snippets;
import com.duongtai.syndiary.entities.Diary;
import com.duongtai.syndiary.entities.User;
import com.duongtai.syndiary.entities.UserDTO;
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
    public Diary findDiaryById(Long id) {
        return diaryRepository.findById(id).get();
    }

    @Override
    public List<Diary> findByAuthor(String username) {
        List<Diary> foundListDiary = new ArrayList<>();
        for (Diary diary:diaryRepository.findAll()) {
            if(diary.getAuthor().getUsername().equalsIgnoreCase(username)){
                foundListDiary.add(diary);
            }
        }
        return foundListDiary;
    }

    @Override
    public void deleteDiaryById(Long id) {
        diaryRepository.deleteById(id);
    }


    @Override
    public Diary updateDiaryById(Diary diary) {
        Diary foundDiary = diaryRepository.findById(diary.getId()).get();

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(Snippets.TIME_PATTERN);

        foundDiary.setLast_edited(sdf.format(date));

        if(diary.isDisplay() != foundDiary.isDisplay()){
            foundDiary.setDisplay(!foundDiary.isDisplay());
        }
        if(diary.getImage_url() != null && !diary.getImage_url().equals(foundDiary.getImage_url())){
            foundDiary.setImage_url(diary.getImage_url());
        }
        if(diary.getTitle() != null && !diary.getTitle().equals(foundDiary.getTitle())){
            foundDiary.setTitle(diary.getTitle());
        }
        if(diary.getContent() != null && !diary.getContent().equals(foundDiary.getContent())){
            foundDiary.setContent(diary.getContent());
        }
        return diaryRepository.save(foundDiary);
    }

    @Override
    public List<Diary> getAllDisplayDiary() {
        List<Diary> displayDiary = new ArrayList<>();
        for (Diary diary: diaryRepository.findAll()) {
            if(diary.isDisplay()){
               displayDiary.add(diary);
            }
        }
        return displayDiary;
    }

    @Override
    public List<Diary> getAllDiaryByAuthor() {
        List<Diary> diaries = new ArrayList<>();
        for (Diary diary : diaryRepository.findAll()){
            if(diary.getAuthor().getUsername().equalsIgnoreCase(getUsernameLogin())){
                diaries.add(diary);
            }
        }
        return diaries;
    }
}
