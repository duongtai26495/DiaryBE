package com.duongtai.syndiary.entities;

import javax.persistence.*;

@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @Column(unique = true)
    private String id;

    @Column(length = 2000)
    private String content;

    @Column(name = "created_at" ,updatable = false)
    private String created_at;

    private String last_edited;

    private boolean display;

    private String parent_id = "";

    private String sub_parent_id = "";
    @ManyToOne()
    @JoinColumn(name = "author", referencedColumnName = "id")
    private User author;

    private String diary_id;

    public Comment() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getLast_edited() {
        return last_edited;
    }

    public void setLast_edited(String last_edited) {
        this.last_edited = last_edited;
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }


    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getDiary_id() {
        return diary_id;
    }

    public void setDiary_id(String diary_id) {
        this.diary_id = diary_id;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getSub_parent_id() {
        return sub_parent_id;
    }

    public void setSub_parent_id(String sub_parent_id) {
        this.sub_parent_id = sub_parent_id;
    }
}
