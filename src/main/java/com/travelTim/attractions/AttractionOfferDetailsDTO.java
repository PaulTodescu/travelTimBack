package com.travelTim.attractions;

import com.travelTim.business.BusinessDTOMapper;
import com.travelTim.business.BusinessDetailsDTO;
import com.travelTim.business.BusinessEntity;
import com.travelTim.location.City;
import com.travelTim.offer.OfferStatus;
import com.travelTim.ticket.TicketEntity;
import com.travelTim.user.UserDTOMapper;
import com.travelTim.user.UserDetailsDTO;
import com.travelTim.user.UserEntity;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AttractionOfferDetailsDTO {
    private Long id;
    private String title;
    private String address;
    private City city;
    private String description;
    private OfferStatus status;
    private Long nrViews;
    private BusinessEntity business;
    private Set<TicketEntity> tickets;
    private UserEntity user;

    public AttractionOfferDetailsDTO() {
    }

    public AttractionOfferDetailsDTO(Long id, String title, String address, City city,
                                     String description, OfferStatus status, Long nrViews,
                                     BusinessEntity business, Set<TicketEntity> tickets, UserEntity user) {
        this.id = id;
        this.title = title;
        this.address = address;
        this.city = city;
        this.description = description;
        this.status = status;
        this.nrViews = nrViews;
        this.business = business;
        this.tickets = tickets;
        this.user = user;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BusinessDetailsDTO getBusiness() {
        if (this.business != null) {
            BusinessDTOMapper mapper = new BusinessDTOMapper();
            return mapper.mapBusinessToDetailsDTO(this.business);
        }
        return null;
    }

    public void setBusiness(BusinessEntity business) {
        this.business = business;
    }

    public Set<TicketEntity> getTickets() {
        return tickets.stream()
                .sorted(Comparator.comparing(TicketEntity::getName))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public void setTickets(Set<TicketEntity> tickets) {
        this.tickets = tickets;
    }

    public UserDetailsDTO getUser() {
        UserDTOMapper mapper = new UserDTOMapper();
        return mapper.mapUserToUserDetailsDTO(this.user);
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public OfferStatus getStatus() {
        return status;
    }

    public Long getNrViews() {
        return nrViews;
    }

    public void setNrViews(Long nrViews) {
        this.nrViews = nrViews;
    }

    public void setStatus(OfferStatus status) {
        this.status = status;
    }
}
