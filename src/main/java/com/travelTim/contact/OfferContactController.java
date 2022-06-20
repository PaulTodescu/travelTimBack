package com.travelTim.contact;

import com.travelTim.offer.OfferCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/contact")
public class OfferContactController {

    private final ContactService contactService;

    @Autowired
    public OfferContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PutMapping(path = "/{offerCategory}/{offerId}")
    public ResponseEntity<Void> addOfferContact(
            @PathVariable("offerId") Long offerId,
            @PathVariable("offerCategory") OfferCategory offerCategory,
            @RequestBody OfferContact offerContact){
        this.contactService.setContactDetails(offerId, offerCategory, offerContact);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/{offerCategory}/{offerId}")
    public ResponseEntity<OfferContact> getOfferContact(
            @PathVariable("offerId") Long offerId,
            @PathVariable("offerCategory") OfferCategory offerCategory){
        OfferContact contact = this.contactService.getContactDetails(offerId, offerCategory);
        return new ResponseEntity<>(contact, HttpStatus.OK);
    }
}
