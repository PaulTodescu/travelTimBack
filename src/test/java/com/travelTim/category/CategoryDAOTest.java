package com.travelTim.category;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class CategoryDAOTest {

    @Autowired
    private CategoryDAO categoryDAO;

    @Test
    public void findAllCategories() {
        categoryDAO.save(new CategoryEntity("lodging"));
        List<CategoryEntity> categories = categoryDAO.findAll();
        assertEquals(1, categories.size());
    }

    @Test
    public void saveAndRetrieveCategory() {
        CategoryEntity category = categoryDAO.save(new CategoryEntity("lodging"));
        CategoryEntity foundCategory = categoryDAO.findCategoryEntityById(category.getId()).orElse(null);
        assertNotNull(foundCategory);
        assertEquals(category.getName(), foundCategory.getName());
    }

}
