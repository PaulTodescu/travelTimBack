package com.travelTim.activities;

import com.travelTim.attractions.AttractionOffersStatistics;
import com.travelTim.contact.OfferContactEntity;
import com.travelTim.offer.OfferStatus;
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

    @PutMapping(path = "/{offerId}/contact/add")
    public ResponseEntity<Void> addOfferContact(
            @PathVariable("offerId") Long offerId,
            @RequestBody OfferContactEntity offerContact){
        this.activityOfferService.addContactDetails(offerId, offerContact);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(path = "/{offerId}/contact/edit")
    public ResponseEntity<Void> editOfferContact(
            @PathVariable("offerId") Long offerId,
            @RequestBody OfferContactEntity offerContact){
        this.activityOfferService.editContactDetails(offerId, offerContact);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/{offerId}/contact")
    public ResponseEntity<OfferContactEntity> getOfferContact(
            @PathVariable("offerId") Long offerId){
        OfferContactEntity contact = this.activityOfferService.getContactDetails(offerId);
        return new ResponseEntity<>(contact, HttpStatus.OK);
    }


    @GetMapping("/{offerId}")
    public ResponseEntity<ActivityOfferEntity> findActivityOfferById(
            @PathVariable("offerId") Long offerId) {
        ActivityOfferEntity offer = this.activityOfferService.findActivityOfferById(offerId);
        return new ResponseEntity<>(offer, HttpStatus.OK);
    }

    @GetMapping(path = "/{offerId}/details")
    public ResponseEntity<ActivityOfferDetailsDTO> getActivityOfferDetails(
            @PathVariable("offerId") Long offerId) {
        ActivityOfferDetailsDTO offer = this.activityOfferService.getActivityOfferDetails(offerId);
        return new ResponseEntity<>(offer, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{offerId}")
    public ResponseEntity<Void> deleteActivityOffer(@PathVariable("offerId") Long offerId){
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

    @PutMapping(path = "/{offerId}/status/change")
    public ResponseEntity<Void> changeActivityOfferStatus(
            @PathVariable("offerId") Long offerId,
            @RequestBody OfferStatus status){
        this.activityOfferService.changeActivityOfferStatus(offerId, status);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/statistics")
    public ResponseEntity<ActivityOffersStatistics> getActivityOffersStatistics(){
        ActivityOffersStatistics statistics = this.activityOfferService.getActivityOffersStatistics();
        return new ResponseEntity<>(statistics, HttpStatus.OK);
    }
}
