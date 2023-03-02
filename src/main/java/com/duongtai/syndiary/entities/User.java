package com.duongtai.syndiary.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "User")
public class User {
	
	enum GENDER {
		MALE, FEMALE, UNKNOWN;
	}

    @Id
    @Column(name = "id", unique = true)
    private String id;

    @Column(name = "full_name")
    private String full_name;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "username", unique = true)
    private String username;

    @JsonIgnore
    @JsonSetter
    private String password;

    @Column(name = "joined_at", updatable = false)
    private String joined_at;

    private String last_edited;

    private boolean active;

    private int gender;

    @JsonIgnore
    public String active_token ;
    @Column(length = 1000)
    private String profile_image;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_user",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;

    @JsonIgnore
    @OneToMany(targetEntity = Comment.class, mappedBy = "author",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Comment> comments;

    @OneToMany(targetEntity = Diary.class, mappedBy = "author",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Diary> diaries;

    public User() {
    }


    public String getActive_token() {
        return active_token;
    }

    public void setActive_token(String active_token) {
        this.active_token = active_token;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getJoined_at() {
        return joined_at;
    }

    public void setJoined_at(String joined_at) {
        this.joined_at = joined_at;
    }

    public String getLast_edited() {
        return last_edited;
    }

    public void setLast_edited(String last_edited) {
        this.last_edited = last_edited;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getActive() {
        return active;
    }

    public List<Diary> getDiaries() {
        return diaries;
    }

    public void setDiaries(List<Diary> diaries) {
        this.diaries = diaries;
    }

    public void setActive(boolean active) {
		this.active = active;
	}

	public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
