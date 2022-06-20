package com.travelTim.contact;

import com.travelTim.activities.ActivityOfferDAO;
import com.travelTim.activities.ActivityOfferEntity;
import com.travelTim.activities.ActivityOfferService;
import com.travelTim.attractions.AttractionOfferDAO;
import com.travelTim.attractions.AttractionOfferEntity;
import com.travelTim.attractions.AttractionOfferService;
import com.travelTim.food.FoodOfferDAO;
import com.travelTim.food.FoodOfferEntity;
import com.travelTim.food.FoodOfferService;
import com.travelTim.lodging.LodgingOfferDAO;
import com.travelTim.lodging.LodgingOfferEntity;
import com.travelTim.lodging.LodgingOfferService;
import com.travelTim.offer.OfferCategory;
import org.springframework.stereotype.Service;

@Service
public class ContactService {

    private final LodgingOfferService lodgingOfferService;
    private final LodgingOfferDAO lodgingOfferDAO;
    private final FoodOfferService foodOfferService;
    private final FoodOfferDAO foodOfferDAO;
    private final AttractionOfferService attractionOfferService;
    private final AttractionOfferDAO attractionOfferDAO;
    private final ActivityOfferService activityOfferService;
    private final ActivityOfferDAO activityOfferDAO;

    public ContactService(LodgingOfferService lodgingOfferService, LodgingOfferDAO lodgingOfferDAO,
                          FoodOfferService foodOfferService, FoodOfferDAO foodOfferDAO,
                          AttractionOfferService attractionOfferService, AttractionOfferDAO attractionOfferDAO,
                          ActivityOfferService activityOfferService, ActivityOfferDAO activityOfferDAO) {
        this.lodgingOfferService = lodgingOfferService;
        this.lodgingOfferDAO = lodgingOfferDAO;
        this.foodOfferService = foodOfferService;
        this.foodOfferDAO = foodOfferDAO;
        this.attractionOfferService = attractionOfferService;
        this.attractionOfferDAO = attractionOfferDAO;
        this.activityOfferService = activityOfferService;
        this.activityOfferDAO = activityOfferDAO;
    }


    public void setContactDetails(Long offerId, OfferCategory offerCategory, OfferContact offerContact) {
        String email = offerContact.getEmail();
        String phoneNumber = offerContact.getPhoneNumber();
        switch (offerCategory) {
            case lodging:
                this.lodgingOfferDAO.setContact(email, phoneNumber, offerId);
                break;
            case food:
                this.foodOfferDAO.setContact(email, phoneNumber, offerId);
                break;
            case attractions:
                this.attractionOfferDAO.setContact(email, phoneNumber, offerId);
                break;
            case activities:
                this.activityOfferDAO.setContact(email, phoneNumber, offerId);
                break;
        }
    }

    public OfferContact getContactDetails(Long offerId, OfferCategory offerCategory) {
        OfferContact offerContact;
        switch (offerCategory) {
            case lodging:
                offerContact = this.getLodgingOfferContactDetails(offerId);
                break;
            case food:
                offerContact = this.getFoodOfferContactDetails(offerId);
                break;
            case attractions:
                offerContact = this.getAttractionOfferContactDetails(offerId);
                break;
            case activities:
                offerContact = this.getActivityOfferContactDetails(offerId);
                break;
            default:
                offerContact = new OfferContact();
        }
        return offerContact;
    }

    public OfferContact getLodgingOfferContactDetails(Long offerId) {
        LodgingOfferEntity offer = this.lodgingOfferService.findLodgingOfferEntityById(offerId);
        return new OfferContact(offer.getEmail(), offer.getPhoneNumber());
    }

    public OfferContact getFoodOfferContactDetails(Long offerId) {
        FoodOfferEntity offer = this.foodOfferService.findFoodOfferById(offerId);
        return new OfferContact(offer.getEmail(), offer.getPhoneNumber());
    }

    public OfferContact getAttractionOfferContactDetails(Long offerId) {
        AttractionOfferEntity offer = this.attractionOfferService.findAttractionOfferById(offerId);
        return new OfferContact(offer.getEmail(), offer.getPhoneNumber());
    }

    public OfferContact getActivityOfferContactDetails(Long offerId) {
        ActivityOfferEntity offer = this.activityOfferService.findActivityOfferById(offerId);
        return new OfferContact(offer.getEmail(), offer.getPhoneNumber());
    }
}
