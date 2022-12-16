package com.duongtai.syndiary.controllers;

import com.duongtai.syndiary.configs.Snippets;
import com.duongtai.syndiary.entities.Comment;
import com.duongtai.syndiary.entities.Diary;
import com.duongtai.syndiary.entities.ResponseObject;
import com.duongtai.syndiary.entities.User;
import com.duongtai.syndiary.services.impl.DiaryServiceImpl;
import com.duongtai.syndiary.services.impl.StorageServiceImpl;
import com.duongtai.syndiary.services.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.duongtai.syndiary.configs.MyUserDetail.getUsernameLogin;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

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

    @PutMapping("update")
    public ResponseEntity updateDiary(@RequestBody Diary diary){
        if (diaryService.findDiaryById(diary.getId()).getAuthor().getUsername().equalsIgnoreCase(getUsernameLogin())){
            return  ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(Snippets.SUCCESS,Snippets.DIARY_EDITED,
                            diaryService.updateDiaryById(diary))
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                new ResponseObject(Snippets.FAILED,Snippets.YOU_DONT_HAVE_PERMISSION, null));
    }

    @DeleteMapping("delete")
    public ResponseEntity deleteById(@RequestParam String id){
        Diary diary = diaryService.findDiaryById(id);
        if(diary == null){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject(Snippets.FAILED,Snippets.DIARY_NOT_FOUND, null));
        }
        if(diary.getAuthor().getUsername().equalsIgnoreCase(getUsernameLogin())){
            diaryService.deleteDiaryById(id);
            return  ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(Snippets.SUCCESS,Snippets.DIARY_DELETED, null)
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                new ResponseObject(Snippets.FAILED,Snippets.YOU_DONT_HAVE_PERMISSION, null));
    }

    @GetMapping("author={username}")
    public ResponseEntity getDiaryByAuthor (@PathVariable String username){
        if(username.equalsIgnoreCase(getUsernameLogin())){
            Pageable pageable = PageRequest.of(0, 10);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(Snippets.SUCCESS, Snippets.DIARY_FOUND, diaryService.getAllDiaryByAuthor(username, pageable))
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                new ResponseObject(Snippets.FAILED,Snippets.YOU_DONT_HAVE_PERMISSION, null));
    }


    @GetMapping("id={diary_id}")
    public void getDiaryById (@PathVariable String diary_id, HttpServletResponse response) throws IOException {
        if(diaryService.findDiaryById(diary_id) != null){
            Map<String, Object> result = new HashMap<>();
            result.put("diary",diaryService.findDiaryById(diary_id));
            result.put("comment",diaryService.loadCommentByDiaryId(diary_id));
            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), result);
        }
    }

}
