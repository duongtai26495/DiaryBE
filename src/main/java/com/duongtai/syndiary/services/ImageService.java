package com.duongtai.syndiary.services;

import com.duongtai.syndiary.entities.Image;

import java.util.List;

public interface ImageService {
    String saveImageWithName(String name);
    
    List<Image> getAllImage();

    void deleteImageById(Long id);

}
