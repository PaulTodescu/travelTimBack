package com.travelTim.lodging;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.travelTim.business.BusinessEntity;
import com.travelTim.currency.Currency;

import javax.persistence.*;
import java.util.Set;


@Entity
@Table(name = "legal_person_lodging_offer")
public class LegalPersonLodgingOfferEntity extends LodgingOfferEntity {

    @ManyToOne()
    @JoinColumn(columnDefinition = "business_id", nullable = false)
    private BusinessEntity business;

    public LegalPersonLodgingOfferEntity() {
    }

    public LegalPersonLodgingOfferEntity(Integer nrRooms, Integer nrBathrooms, Integer nrSingleBeds, Integer nrDoubleBeds,
                                         Integer floor, Float price, Currency currency, String description, String email,
                                         String phoneNumber, BusinessEntity business, Set<LodgingOfferUtilityEntity> utilities) {
        super(nrRooms, nrBathrooms, nrSingleBeds, nrDoubleBeds, floor, price, currency, description, email, phoneNumber, utilities);
        this.business = business;
    }

    public BusinessEntity getBusiness() {
        return business;
    }

    public void setBusiness(BusinessEntity business) {
        this.business = business;
    }

}
