package com.duongtai.syndiary.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
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
	PasswordEncoder passwordEncoder;

	@Autowired
	private CategoryRepository categoryRepository;
    @GetMapping("profile/{username}")
    public ResponseEntity<ResponseObject> getUserByUsername(@PathVariable(name = "username") String username){
    	User user = userService.findByUsername(username);
    	UserDTO userDTO = ConvertEntity.convertToDTO(user);
		return ResponseEntity.status(HttpStatus.OK).body(
				new ResponseObject(Snippets.SUCCESS, Snippets.USER_FOUND, userDTO));
	}

    @PostMapping("register")
    public ResponseEntity<ResponseObject> createUser (@RequestBody User user){

    	if(	user.getFull_name().isEmpty() ||
			user.getUsername().isEmpty() ||
			user.getEmail().isEmpty() ||
			user.getPassword().isEmpty() ||
			user.getGender() < 0 ||
			user.getGender() > 3) {
			return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
					new ResponseObject(Snippets.FAILED, Snippets.NOT_NULL, null));
		}
		if (userService.findByUsername(user.getUsername()) != null ||
			userService.findByEmail(user.getEmail()) != null){
			return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
					new ResponseObject(Snippets.FAILED, Snippets.EMAIL_ALREADY_TAKEN + " or "+ Snippets.USERNAME_ALREADY_TAKEN, null));
		}
		user = userService.saveUser(user);
		UserDTO userDTO = ConvertEntity.convertToDTO(user);
		System.out.println("User "+userDTO.getUsername() +" created.");
		return ResponseEntity.status(HttpStatus.OK).body(
				new ResponseObject(Snippets.SUCCESS,Snippets.USER_CREATE_SUCCESSFULLY, userDTO));

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

	@GetMapping("active/{code}")
	public ResponseEntity<ResponseObject> activeAccount (@PathVariable("code") String code){
		if(code != null){
			User user = userService.findByActiveCode(code);

			if(user == null || user.getActive()){
				return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
						new ResponseObject(Snippets.FAILED, Snippets.ACCOUNT_ACTIVE_FAILED,null)
				);
			}

			user.setActive(true);
			userService.changeActiveUser(user);

			return ResponseEntity.status(HttpStatus.OK).body(
					new ResponseObject(Snippets.SUCCESS, Snippets.ACCOUNT_ACTIVE_SUCCESS,null)
			);
		}
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
				new ResponseObject(Snippets.FAILED, Snippets.ACCOUNT_ACTIVE_FAILED,null)
		);
	}

	@GetMapping("check_active")
	public ResponseEntity<ResponseObject> check_active () {
		return null;
	}
}
