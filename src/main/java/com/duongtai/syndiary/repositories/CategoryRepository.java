package com.duongtai.syndiary.repositories;

import com.duongtai.syndiary.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(value = "SELECT c FROM Category c WHERE c.display = true")
    List<Category> loadAllCategory();

    @Query(value = "SELECT c FROM Category c WHERE c.name = :name")
    Category loadCategoryByName(@Param("name") String name);

    @Query(value = "SELECT c FROM Category c WHERE c.id = :id")
    Category loadCategoryById(@Param("id") String id);
}
