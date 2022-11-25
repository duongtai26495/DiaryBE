package com.duongtai.syndiary.services.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.duongtai.syndiary.configs.MyUserDetail;
import com.duongtai.syndiary.configs.Snippets;
import com.duongtai.syndiary.entities.*;
import com.duongtai.syndiary.repositories.UserRepository;
import com.duongtai.syndiary.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.duongtai.syndiary.configs.MyUserDetail.getUsernameLogin;
import static com.duongtai.syndiary.configs.Snippets.EXPIRATION_TIME;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleServiceImpl roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String ROLE_USER = Snippets.ROLE_USER;
    public UserServiceImpl() {

    }


    @Override
    public User findByUsername(String username) {
    	User user = userRepository.findByUsername(username);
    	if( user != null) {
    		return user;
    	}
        return null;
    }

    @Override
    public User findByEmail(String email) {
    	User user = userRepository.findByEmail(email);
    	if( user != null) {
    		return user;
    	}
        return null;
    }

    @Override
    public User getUserByUsername(String username) {
        if (username.equals(getUsernameLogin()) && getUsernameLogin() != null){
            User user = userRepository.findByUsername(username);
            if(user!=null){
                return user;
            }
        }
		return null;
    }

    @Override
    public synchronized User saveUser(User user) {

        if (findByEmail(user.getEmail()) != null || findByUsername(user.getUsername()) != null){
            return null;
        }

        Role default_role_user = roleService.getRoleByName(ROLE_USER);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(Snippets.TIME_PATTERN);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);
        user.setJoined_at(sdf.format(date));
        user.setLast_edited(sdf.format(date));
        user.setRole(default_role_user);
        

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated()){
            LOG.info(String.format("User '%s' register successfully",
                    user.getUsername()));
        }else{
            System.out.println(Snippets.USER_DO_NOT_LOGIN);
        }
        
        return userRepository.save(user);
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id).get();
    }

    @Override
    public synchronized User editByUsername(User user) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(Snippets.TIME_PATTERN);
        User getUser = userRepository.findByUsername(user.getUsername());
        getUser.setId(getUser.getId());

        if(user.getProfile_image() != null && !user.getProfile_image().equals(getUser.getProfile_image())){
            getUser.setProfile_image(user.getProfile_image());
        }

        if(user.getActive() != getUser.getActive()) {
            getUser.setActive(!getUser.getActive());
        }

        if(user.getFull_name() != null){
            getUser.setFull_name(user.getFull_name());
        }

        if(user.getGender()>0 && user.getGender() <=2){
            getUser.setGender(user.getGender());
        }
        getUser.setLast_edited(sdf.format(date));
        
        return userRepository.save(getUser);
    }

    @Override
    public User changeProfileImage(User user) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(Snippets.TIME_PATTERN);
        User getUser = userRepository.findByUsername(user.getUsername());
        getUser.setLast_edited(sdf.format(date));
        getUser.setProfile_image(user.getProfile_image());
        return userRepository.save(getUser);
    }


    @Override
    public synchronized boolean updatePassword(String newPassword) {
        User user = userRepository.findByUsername(getUsernameLogin());
        if(user != null) {
            String passwordEncode = passwordEncoder.encode(newPassword);
            user.setPassword(passwordEncode);
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat(Snippets.TIME_PATTERN);
            user.setLast_edited(sdf.format(date));
            userRepository.save(user);
            return true;
        }
    return false;
    }

    @Override
    public synchronized void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            try {
//                tokenRepository.save(old_token);
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refresh_token);
                String username = decodedJWT.getSubject();
                User user = userRepository.findByUsername(username);
                String access_token = JWT.create()
                        .withSubject(user.getUsername())
                        .withIssuer(request.getRequestURL().toString())
                        .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                        .sign(algorithm);

                Map<String, String> tokens = new HashMap<>();
                tokens.put(Snippets.ACCESS_TOKEN,access_token);
                tokens.put(Snippets.REFRESH_TOKEN,refresh_token);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(),tokens);
            }catch (Exception exception){
                response.setHeader("error",exception.getMessage());
                response.setStatus(FORBIDDEN.value());
                //response.sendError(FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("error_message",exception.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(),error);
            }
        }else {
            throw new RuntimeException("Refesh token is missing");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        return new MyUserDetail(user);
    }


	@Override
	public List<User> findAllUser() {
		return userRepository.findAll();
	}

    @Override
    public User changeRoleUser(User user) {
        Role role = userRepository.findByUsername(getUsernameLogin()).getRole();
        if(role.getName().equals(Snippets.ROLE_ADMIN)){
                User foundUser = userRepository.findByUsername(user.getUsername());
                foundUser.setRole(user.getRole());
                return userRepository.save(foundUser);
        }
        return null;
    }

    @Override
    public User changeActiveUser(User user) {
        Role role = userRepository.findByUsername(getUsernameLogin()).getRole();
        if(role.getName().equals(Snippets.ROLE_ADMIN)){
                User foundUser = userRepository.findByUsername(user.getUsername());
                foundUser.setActive(user.getActive());
                return userRepository.save(foundUser);
        }
        return null;
    }

}
