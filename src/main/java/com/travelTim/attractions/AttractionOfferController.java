package com.travelTim.attractions;

import com.travelTim.contact.OfferContactEntity;
import com.travelTim.food.FoodOffersStatistics;
import com.travelTim.offer.OfferStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/attraction")
public class AttractionOfferController {

    private final AttractionOfferService attractionOfferService;

    @Autowired
    public AttractionOfferController(AttractionOfferService attractionOfferService) {
        this.attractionOfferService = attractionOfferService;
    }

    @PostMapping()
    public ResponseEntity<Long> addAttractionOffer(@RequestBody AttractionOfferEntity attractionOffer){
        Long attractionId = this.attractionOfferService.addAttractionOffer(attractionOffer);
        return new ResponseEntity<>(attractionId, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{offerId}/contact/add")
    public ResponseEntity<Void> addOfferContact(
            @PathVariable("offerId") Long offerId,
            @RequestBody OfferContactEntity offerContact){
        this.attractionOfferService.addContactDetails(offerId, offerContact);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(path = "/{offerId}/contact/edit")
    public ResponseEntity<Void> editOfferContact(
            @PathVariable("offerId") Long offerId,
            @RequestBody OfferContactEntity offerContact){
        this.attractionOfferService.editContactDetails(offerId, offerContact);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/{offerId}/contact")
    public ResponseEntity<OfferContactEntity> getOfferContact(
            @PathVariable("offerId") Long offerId){
        OfferContactEntity contact = this.attractionOfferService.getContactDetails(offerId);
        return new ResponseEntity<>(contact, HttpStatus.OK);
    }

    @GetMapping(path = "/{offerId}")
    public ResponseEntity<AttractionOfferEntity> findAttractionOfferById(
            @PathVariable("offerId") Long offerId) {
        AttractionOfferEntity offer = this.attractionOfferService.findAttractionOfferById(offerId);
        return new ResponseEntity<>(offer, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{offerId}")
    public ResponseEntity<?> deleteAttractionOffer(@PathVariable("offerId") Long offerId){
        this.attractionOfferService.deleteAttractionOffer(offerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/{offerId}/details")
    public ResponseEntity<AttractionOfferDetailsDTO> getAttractionOfferDetails(
            @PathVariable("offerId") Long offerId) {
        AttractionOfferDetailsDTO offer = this.attractionOfferService.getAttractionOfferDetails(offerId);
        return new ResponseEntity<>(offer, HttpStatus.OK);
    }

    @GetMapping(path = "/edit/get/{offerId}")
    public ResponseEntity<AttractionOfferEditDTO> findAttractionOfferForEdit(
            @PathVariable("offerId") Long offerId){
        AttractionOfferEditDTO offer = this.attractionOfferService.findAttractionOfferForEdit(offerId);
        return new ResponseEntity<>(offer, HttpStatus.OK);
    }

    @PutMapping(path = "/{offerId}")
    public ResponseEntity<Void> editAttractionOffer(
            @PathVariable("offerId") Long offerId,
            @RequestBody AttractionOfferEditDTO offerToSave){
        this.attractionOfferService.editAttractionOffer(offerId, offerToSave);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(path = "/{offerId}/status/change")
    public ResponseEntity<Void> changeAttractionOfferStatus(
            @PathVariable("offerId") Long offerId,
            @RequestBody OfferStatus status){
        this.attractionOfferService.changeAttractionOfferStatus(offerId, status);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/statistics")
    public ResponseEntity<AttractionOffersStatistics> getAttractionOffersStatistics(){
        AttractionOffersStatistics statistics = this.attractionOfferService.getAttractionOffersStatistics();
        return new ResponseEntity<>(statistics, HttpStatus.OK);
    }

}
