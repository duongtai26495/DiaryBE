package com.duongtai.syndiary.controllers;

import com.duongtai.syndiary.configs.Snippets;
import com.duongtai.syndiary.entities.*;
import com.duongtai.syndiary.services.impl.StorageServiceImpl;
import com.duongtai.syndiary.services.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.duongtai.syndiary.configs.MyUserDetail.getUsernameLogin;

@CrossOrigin
@RestController
@RequestMapping("/user/")
public class UserController {

    @Autowired
    StorageServiceImpl storageService;

    @Autowired
    UserServiceImpl userService;

    @GetMapping("profile/{username}")
    public ResponseEntity<ResponseObject> getUserByUsername(@PathVariable String username){
    	User user = userService.getUserByUsername(username);
    	UserDTO userDTO = ConvertEntity.convertToDTO(user);
		return ResponseEntity.status(HttpStatus.OK).body(
				new ResponseObject(Snippets.SUCCESS, Snippets.USER_FOUND, userDTO));
	}

    @PostMapping("register")
    public ResponseEntity<ResponseObject> createUser (@RequestBody User user){
    	
    	if(user != null){
    		user = userService.saveUser(user);
    		UserDTO userDTO = ConvertEntity.convertToDTO(user);
    		return ResponseEntity.status(HttpStatus.OK).body(
    				new ResponseObject(Snippets.SUCCESS,Snippets.USER_CREATE_SUCCESSFULLY, userDTO));
    	}
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
				new ResponseObject(Snippets.FAILED,Snippets.EMAIL_ALREADY_TAKEN +" or " + Snippets.USERNAME_ALREADY_TAKEN, null));
    }

    @PutMapping("edit/{username}")
    public ResponseEntity<ResponseObject> editByUsername(@PathVariable String username, @RequestBody User user){
        user.setUsername(username);
        if(userService.findByUsername(username) != null) {
        	UserDTO userDTO = ConvertEntity.convertToDTO(userService.editByUsername(user));
        	return ResponseEntity.status(HttpStatus.OK).body(
        			new ResponseObject(Snippets.SUCCESS, Snippets.USER_EDITED, userDTO)
        			);
        }
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
				new ResponseObject(Snippets.FAILED,Snippets.USER_NOT_FOUND, null));
    }

    @PutMapping("change_password")
    public ResponseEntity<ResponseObject> updatePasswordByUsername(@RequestBody User user){
    	if(userService.updatePassword(user.getPassword())) {
    		return ResponseEntity.status(HttpStatus.OK).body(
        			new ResponseObject(Snippets.SUCCESS, Snippets.PASSWORD_UPDATED, null)
        			);
    	}
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
    			new ResponseObject(Snippets.FAILED, Snippets.HAVE_ERROR, null)
    			);
    }

    @PostMapping("upload_image/{username}")
    public ResponseEntity<ResponseObject> uploadImageWithUsername(@RequestParam("image") MultipartFile file, @PathVariable String username){
    	String filename = "";
		if(username.equalsIgnoreCase(getUsernameLogin())){
			filename = storageService.storeFile(file, username);
			if(filename != null) {
				return ResponseEntity.status(HttpStatus.OK).body(
						new ResponseObject(Snippets.SUCCESS, Snippets.UPLOAD_PROFILE_IMAGE_SUCCESS, filename)
				);
			}
		}

    		return ResponseEntity.status(HttpStatus.OK).body(
	    			new ResponseObject(Snippets.FAILED, Snippets.STORE_FILE_FAILED, filename)
	    			);
    			
    }

    @PostMapping("upload_image")
    public ResponseEntity<ResponseObject> uploadImage(@RequestParam("image") MultipartFile file){
    	String filename = storageService.storeFile(file, "noname");
		if(filename != null) {
			return ResponseEntity.status(HttpStatus.OK).body(
	    			new ResponseObject(Snippets.SUCCESS, Snippets.UPLOAD_IMAGE_SUCCESS, filename)
	    			);
		}
		return ResponseEntity.status(HttpStatus.OK).body(
    			new ResponseObject(Snippets.FAILED, Snippets.STORE_FILE_FAILED, null)
    			);

    }

    @GetMapping("images/{username}")
    public ResponseEntity<byte[]> readUserImage (@PathVariable String username){
        return storageService.readProfileImage(username);
    }


    @GetMapping("refresh_token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        userService.refreshToken(request,response);
    }

}
