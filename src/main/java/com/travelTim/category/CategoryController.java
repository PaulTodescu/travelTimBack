package com.travelTim.category;

import com.travelTim.activities.ActivityOfferDTO;
import com.travelTim.activities.ActivityOfferEntity;
import com.travelTim.attractions.AttractionOfferDTO;
import com.travelTim.attractions.AttractionOfferEntity;
import com.travelTim.food.FoodOfferDTO;
import com.travelTim.food.FoodOfferEntity;
import com.travelTim.lodging.LodgingOfferDTO;
import com.travelTim.lodging.LodgingOfferEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping(path = "/category")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping(path = "/all")
    public ResponseEntity<List<CategoryDTO>> getAllCategories(){
        List<CategoryDTO> categories = categoryService.findAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping(path = "/{categoryId}")
    public ResponseEntity<CategoryEntity> getCategoryById(@PathVariable("categoryId") Long categoryId){
        CategoryEntity category = this.categoryService.findCategoryById(categoryId);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @GetMapping("/offers/lodging")
    public ResponseEntity<Set<LodgingOfferDTO>> getAllLodgingOffers() throws IOException {
        Set<LodgingOfferDTO> lodgingOffers = this.categoryService.getLodgingOffers();
        return new ResponseEntity<>(lodgingOffers, HttpStatus.OK);
    }

    @GetMapping("/offers/food")
    public ResponseEntity<Set<FoodOfferDTO>> getAllFoodOffers(){
        Set<FoodOfferDTO> foodOffers = this.categoryService.getFoodOffers();
        return new ResponseEntity<>(foodOffers, HttpStatus.OK);
    }

    @GetMapping("/offers/attractions")
    public ResponseEntity<Set<AttractionOfferDTO>> getAllAttractionOffers(){
        Set<AttractionOfferDTO> attractionOffers = this.categoryService.getAttractionOffers();
        return new ResponseEntity<>(attractionOffers, HttpStatus.OK);
    }

    @GetMapping("/offers/activities")
    public ResponseEntity<Set<ActivityOfferDTO>> getAllActivityOffers(){
        Set<ActivityOfferDTO> activityOffers = this.categoryService.getActivityOffers();
        return new ResponseEntity<>(activityOffers, HttpStatus.OK);
    }

}
