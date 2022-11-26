package com.duongtai.syndiary.controllers;

import com.duongtai.syndiary.configs.Snippets;
import com.duongtai.syndiary.entities.Diary;
import com.duongtai.syndiary.entities.ResponseObject;
import com.duongtai.syndiary.entities.User;
import com.duongtai.syndiary.services.impl.DiaryServiceImpl;
import com.duongtai.syndiary.services.impl.StorageServiceImpl;
import com.duongtai.syndiary.services.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.duongtai.syndiary.configs.MyUserDetail.getUsernameLogin;

@CrossOrigin
@RestController
@RequestMapping("/diary/")
public class DiaryController {

    @Autowired
    StorageServiceImpl storageService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    DiaryServiceImpl diaryService;

    @PostMapping("save")
    public ResponseEntity saveDiary(@RequestBody Diary diary){
        if(getUsernameLogin() != null){
            diary.setAuthor(userService.getUserByUsername(getUsernameLogin()));
            return  ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(Snippets.SUCCESS,Snippets.DIARY_CREATE_SUCCESS,diaryService.saveNewDiary(diary))
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                new ResponseObject(Snippets.FAILED,Snippets.YOU_DONT_HAVE_PERMISSION, null));
    }
}
