package com.duongtai.syndiary.repositories;

import com.duongtai.syndiary.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @Query("SELECT u FROM User u WHERE u.email = :email")
    User findByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.username = :username AND u.active = true")
    User findByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.active_token = :code")
    User findUserByActiveCode (@Param("code") String code);

    @Query("UPDATE User u SET u.active = true WHERE u.username = :username")
    User activeByUsername(@Param("username") String username);
}
