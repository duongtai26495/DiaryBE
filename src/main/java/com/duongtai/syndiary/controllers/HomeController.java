package com.duongtai.syndiary.controllers;

import com.duongtai.syndiary.configs.Snippets;
import com.duongtai.syndiary.entities.Diary;
import com.duongtai.syndiary.services.impl.DiaryServiceImpl;
import com.duongtai.syndiary.services.impl.StorageServiceImpl;
import com.duongtai.syndiary.services.impl.UserServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
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

    @GetMapping("image/{fileName:.+}")
    public ResponseEntity<byte[]> readFile (@PathVariable String fileName){
        return storageService.readFile(fileName);
    }
    @GetMapping("image/profile/{fileName:.+}")
    public ResponseEntity<byte[]> readProfileImage (@PathVariable String fileName){
        return storageService.readProfileImage(fileName);
    }

    @GetMapping("")
    public Page<Diary> getAllDiaryDisplayWithPagination(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "20") int size)
    {
        Pageable pageable = PageRequest.of(page,size);
        return diaryService.getAllDisplayDiary(pageable);
    }

}
