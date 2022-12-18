package com.duongtai.syndiary.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @Column(name = "id", unique = true)
    private String id;

    private String name;

    @ManyToMany(mappedBy = "categories")
    @JsonIgnore
    private List<Diary> diaries = new ArrayList<>();

    private boolean display;

    public Category() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public List<Diary> getDiaries() {
        return diaries;
    }

    public void setDiaries(List<Diary> diaries) {
        this.diaries = diaries;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }
}
