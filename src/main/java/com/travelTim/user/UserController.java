package com.travelTim.user;

import com.travelTim.activities.ActivityOfferBaseDetailsDTO;
import com.travelTim.activities.ActivityOfferEntity;
import com.travelTim.activities.ActivityOfferForBusinessPageDTO;
import com.travelTim.attractions.AttractionOfferBaseDetailsDTO;
import com.travelTim.attractions.AttractionOfferEntity;
import com.travelTim.attractions.AttractionOfferForBusinessPageDTO;
import com.travelTim.business.BusinessEntity;
import com.travelTim.food.FoodOfferBaseDetailsDTO;
import com.travelTim.food.FoodOfferEntity;
import com.travelTim.lodging.LodgingOfferBaseDetailsDTO;
import com.travelTim.lodging.LodgingOfferDTO;
import com.travelTim.lodging.LodgingOfferEntity;
import com.travelTim.lodging.PhysicalPersonLodgingOfferDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/register")
    public ResponseEntity<UserEntity> adduser(@RequestBody UserEntity user) throws IOException {
        userService.addUser(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<List<UserEntity>> getAllUsers(){
        List<UserEntity> users = userService.findAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping(path = "/{userId}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable("userId") Long userId){
        UserEntity user = userService.findUserById(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping()
    @ResponseBody
    public String getBeforeLoggedInUsername(@RequestParam String email){
        return userService.findUserByEmail(email).getFirstName();
    }

    @GetMapping(path = "/name")
    @ResponseBody
    public String getLoggedInUserName(){
        return userService.findLoggedInUserName();
    }


    @GetMapping(path = "/info")
    public ResponseEntity<UserEntity> getLoggedInUser(){
        UserEntity user = this.userService.findLoggedInUser();
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping(path = "/{userId}/details")
    public ResponseEntity<UserDTO> getUserDetailsById(@PathVariable("userId") Long userId){
        UserDTO user = userService.getUserDetailsById(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<UserEntity> updateUser(@RequestBody UserEntity user){
        this.userService.updateUser(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/phone")
    public ResponseEntity<UserEntity> updateUserPhoneNumber(@RequestBody(required = false) String phoneNumber){
        System.out.println("received phone: " + phoneNumber);
        this.userService.updateUserPhoneNumber(phoneNumber);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteUser(){
        this.userService.deleteUser();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/id")
    public ResponseEntity<Long> getLoggedInUserId(){
        Long id = this.userService.findLoggedInUser().getId();
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @GetMapping("/businesses")
    public ResponseEntity<List<BusinessEntity>> getAllBusinessesForCurrentUser(){
        List<BusinessEntity> businesses = this.userService.getAllBusinessesForCurrentUser();
        return new ResponseEntity<>(businesses, HttpStatus.OK);
    }

    @GetMapping("/offers/lodging")
    public ResponseEntity<List<LodgingOfferBaseDetailsDTO>> getAllLodgingOffersForCurrentUser() throws IOException {
        List<LodgingOfferBaseDetailsDTO> lodgingOffers = this.userService.getAllLodgingOffersForCurrentUser();
        return new ResponseEntity<>(lodgingOffers, HttpStatus.OK);
    }

    @GetMapping("/offers/food")
    public ResponseEntity<List<FoodOfferBaseDetailsDTO>> getAllFoodOffersForCurrentUser(){
        List<FoodOfferBaseDetailsDTO> foodOffers = this.userService.getAllFoodOffersForCurrentUser();
        return new ResponseEntity<>(foodOffers, HttpStatus.OK);
    }

    @GetMapping("/offers/attraction")
    public ResponseEntity<List<AttractionOfferBaseDetailsDTO>> getAllAttractionOffersForCurrentUser(){
        List<AttractionOfferBaseDetailsDTO> attractionOffers = this.userService.getAllAttractionOffersForCurrentUser();
        return new ResponseEntity<>(attractionOffers, HttpStatus.OK);
    }

    @GetMapping("/offers/activity")
    public ResponseEntity<List<ActivityOfferBaseDetailsDTO>> getAllActivityOffersForCurrentUser(){
        List<ActivityOfferBaseDetailsDTO> activityOffers = this.userService.getAllActivityOffersForCurrentUser();
        return new ResponseEntity<>(activityOffers, HttpStatus.OK);
    }

    @GetMapping("/{userId}/offers/lodging")
    public ResponseEntity<List<PhysicalPersonLodgingOfferDTO>> getLodgingOffersForUserPage(
            @PathVariable("userId") Long userId) throws IOException {
        List<PhysicalPersonLodgingOfferDTO> lodgingOffers = this.userService.getLodgingOffersForUserPage(userId);
        return new ResponseEntity<>(lodgingOffers, HttpStatus.OK);
    }

    @GetMapping("/{userId}/offers/attractions")
    public ResponseEntity<List<AttractionOfferForBusinessPageDTO>> getAttractionForUserPage(
            @PathVariable("userId") Long userId) {
        List<AttractionOfferForBusinessPageDTO> lodgingOffers = this.userService.getAttractionOffersForUserPage(userId);
        return new ResponseEntity<>(lodgingOffers, HttpStatus.OK);
    }

    @GetMapping("/{userId}/offers/activities")
    public ResponseEntity<List<ActivityOfferForBusinessPageDTO>> getActivityOffersForUserPage(
            @PathVariable("userId") Long userId) {
        List<ActivityOfferForBusinessPageDTO> lodgingOffers = this.userService.getActivityOffersForUserPage(userId);
        return new ResponseEntity<>(lodgingOffers, HttpStatus.OK);
    }


}
