package com.duongtai.syndiary.services.impl;

import com.duongtai.syndiary.configs.Snippets;
import com.duongtai.syndiary.entities.Image;
import com.duongtai.syndiary.repositories.ImageRepository;
import com.duongtai.syndiary.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.duongtai.syndiary.configs.MyUserDetail.getUsernameLogin;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageRepository imageRepository;
    
    
    @Override
    public List<Image> getAllImage() {
        return imageRepository.findAll();
    }

    @Override
    public void deleteImageById(Long id) {
        if(imageRepository.existsById(id)){
            imageRepository.deleteById(id);
        }
    }



	@Override
	public String saveImageWithName(String name) {
		  if(name != null){
	            Image image = new Image();
	            Date date = new Date();
	            SimpleDateFormat sdf = new SimpleDateFormat(Snippets.TIME_PATTERN);
	            image.setAdded_at(sdf.format(date));
	            image.setAdded_by(getUsernameLogin());
	            image.setName(name);
	            imageRepository.save(image);
	           return image.getName();
	        }
	        return null;
	}
}
