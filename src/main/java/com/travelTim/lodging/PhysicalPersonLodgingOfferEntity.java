package com.travelTim.lodging;

import com.travelTim.currency.Currency;
import com.travelTim.location.City;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "physical_person_lodging_offer")
public class PhysicalPersonLodgingOfferEntity extends LodgingOfferEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private City city;

    public PhysicalPersonLodgingOfferEntity() {
    }

    public PhysicalPersonLodgingOfferEntity(Integer nrRooms, Integer nrBathrooms,
                                            Integer nrSingleBeds, Integer nrDoubleBeds, Integer floor, Float price,
                                            Currency currency, String description,
                                            Set<LodgingOfferUtilityEntity> utilities,
                                            String title, String address, City city) {
        super(nrRooms, nrBathrooms, nrSingleBeds, nrDoubleBeds, floor, price, currency, description, utilities);
        this.title = title;
        this.address = address;
        this.city = city;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "PhysicalPersonLodgingOfferEntity{" +
                "title='" + title + '\'' +
                ", address='" + address + '\'' +
                ", city=" + city +
                '}';
    }
}
