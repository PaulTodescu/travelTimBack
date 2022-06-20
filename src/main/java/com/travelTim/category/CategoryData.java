package com.travelTim.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CategoryData implements CommandLineRunner {

    private final CategoryDAO categoryDAO;

    @Autowired
    public CategoryData(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    @Override
    public void run(String... args) {

        saveCategory(new CategoryEntity("lodging"));
        saveCategory(new CategoryEntity("food"));
        saveCategory(new CategoryEntity("attractions"));
        saveCategory(new CategoryEntity("activities"));

    }

    public void saveCategory(CategoryEntity categoryEntity){
        if(categoryDAO.findCategoryEntityByName(categoryEntity.getName()).isEmpty()){
            categoryDAO.save(categoryEntity);
        }
    }
}
