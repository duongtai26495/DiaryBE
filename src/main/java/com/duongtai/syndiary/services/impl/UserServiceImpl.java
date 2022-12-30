package com.duongtai.syndiary.services.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.duongtai.syndiary.configs.MailSender;
import com.duongtai.syndiary.configs.MyUserDetail;
import com.duongtai.syndiary.configs.Snippets;
import com.duongtai.syndiary.entities.*;
import com.duongtai.syndiary.repositories.UserRepository;
import com.duongtai.syndiary.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.GrantedAuthority;
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
import java.util.stream.Collectors;

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

    @Autowired
    private MailSender mailSender;

    private static final String ROLE_USER = Snippets.ROLE_USER;

    public UserServiceImpl() {

    }

    @Override
    public User findByUsername(String username) {
    	return userRepository.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
    	return userRepository.findByEmail(email);
    }

    private void sendEmail (
            String to,
            String content
    ) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("diarygroup.base2022@gmail.com");
        message.setTo(to);
        message.setSubject(Snippets.ACCOUNT_CREATED_SUCCESS);
        message.setText(content);
        mailSender.getJavaMailSender().send(message);
    }

    private String generateToken(String username, String email, String active_token){
        Algorithm algorithm = Algorithm.HMAC256(Snippets.SECRET_CODE.getBytes());
        String token = JWT.create()
                .withSubject(username)
                .withClaim("token",active_token)
                .withIssuer(email)
                .sign(algorithm);
        return token;
    }

    @Override
    public synchronized User saveUser(User user) {

        if (findByEmail(user.getEmail()) != null || findByUsername(user.getUsername()) != null){
            return null;
        }

        user.setId(UUID.randomUUID().toString());
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(Snippets.TIME_PATTERN);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(false);
        user.setJoined_at(sdf.format(date));
        user.setLast_edited(sdf.format(date));
        List<Role> roles = new ArrayList<>();
        roles.add(roleService.getRoleByName(Snippets.ROLE_USER));
        user.setRoles(roles);
        String active_token = generateToken(user.getEmail(), user.getUsername(), UUID.randomUUID().toString());
        user.setActive_token(active_token);

        User doneUser = userRepository.save(user);
        userRepository.save(doneUser);
        String url = "http://localhost:3000/active/key="+active_token;
        sendEmail(user.getEmail(), "You can click on this url: "+url+" to active account and login. Thank you");
        return doneUser;
    }

    @Override
    public User getUserById(String id) {
        return userRepository.findById(id).get();
    }

    @Override
    public synchronized User editByUsername(User user) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(Snippets.TIME_PATTERN);
        User getUser = userRepository.findByUsername(user.getUsername());

        if(user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            if (!user.getPassword().equals(getUser.getPassword())) {
                getUser.setPassword(user.getPassword());
            }
        }

        if(user.getProfile_image() != null && !user.getProfile_image().equals(getUser.getProfile_image())){
            getUser.setProfile_image(user.getProfile_image());
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
    public synchronized void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            try {
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
                        .withClaim(Snippets.ROLES,user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
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
        List<Role> roles = userRepository.findByUsername(getUsernameLogin()).getRoles();

        if(roles.stream().anyMatch(roleService.getRoleByName(Snippets.ROLE_ADMIN)::equals)){
                User foundUser = userRepository.findByUsername(user.getUsername());
                foundUser.setRoles(user.getRoles());
                return userRepository.save(foundUser);
        }
        return null;
    }

    @Override
    public User changeActiveUser(User user) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(Snippets.TIME_PATTERN);
        user.setLast_edited(sdf.format(date));
        return userRepository.save(user);
    }

    @Override
    public User findByActiveCode(String code) {
        return userRepository.findUserByActiveCode(code);
    }

}
