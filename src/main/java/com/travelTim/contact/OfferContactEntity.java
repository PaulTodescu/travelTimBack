package com.travelTim.contact;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.travelTim.activities.ActivityOfferEntity;
import com.travelTim.attractions.AttractionOfferEntity;
import com.travelTim.food.FoodOfferEntity;
import com.travelTim.lodging.LodgingOfferEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "offer_contact")
public class OfferContactEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    private String phoneNumber;

    @OneToMany(mappedBy = "offerContact")
    @JsonIgnore
    private Set<LodgingOfferEntity> lodgingOffers = new HashSet<>();

    @OneToMany(mappedBy = "offerContact")
    @JsonIgnore
    private Set<FoodOfferEntity> foodOffers = new HashSet<>();

    @OneToMany(mappedBy = "offerContact")
    @JsonIgnore
    private Set<AttractionOfferEntity> attractionOffers = new HashSet<>();

    @OneToMany(mappedBy = "offerContact")
    @JsonIgnore
    private Set<ActivityOfferEntity> activityOffers = new HashSet<>();


    public OfferContactEntity() {
    }

    public OfferContactEntity(String email, String phoneNumber) {
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Set<AttractionOfferEntity> getAttractionOffers() {
        return attractionOffers;
    }

    public void setAttractionOffers(Set<AttractionOfferEntity> attractionOffers) {
        this.attractionOffers = attractionOffers;
    }

    public void addAttractionOffer(AttractionOfferEntity offer){
        this.attractionOffers.add(offer);
        offer.setOfferContact(this);
    }

    public Set<ActivityOfferEntity> getActivityOffers() {
        return activityOffers;
    }

    public void setActivityOffers(Set<ActivityOfferEntity> activityOffers) {
        this.activityOffers = activityOffers;
    }

    public Set<FoodOfferEntity> getFoodOffers() {
        return foodOffers;
    }

    public void setFoodOffers(Set<FoodOfferEntity> foodOffers) {
        this.foodOffers = foodOffers;
    }

    public Set<LodgingOfferEntity> getLodgingOffers() {
        return lodgingOffers;
    }

    public void setLodgingOffers(Set<LodgingOfferEntity> lodgingOffers) {
        this.lodgingOffers = lodgingOffers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OfferContactEntity that = (OfferContactEntity) o;
        return email.equals(that.email) && Objects.equals(phoneNumber, that.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, phoneNumber);
    }
}
