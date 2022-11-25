package com.duongtai.syndiary.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.duongtai.syndiary.entities.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long>{
	
}
