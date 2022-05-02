package com.travelTim.activities;

import com.travelTim.attractions.AttractionOfferEditDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/activity")
public class ActivityOfferController {

    private final ActivityOfferService activityOfferService;

    @Autowired
    public ActivityOfferController(ActivityOfferService activityOfferService) {
        this.activityOfferService = activityOfferService;
    }

    @PostMapping
    public ResponseEntity<Long> addActivityOffer(@RequestBody ActivityOfferEntity activityOffer){
        Long activityId = this.activityOfferService.addActivityOffer(activityOffer);
        return new ResponseEntity<>(activityId, HttpStatus.CREATED);
    }

    @GetMapping("/{offerId}")
    public ResponseEntity<ActivityOfferEntity> findActivityOfferById(
            @PathVariable("offerId") Long offerId) {
        ActivityOfferEntity offer = this.activityOfferService.findActivityOfferById(offerId);
        return new ResponseEntity<>(offer, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{offerId}")
    public ResponseEntity<?> deleteActivityOffer(@PathVariable("offerId") Long offerId){
        this.activityOfferService.deleteActivityOffer(offerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/edit/get/{offerId}")
    public ResponseEntity<ActivityOfferEditDTO> findActivityOfferForEdit(
            @PathVariable("offerId") Long offerId){
        ActivityOfferEditDTO offer = this.activityOfferService.findActivityOfferForEdit(offerId);
        return new ResponseEntity<>(offer, HttpStatus.OK);
    }

    @PutMapping(path = "/{offerId}")
    public ResponseEntity<Void> editActivityOffer(
            @PathVariable("offerId") Long offerId,
            @RequestBody ActivityOfferEditDTO offerToSave){
        this.activityOfferService.editActivityOffer(offerId, offerToSave);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
