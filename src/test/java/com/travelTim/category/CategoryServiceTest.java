package com.travelTim.category;


import com.travelTim.config.CustomUserDetailsService;
import com.travelTim.config.JwtUtil;
import com.travelTim.files.ImageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryDAO categoryDAO;

    @Mock
    private ImageService imageService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @Test
    public void findAllCategories() {
        when(categoryDAO.findAll()).thenReturn(
                new ArrayList<>(List.of(
                        new CategoryEntity("lodging"),
                        new CategoryEntity("food"),
                        new CategoryEntity("attractions"),
                        new CategoryEntity("activities")
                ))
        );
        List<CategoryDTO> categories = this.categoryService.findAllCategories();
        assertEquals("lodging", categories.get(0).getName());
        assertEquals("food", categories.get(1).getName());
        assertEquals("attractions", categories.get(2).getName());
        assertEquals("activities", categories.get(3).getName());
    }


}
