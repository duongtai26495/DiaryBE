package com.duongtai.syndiary.controllers;

import com.duongtai.syndiary.configs.Snippets;
import com.duongtai.syndiary.entities.Comment;
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

import java.util.List;

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

    @PutMapping("update/{id}")
    public ResponseEntity updateDiary(@PathVariable Long id, @RequestBody Diary diary){
        if (diaryService.findDiaryById(id).getAuthor().getUsername().equalsIgnoreCase(getUsernameLogin())){
            diary.setId(id);
            return  ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(Snippets.SUCCESS,Snippets.DIARY_EDITED,
                            diaryService.updateDiaryById(diary))
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                new ResponseObject(Snippets.FAILED,Snippets.YOU_DONT_HAVE_PERMISSION, null));
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity deleteById(@PathVariable Long id){
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
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(Snippets.SUCCESS, Snippets.DIARY_FOUND, diaryService.findByAuthor(username))
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                new ResponseObject(Snippets.FAILED,Snippets.YOU_DONT_HAVE_PERMISSION, null));
    }

    @GetMapping("public/{id}")
    public Diary getDiaryById (@PathVariable Long id){
        return diaryService.findDiaryById(id);
    }

    @GetMapping("comment/{id}")
    public List<Comment> getAllComment (@PathVariable Long id){
        return diaryService.loadCommentByDiaryId(id);
    }

    @PostMapping("comment/add")
    public ResponseEntity addNewComment (@RequestBody Comment comment){
        comment.setAuthor(userService.findByUsername(getUsernameLogin()));
        try {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(Snippets.SUCCESS, Snippets.COMMENT_ADDED, diaryService.addComment(comment))
            );
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject(Snippets.FAILED, String.format(Snippets.ADD_NEW_COMMENT_FAILED,e.getMessage()), null)
            );
        }
    }

    @PutMapping("comment/update")
    public ResponseEntity updateComment (@RequestBody Comment comment){
        if(getUsernameLogin().equalsIgnoreCase(comment.getAuthor().getUsername())){
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(Snippets.SUCCESS, Snippets.COMMENT_UPDATED, diaryService.updateCommentById(comment))
            );
        }else{
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject(Snippets.FAILED, Snippets.UPDATE_COMMENT_FAILED, null)
            );
        }
    }
}
