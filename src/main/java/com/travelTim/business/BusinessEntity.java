package com.travelTim.business;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.travelTim.activities.ActivityOfferEntity;
import com.travelTim.attractions.AttractionOfferEntity;
import com.travelTim.food.FoodOfferEntity;
import com.travelTim.location.City;
import com.travelTim.lodging.LegalPersonLodgingOfferEntity;
import com.travelTim.review.ReviewEntity;
import com.travelTim.user.UserEntity;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "business")
public class BusinessEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private City city;

    @Column(nullable = false)
    private String address;

    private String email;
    private String phoneNumber;
    private String websiteLink;
    private String facebookLink;
    private String twitterLink;

    @ManyToOne
    @JoinColumn(columnDefinition = "user_id")
    @JsonIgnore
    private UserEntity user;

    @OneToMany(mappedBy = "business")
    @JsonIgnore
    private Set<LegalPersonLodgingOfferEntity> lodgingOffers = new HashSet<>();

    @OneToOne(mappedBy = "business")
    @JsonIgnore
    private FoodOfferEntity foodOffer;

    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<AttractionOfferEntity> attractionOffers = new HashSet<>();

    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ActivityOfferEntity> activityOffers = new HashSet<>();

    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ReviewEntity> reviews = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "business_schedule",
            joinColumns = {@JoinColumn(name = "business_id")},
            inverseJoinColumns = {@JoinColumn(name = "day_schedule_id")}
    )
    private Set<BusinessDaySchedule> schedule = new HashSet<>();

    public BusinessEntity() {
    }

    public BusinessEntity(String name, City city, String address, String email,
                          String phoneNumber, String websiteLink, String facebookLink,
                          String twitterLink, UserEntity user) {
        this.name = name;
        this.city = city;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.websiteLink = websiteLink;
        this.facebookLink = facebookLink;
        this.twitterLink = twitterLink;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getWebsiteLink() {
        return websiteLink;
    }

    public void setWebsiteLink(String websiteLink) {
        this.websiteLink = websiteLink;
    }

    public String getFacebookLink() {
        return facebookLink;
    }

    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }

    public String getTwitterLink() {
        return twitterLink;
    }

    public void setTwitterLink(String twitterLink) {
        this.twitterLink = twitterLink;
    }

    public Set<LegalPersonLodgingOfferEntity> getLodgingOffers() {
        return lodgingOffers;
    }

    public void setLodgingOffers(Set<LegalPersonLodgingOfferEntity> lodgingOffers) {
        this.lodgingOffers = lodgingOffers;
    }

    public FoodOfferEntity getFoodOffer() {
        return foodOffer;
    }

    public void setFoodOffer(FoodOfferEntity foodOffer) {
        this.foodOffer = foodOffer;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Set<AttractionOfferEntity> getAttractionOffers() {
        return attractionOffers;
    }

    public void setAttractionOffers(Set<AttractionOfferEntity> attractionOffers) {
        this.attractionOffers = attractionOffers;
    }

    public Set<ActivityOfferEntity> getActivityOffers() {
        return activityOffers;
    }

    public void setActivityOffers(Set<ActivityOfferEntity> activityOffers) {
        this.activityOffers = activityOffers;
    }

    public Set<BusinessDaySchedule> getSchedule() {
        return schedule.stream()
                .sorted(Comparator.comparing(
                        daySchedule -> List.of("Monday","Tuesday","Wednesday", "Thursday",
                        "Friday","Saturday","Sunday")
                                .indexOf(daySchedule.getDay().toString())))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public void setSchedule(Set<BusinessDaySchedule> schedule) {
        this.schedule = schedule;
    }

    public void removeSchedule(BusinessDaySchedule daySchedule){
        this.schedule.remove(daySchedule);
        daySchedule.getBusinesses().remove(this);
    }

    public Set<ReviewEntity> getReviews() {
        return reviews;
    }

    public void setReviews(Set<ReviewEntity> reviews) {
        this.reviews = reviews;
    }
}
