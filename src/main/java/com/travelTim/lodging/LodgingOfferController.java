package com.travelTim.lodging;

import com.travelTim.business.BusinessDaySchedule;
import com.travelTim.offer.OfferStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.Set;

@Controller
@RequestMapping("/lodging")
public class LodgingOfferController {

    private final LodgingOfferService lodgingOfferService;

    @Autowired
    public LodgingOfferController(LodgingOfferService lodgingOfferService) {
        this.lodgingOfferService = lodgingOfferService;
    }

    @PostMapping("/physical")
    public ResponseEntity<Long> addPhysicalPersonLodgingOffer (
            @RequestBody PhysicalPersonLodgingOfferEntity lodgingOffer){
        Long offerId = this.lodgingOfferService.addPhysicalPersonLodgingOffer(lodgingOffer);
        return new ResponseEntity<>(offerId, HttpStatus.CREATED);
    }

    @PostMapping("/legal")
    public ResponseEntity<Long> addLegalPersonLodgingOffer (
            @RequestBody LegalPersonLodgingOfferEntity lodgingOffer){
        Long offerId = this.lodgingOfferService.addLegalPersonLodgingOffer(lodgingOffer);
        return new ResponseEntity<>(offerId, HttpStatus.CREATED);
    }

    @GetMapping(path = "/legal/{offerId}/business/schedule")
    public ResponseEntity<Set<BusinessDaySchedule>> getBusinessScheduleForLegalLodgingOffer(
            @PathVariable("offerId") Long offerId){
        Set<BusinessDaySchedule> schedule = this.lodgingOfferService.getBusinessScheduleForLegalLodgingOffer(offerId);
        return new ResponseEntity<>(schedule, HttpStatus.OK);
    }

    @GetMapping(path = "/physical/{offerId}")
    public ResponseEntity<PhysicalPersonLodgingOfferDetailsDTO> findPhysicalPersonLodgingOfferById(
            @PathVariable("offerId") Long offerId) {
        PhysicalPersonLodgingOfferDetailsDTO offer = this.lodgingOfferService.findPhysicalPersonLodgingOfferById(offerId);
        return new ResponseEntity<>(offer, HttpStatus.OK);
    }

    @GetMapping(path = "/physical/edit/get/{offerId}")
    public ResponseEntity<PhysicalPersonLodgingOfferEditDTO> findPhysicalPersonLodgingOfferForEdit(
            @PathVariable("offerId") Long offerId) {
        PhysicalPersonLodgingOfferEditDTO offer = this.lodgingOfferService
                        .findPhysicalPersonLodgingOfferForEdit(offerId);
        return new ResponseEntity<>(offer, HttpStatus.OK);
    }

    @PutMapping(path = "/physical/{offerId}")
    public ResponseEntity<Void> editPhysicalPersonLodgingOffer(
            @RequestBody PhysicalPersonLodgingOfferEditDTO offerToSave,
            @PathVariable("offerId") Long offerId){
        this.lodgingOfferService.editPhysicalPersonLodgingOffer(offerToSave, offerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/legal/{offerId}")
    public ResponseEntity<LegalPersonLodgingOfferBaseDetailsDTO> findLegalPersonLodgingOfferById(
            @PathVariable("offerId") Long offerId){
        LegalPersonLodgingOfferBaseDetailsDTO offer = this.lodgingOfferService.findLegalPersonLodgingOfferById(offerId);
        return new ResponseEntity<>(offer, HttpStatus.OK);
    }

    @PutMapping(path = "/legal/{offerId}")
    public ResponseEntity<Void> editLegalPersonLodgingOffer(
            @RequestBody LegalPersonLodgingOfferEditDTO offerToSave,
            @PathVariable("offerId") Long offerId){
        this.lodgingOfferService.editLegalPersonLodgingOffer(offerToSave, offerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/legal/edit/get/{offerId}")
    public ResponseEntity<LegalPersonLodgingOfferEditDTO> findLegalPersonLodgingOfferForEdit(
            @PathVariable("offerId") Long offerId){
        LegalPersonLodgingOfferEditDTO offer = this.lodgingOfferService.findLegalPersonLodgingOfferForEdit(offerId);
        return new ResponseEntity<>(offer, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{offerId}")
    public ResponseEntity<Void> deleteLodgingOffer(@PathVariable("offerId") Long offerId){
        this.lodgingOfferService.deleteLodgingOffer(offerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(path = "/{offerId}/status/change")
    public ResponseEntity<Void> changeLodgingOfferStatus(
            @PathVariable("offerId") Long offerId,
            @RequestBody OfferStatus status){
        this.lodgingOfferService.changeLodgingOfferStatus(offerId, status);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/statistics")
    public ResponseEntity<LodgingOffersStatistics> getLodgingOffersStatistics(){
        LodgingOffersStatistics statistics = this.lodgingOfferService.getLodgingOffersStatistics();
        return new ResponseEntity<>(statistics, HttpStatus.OK);
    }

    @PostMapping("/price/request")
    public ResponseEntity<Void> addRequestedLodgingOfferPrice (
            @RequestBody LodgingOfferRequestedPrice requestedPrice){
        this.lodgingOfferService.addRequestedLodgingOfferPrice(requestedPrice);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
