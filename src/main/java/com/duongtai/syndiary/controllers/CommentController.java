package com.duongtai.syndiary.controllers;

import com.duongtai.syndiary.configs.Snippets;
import com.duongtai.syndiary.entities.Comment;
import com.duongtai.syndiary.entities.ResponseObject;
import com.duongtai.syndiary.services.impl.DiaryServiceImpl;
import com.duongtai.syndiary.services.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.duongtai.syndiary.configs.MyUserDetail.getUsernameLogin;

@CrossOrigin
@RestController
@RequestMapping("/comment/")
public class CommentController {

    @Autowired
    private DiaryServiceImpl diaryService;

    @Autowired
    private UserServiceImpl userService;

    @GetMapping("id={id}")
    public Comment loadCommentById (@PathVariable String id){
        return diaryService.loadCommentWithId(id);
    }


    @PostMapping("add")
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

    @PutMapping("update")
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
