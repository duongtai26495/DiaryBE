package com.duongtai.syndiary.controllers;

import com.duongtai.syndiary.entities.Diary;
import com.duongtai.syndiary.services.impl.DiaryServiceImpl;
import com.duongtai.syndiary.services.impl.StorageServiceImpl;
import com.duongtai.syndiary.services.impl.UserServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class HomeController {

    @Autowired
    private StorageServiceImpl storageService;
    
    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private DiaryServiceImpl diaryService;

    @GetMapping("images/{fileName:.+}")
    public ResponseEntity<byte[]> readFile (@PathVariable String fileName){
        return storageService.readFile(fileName);
    }
    @GetMapping("images/profile/{fileName:.+}")
    public ResponseEntity<byte[]> readProfileImage (@PathVariable String fileName){
        return storageService.readProfileImage(fileName);
    }

    @GetMapping("")
    public List<Diary> getAllDiaryDisplay(){
        return diaryService.getAllDisplayDiary();
    }
}
