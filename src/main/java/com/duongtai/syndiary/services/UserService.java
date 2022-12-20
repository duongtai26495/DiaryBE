package com.duongtai.syndiary.services;

import com.duongtai.syndiary.entities.User;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface UserService {

    User findByUsername(String username);

    User findByEmail(String email);

    User saveUser(User user);

    User getUserById(String id);

    User editByUsername(User user);

    User changeProfileImage(User user);


    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;

	
	List<User> findAllUser();

    User changeRoleUser(User user);

    User changeActiveUser(User user);

    User findByActiveCode(String code);
}
