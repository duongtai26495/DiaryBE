package com.duongtai.syndiary.controllers;

import com.duongtai.syndiary.configs.Snippets;
import com.duongtai.syndiary.entities.*;
import com.duongtai.syndiary.repositories.CategoryRepository;
import com.duongtai.syndiary.services.impl.DiaryServiceImpl;
import com.duongtai.syndiary.services.impl.RoleServiceImpl;
import com.duongtai.syndiary.services.impl.StorageServiceImpl;
import com.duongtai.syndiary.services.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.duongtai.syndiary.configs.MyUserDetail.getUsernameLogin;

@CrossOrigin
@RestController
@RequestMapping("/user/")
public class UserController {

    @Autowired
    StorageServiceImpl storageService;

    @Autowired
    UserServiceImpl userService;

	@Autowired
	DiaryServiceImpl diaryService;

	@Autowired
	RoleServiceImpl roleService;
	@Autowired
	private CategoryRepository categoryRepository;
    @GetMapping("profile")
    public ResponseEntity<ResponseObject> getUserByUsername(@RequestParam(name = "username") String username){
    	User user = userService.findByUsername(username);
    	UserDTO userDTO = ConvertEntity.convertToDTO(user);
		return ResponseEntity.status(HttpStatus.OK).body(
				new ResponseObject(Snippets.SUCCESS, Snippets.USER_FOUND, userDTO));
	}

    @PostMapping("register")
    public ResponseEntity<ResponseObject> createUser (@RequestBody User user){
    	
    	if(user != null){
    		user = userService.saveUser(user);
    		UserDTO userDTO = ConvertEntity.convertToDTO(user);
			System.out.println("User "+userDTO.getUsername() +" created.");
    		return ResponseEntity.status(HttpStatus.OK).body(
    				new ResponseObject(Snippets.SUCCESS,Snippets.USER_CREATE_SUCCESSFULLY, userDTO));
    	}
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
				new ResponseObject(Snippets.FAILED,Snippets.EMAIL_ALREADY_TAKEN +" or " + Snippets.USERNAME_ALREADY_TAKEN, null));
    }

    @PutMapping("update")
    public ResponseEntity<ResponseObject> editByUsername(@RequestBody User user){
        if(userService.findByUsername(user.getUsername()) != null) {
        	UserDTO userDTO = ConvertEntity.convertToDTO(userService.editByUsername(user));
        	return ResponseEntity.status(HttpStatus.OK).body(
        			new ResponseObject(Snippets.SUCCESS, Snippets.USER_EDITED, userDTO)
        			);
        }
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
				new ResponseObject(Snippets.FAILED,Snippets.USER_NOT_FOUND, null));
    }

    @PostMapping("upload_image")
    public ResponseEntity<ResponseObject> uploadImageWithUsername(@RequestParam("image") MultipartFile file, @RequestParam(defaultValue = "") String username){
    	String filename = "";
		if(!username.equals("")){
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

		}else{
			filename = storageService.storeFile(file, "noname");
			if(filename != null) {
				return ResponseEntity.status(HttpStatus.OK).body(
						new ResponseObject(Snippets.SUCCESS, Snippets.UPLOAD_IMAGE_SUCCESS, filename)
				);
			}
			return ResponseEntity.status(HttpStatus.OK).body(
					new ResponseObject(Snippets.FAILED, Snippets.STORE_FILE_FAILED, null)
			);
		}

    }

	@GetMapping("diary")
	public Page<Diary> getAllDiaryByAuthor (){
		Pageable pageable = PageRequest.of(0,10);
		return diaryService.getAllDiaryByAuthor(getUsernameLogin(), pageable);
	}


    @GetMapping("refresh_token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        userService.refreshToken(request,response);
    }

	@PostMapping("category/new")
	public Category saveNew(@RequestBody Category category){
		User user = userService.findByUsername(getUsernameLogin());
		if(categoryRepository.loadCategoryByName(category.getName().toLowerCase()) == null
				&& user.getRoles().stream().anyMatch(roleService.getRoleByName(Snippets.ROLE_ADMIN)::equals)) {
			return diaryService.saveNewCategory(category);
		}
	return null;
	}

}
