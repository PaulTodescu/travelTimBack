package com.travelTim.lodging;

import com.travelTim.currency.Currency;
import com.travelTim.location.City;
import com.travelTim.offer.OfferStatus;
import com.travelTim.user.UserDTOMapper;
import com.travelTim.user.UserDetailsDTO;
import com.travelTim.user.UserEntity;

import java.util.Set;

public class PhysicalPersonLodgingOfferDetailsDTO {

    private Long id;
    private String title;
    private String address;
    private City city;
    private Integer nrRooms;
    private Integer nrBathrooms;
    private Integer nrSingleBeds;
    private Integer nrDoubleBeds;
    private Integer floor;
    private Float price;
    private Currency currency;
    private String description;
    private Set<LodgingOfferUtilityEntity> utilities;
    private UserEntity user;
    private Long nrViews;
    private OfferStatus status;

    public PhysicalPersonLodgingOfferDetailsDTO() {
    }

    public PhysicalPersonLodgingOfferDetailsDTO(Long id, String title, String address,
                                                City city, Integer nrRooms, Integer nrBathrooms,
                                                Integer nrSingleBeds, Integer nrDoubleBeds,
                                                Integer floor, Float price, Currency currency,
                                                String description, Set<LodgingOfferUtilityEntity> utilities,
                                                UserEntity user, Long nrViews, OfferStatus status) {
        this.id = id;
        this.title = title;
        this.address = address;
        this.city = city;
        this.nrRooms = nrRooms;
        this.nrBathrooms = nrBathrooms;
        this.nrSingleBeds = nrSingleBeds;
        this.nrDoubleBeds = nrDoubleBeds;
        this.floor = floor;
        this.price = price;
        this.currency = currency;
        this.description = description;
        this.utilities = utilities;
        this.user = user;
        this.nrViews = nrViews;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getNrRooms() {
        return nrRooms;
    }

    public void setNrRooms(Integer nrRooms) {
        this.nrRooms = nrRooms;
    }

    public Integer getNrBathrooms() {
        return nrBathrooms;
    }

    public void setNrBathrooms(Integer nrBathrooms) {
        this.nrBathrooms = nrBathrooms;
    }

    public Integer getNrSingleBeds() {
        return nrSingleBeds;
    }

    public void setNrSingleBeds(Integer nrSingleBeds) {
        this.nrSingleBeds = nrSingleBeds;
    }

    public Integer getNrDoubleBeds() {
        return nrDoubleBeds;
    }

    public void setNrDoubleBeds(Integer nrDoubleBeds) {
        this.nrDoubleBeds = nrDoubleBeds;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<LodgingOfferUtilityEntity> getUtilities() {
        return utilities;
    }

    public void setUtilities(Set<LodgingOfferUtilityEntity> utilities) {
        this.utilities = utilities;
    }

    public UserDetailsDTO getUser() {
        UserDTOMapper mapper = new UserDTOMapper();
        return mapper.mapUserToUserDetailsDTO(user);
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Long getNrViews() {
        return nrViews;
    }

    public void setNrViews(Long nrViews) {
        this.nrViews = nrViews;
    }

    public OfferStatus getStatus() {
        return status;
    }

    public void setStatus(OfferStatus status) {
        this.status = status;
    }
}
