package com.travelTim.category;

import com.travelTim.config.CustomUserDetailsService;
import com.travelTim.config.JwtUtil;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.Arrays;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = CategoryController.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    public void findAllCategories() throws Exception {
        when(categoryService.findAllCategories()).thenReturn(
                Arrays.asList(
                        new CategoryDTO(1L, "lodging"),
                        new CategoryDTO(2L, "food"),
                        new CategoryDTO(3L, "attractions"),
                        new CategoryDTO(4L, "activities")
                )
        );
        RequestBuilder request = MockMvcRequestBuilders
                .get("/category/all")
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        JSONAssert.assertEquals(
                result.getResponse().getContentAsString(),
                "[{id:1,name:lodging},{id:2,name:food},{id:3,name:attractions},{id:4,name:activities}]",
                false
        );

    }

}
