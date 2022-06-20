package com.travelTim.activities;

import com.travelTim.category.CategoryEntity;
import com.travelTim.category.CategoryService;
import com.travelTim.category.CategoryType;
import com.travelTim.contact.OfferContactDAO;
import com.travelTim.contact.OfferContactEntity;
import com.travelTim.favourites.FavouriteOffersEntity;
import com.travelTim.files.ImageService;
import com.travelTim.offer.OfferStatus;
import com.travelTim.ticket.TicketDAO;
import com.travelTim.ticket.TicketEntity;
import com.travelTim.user.UserEntity;
import com.travelTim.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ActivityOfferService {

    private final ActivityOfferDAO activityOfferDAO;
    private final UserService userService;
    private final TicketDAO ticketDAO;
    private final OfferContactDAO offerContactDAO;
    private final CategoryService categoryService;
    private final ImageService imageService;

    @Autowired
    public ActivityOfferService(ActivityOfferDAO activityOfferDAO, UserService userService,
                                TicketDAO ticketDAO, OfferContactDAO offerContactDAO,
                                CategoryService categoryService, ImageService imageService) {
        this.activityOfferDAO = activityOfferDAO;
        this.userService = userService;
        this.ticketDAO = ticketDAO;
        this.offerContactDAO = offerContactDAO;
        this.categoryService = categoryService;
        this.imageService = imageService;
    }

    public Long addActivityOffer(ActivityOfferEntity activityOffer) {
        UserEntity loggedInUser = this.userService.findLoggedInUser();
        activityOffer.setUser(loggedInUser);
        activityOffer.setStatus(OfferStatus.active);
        CategoryEntity category = this.categoryService.findCategoryByName(CategoryType.activities);
        activityOffer.setCategory(category);
        activityOffer.setNrViews(0L);
        this.addActivityOfferTickets(activityOffer, activityOffer.getTickets());
        return this.activityOfferDAO.save(activityOffer).getId();
    }

    public void addActivityOfferTickets(ActivityOfferEntity offer,
                                          Set<TicketEntity> ticketsToAdd){
        Set<TicketEntity> tickets = new HashSet<>();
        for (TicketEntity ticket: ticketsToAdd){
            TicketEntity ticketToSave;
            if (this.ticketDAO.findTicketEntityByNameAndPrice(ticket.getName(), ticket.getPrice()).isEmpty()){
                ticketToSave = this.ticketDAO.save(ticket);
            } else {
                ticketToSave = this.ticketDAO.findTicketEntityByNameAndPrice(ticket.getName(), ticket.getPrice()).get();
            }
            tickets.add(ticketToSave);
        }
        offer.setTickets(tickets);
        this.activityOfferDAO.save(offer);
    }

    public void addContactDetails(Long offerId, OfferContactEntity offerContact){
        ActivityOfferEntity offer = this.findActivityOfferById(offerId);
        this.setContactDetails(offer, offerContact);
    }

    public void editContactDetails(Long offerId, OfferContactEntity offerContact){
        ActivityOfferEntity offer = this.findActivityOfferById(offerId);
        OfferContactEntity initialOfferContact = offer.getOfferContact();
        if (!initialOfferContact.equals(offerContact)) {
            initialOfferContact.getActivityOffers().remove(offer);
            offer.setOfferContact(null);
            if (initialOfferContact.getAttractionOffers().size() == 0){
                this.offerContactDAO.deleteOfferContactEntityById(initialOfferContact.getId());
            }
        }
        this.setContactDetails(offer, offerContact);
    }

    public void setContactDetails(ActivityOfferEntity offer, OfferContactEntity offerContact){
        Optional<OfferContactEntity> contactOptional = this.offerContactDAO
                .findOfferContactEntityByEmailAndPhoneNumber(
                        offerContact.getEmail(),
                        offerContact.getPhoneNumber());
        if (contactOptional.isPresent()) {
            offer.setOfferContact(contactOptional.get());
        } else {
            offer.setOfferContact(this.offerContactDAO.save(offerContact));
        }
        this.activityOfferDAO.save(offer);
    }

    public OfferContactEntity getContactDetails(Long offerId) {
        ActivityOfferEntity offer = this.findActivityOfferById(offerId);
        return offer.getOfferContact();
    }

    public void deleteOfferContact(ActivityOfferEntity offer, OfferContactEntity offerContact) {
        if (offerContact != null) {
            offer.setOfferContact(null);
            offerContact.getActivityOffers().remove(offer);
            if (offerContact.getLodgingOffers().size() == 0 &&
                    offerContact.getFoodOffers().size() == 0 &&
                    offerContact.getAttractionOffers().size() == 0 &&
                    offerContact.getActivityOffers().size() == 0) {
                this.offerContactDAO.deleteOfferContactEntityById(offerContact.getId());
            }
            this.activityOfferDAO.save(offer);
        }
    }

    public ActivityOfferEntity findActivityOfferById(Long offerId) {
        return this.activityOfferDAO.findActivityOfferEntityById(offerId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Activity offer with id: " + offerId + " was not found")
        );
    }

    public List<ActivityOfferEntity> findAllActivityOffers() {
        return this.activityOfferDAO.findAll();
    }

    public ActivityOfferEditDTO findActivityOfferForEdit(Long offerId){
        ActivityOfferEntity offer = this.findActivityOfferById(offerId);
        ActivityDTOMapper mapper = new ActivityDTOMapper();
        return mapper.mapActivityOfferToEditDTO(offer);
    }

    public void editActivityOffer(Long offerId, ActivityOfferEditDTO offerToSave){
        ActivityOfferEntity offer = this.findActivityOfferById(offerId);
        offer.setBusiness(offerToSave.getBusiness());
        offer.setTitle(offerToSave.getTitle());
        offer.setAddress(offerToSave.getAddress());
        offer.setCity(offerToSave.getCity());
        offer.setDescription(offerToSave.getDescription());
        this.addActivityOfferTickets(offer, offerToSave.getTickets());
        this.activityOfferDAO.save(offer);
    }

    public void deleteOfferTickets(Long offerId){
        ActivityOfferEntity offer = this.findActivityOfferById(offerId);
        for (Iterator<TicketEntity> iterator = offer.getTickets().iterator(); iterator.hasNext();){
            TicketEntity ticket = iterator.next();
            offer.removeTicket(ticket);
            if (ticket.getActivityOffers().size() == 0 && ticket.getAttractionOffers().size() == 0){
                this.ticketDAO.deleteTicketEntityById(ticket.getId());
            }
            iterator.remove();
        }
    }

    public void removeActivityOfferFromFavorites(ActivityOfferEntity offer){
        for (Iterator<FavouriteOffersEntity> iterator = offer.getFavourites().iterator(); iterator.hasNext();){
            FavouriteOffersEntity favourites = iterator.next();
            favourites.getActivityOffers().remove(offer);
            iterator.remove();
        }
        this.activityOfferDAO.save(offer);
    }

    public ActivityOfferDetailsDTO getActivityOfferDetails(Long offerId){
        ActivityOfferEntity offer = this.activityOfferDAO.findActivityOfferEntityById(offerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Activity offer with id: " + offerId + " was not found")
                );
        offer.setNrViews(offer.getNrViews() + 1);
        ActivityDTOMapper mapper = new ActivityDTOMapper();
        return mapper.mapActivityOfferToDetailsDTO(offer);
    }

    public void deleteActivityOffer(Long offerId){
        ActivityOfferEntity offer = this.findActivityOfferById(offerId);
        this.deleteOfferTickets(offer.getId());
        this.deleteOfferContact(offer, offer.getOfferContact());
        this.removeActivityOfferFromFavorites(offer);
        this.imageService.deleteOfferImages("activities", offerId);
        this.activityOfferDAO.deleteActivityOfferEntityById(offerId);
    }

    public void changeActivityOfferStatus(Long offerId, OfferStatus status) {
        ActivityOfferEntity offer = this.findActivityOfferById(offerId);
        offer.setStatus(status);
        this.activityOfferDAO.save(offer);
    }

    public ActivityOffersStatistics getActivityOffersStatistics() {
        List<ActivityOfferEntity> offers = this.findAllActivityOffers();
        Double averageOffersViews = offers.stream()
                .collect(Collectors.averagingDouble(ActivityOfferEntity::getNrViews));
        Double averageOffersTicketsPrice = this.getAverageOffersTicketsPrices(offers);

        Set<ActivityOfferEntity> userOffers = this.userService.findLoggedInUser().getActivityOffers();
        Double averageUserOffersViews = userOffers.stream()
                .collect(Collectors.averagingDouble(ActivityOfferEntity::getNrViews));
        Double averageUserOffersTicketsPrice = this.getAverageOffersTicketsPrices(new ArrayList<>(userOffers));

        return new ActivityOffersStatistics(averageOffersViews, averageUserOffersViews,
                averageOffersTicketsPrice, averageUserOffersTicketsPrice);
    }

    public Double getAverageOffersTicketsPrices(List<ActivityOfferEntity> offers) {
        List<TicketEntity> tickets = new ArrayList<>();
        for (ActivityOfferEntity offer: offers) {
            tickets.addAll(offer.getTickets());
        }
        return tickets.stream().collect(Collectors.averagingDouble(TicketEntity::getPrice));
    }
}
