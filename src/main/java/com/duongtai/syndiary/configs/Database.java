package com.duongtai.syndiary.configs;

import com.duongtai.syndiary.entities.Role;
import com.duongtai.syndiary.entities.User;
import com.duongtai.syndiary.repositories.UserRepository;
import com.duongtai.syndiary.services.impl.RoleServiceImpl;
import com.duongtai.syndiary.services.impl.UserServiceImpl;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class Database {
    @Bean
    CommandLineRunner initDatabase(RoleServiceImpl roleService, UserRepository userRepository, UserServiceImpl userService, PasswordEncoder passwordEncoder){
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                Role role_user = new Role(Snippets.ROLE_USER);
                Role role_admin = new Role(Snippets.ROLE_ADMIN);
                roleService.saveNewRole(role_user);
                roleService.saveNewRole(role_admin);
                
                User user = new User();
                user.setFull_name("Master admin");
                user.setUsername("master9981");
                user.setEmail("master9981@gmail.com");
                user.setPassword(passwordEncoder.encode("Blackhat9981"));
                user.setGender(1);
                user.setProfile_image("default_admin.jpg");
                user.setRole(role_admin);
                user.setActive(true);
                if(userService.findByUsername(user.getUsername()) == null) {
                userRepository.save(user);
                }
//
//                    Test test = new Test(1, userService);
//                    Test test2 = new Test(2,userService);
//                    Thread myThread = new Thread(test);
//                    Thread myThread2 = new Thread(test2);
//                    myThread.start();
//                    myThread2.start();
//                    try {
//                        myThread.join();
//                        myThread2.join();
//                    }catch (InterruptedException e){
//
//                    }

            }
        };
    }

    public class Test extends Thread{
        private int threadNumber;
        private UserServiceImpl userService;
        public Test(int threadNumber, UserServiceImpl userService){
            this.threadNumber = threadNumber;
            this.userService = userService;
        }

        @Override
        public void run() {
            List<User> users = new ArrayList<>();
            for(int i = 0; i<1000; i++){
                User user = new User();
                user.setUsername("taiduong00"+i+"-thread-"+threadNumber);
                user.setFull_name("Tai Duong 00"+i+"-thread-"+threadNumber);
                user.setGender(1);
                user.setProfile_image("default_admin.jpg");
                user.setEmail("taiduong00"+i+"@gmail.com"+"-thread-"+threadNumber);
                user.setPassword("Blackhat1");
                users.add(user);
            }
            for(User user : users){
                System.out.println("Create user: "+user.getUsername()+"/Thread: "+threadNumber);
                userService.saveUser(user);
            }
        }
    }
}
