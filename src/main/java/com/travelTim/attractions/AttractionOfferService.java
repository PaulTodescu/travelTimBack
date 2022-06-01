package com.travelTim.attractions;

import com.travelTim.category.CategoryEntity;
import com.travelTim.category.CategoryService;
import com.travelTim.category.CategoryType;
import com.travelTim.contact.OfferContactDAO;
import com.travelTim.contact.OfferContactEntity;
import com.travelTim.favourites.FavouriteOffersEntity;
import com.travelTim.files.ImageUtils;
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
public class AttractionOfferService {

    private final AttractionOfferDAO attractionOfferDAO;
    private final TicketDAO ticketDAO;
    private final OfferContactDAO offerContactDAO;
    private final UserService userService;
    private final CategoryService categoryService;
    private final ImageUtils imageUtils;

    @Autowired
    public AttractionOfferService(AttractionOfferDAO attractionOfferDAO, TicketDAO ticketDAO,
                                  OfferContactDAO offerContactDAO, UserService userService,
                                  CategoryService categoryService, ImageUtils imageUtils) {
        this.attractionOfferDAO = attractionOfferDAO;
        this.ticketDAO = ticketDAO;
        this.offerContactDAO = offerContactDAO;
        this.userService = userService;
        this.categoryService = categoryService;
        this.imageUtils = imageUtils;
    }

    public Long addAttractionOffer(AttractionOfferEntity attractionOffer){
        UserEntity loggedInUser = this.userService.findLoggedInUser();
        attractionOffer.setUser(loggedInUser);
        attractionOffer.setStatus(OfferStatus.active);
        CategoryEntity category = this.categoryService.findCategoryByName(CategoryType.attractions);
        attractionOffer.setCategory(category);
        attractionOffer.setNrViews(0L);
        this.addAttractionOfferTickets(attractionOffer, attractionOffer.getTickets());
        return this.attractionOfferDAO.save(attractionOffer).getId();
    }

    public void addAttractionOfferTickets(AttractionOfferEntity offer,
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
        this.attractionOfferDAO.save(offer);
    }

    public void addContactDetails(Long offerId, OfferContactEntity offerContact){
        AttractionOfferEntity offer = this.findAttractionOfferById(offerId);
        this.setContactDetails(offer, offerContact);
    }

    public void editContactDetails(Long offerId, OfferContactEntity offerContact){
        AttractionOfferEntity offer = this.findAttractionOfferById(offerId);
        OfferContactEntity initialOfferContact = offer.getOfferContact();
        if (!initialOfferContact.equals(offerContact)) {
            this.deleteOfferContact(offer, initialOfferContact);
        }
        this.setContactDetails(offer, offerContact);
    }

    public void setContactDetails(AttractionOfferEntity offer, OfferContactEntity offerContact){
        Optional<OfferContactEntity> contactOptional = this.offerContactDAO
                .findOfferContactEntityByEmailAndPhoneNumber(
                        offerContact.getEmail(),
                        offerContact.getPhoneNumber());
        if (contactOptional.isPresent()) {
            offer.setOfferContact(contactOptional.get());
        } else {
            offer.setOfferContact(this.offerContactDAO.save(offerContact));
        }
        this.attractionOfferDAO.save(offer);
    }

    public OfferContactEntity getContactDetails(Long offerId) {
        AttractionOfferEntity offer = this.findAttractionOfferById(offerId);
        return offer.getOfferContact();
    }

    public void deleteOfferContact(AttractionOfferEntity offer, OfferContactEntity offerContact) {
        if (offerContact != null) {
            offer.setOfferContact(null);
            offerContact.getAttractionOffers().remove(offer);
            if (offerContact.getLodgingOffers().size() == 0 &&
                    offerContact.getFoodOffers().size() == 0 &&
                    offerContact.getAttractionOffers().size() == 0 &&
                    offerContact.getActivityOffers().size() == 0) {
                this.offerContactDAO.deleteOfferContactEntityById(offerContact.getId());
            }
            this.attractionOfferDAO.save(offer);
        }
    }

    public AttractionOfferEntity findAttractionOfferById(Long offerId){
        return this.attractionOfferDAO.findAttractionsOfferEntityById(offerId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Attraction offer with id: " + offerId + " was not found")
        );
    }

    public List<AttractionOfferEntity> findAllAttractionOffers() {
        return this.attractionOfferDAO.findAll();
    }

    public AttractionOfferDetailsDTO getAttractionOfferDetails(Long offerId){
        AttractionOfferEntity offer = this.attractionOfferDAO.findAttractionsOfferEntityById(offerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Attraction offer with id: " + offerId + " was not found")
                );
        offer.setNrViews(offer.getNrViews() + 1);
        AttractionDTOMapper mapper = new AttractionDTOMapper();
        return mapper.mapAttractionOfferToDetailsDTO(offer);
    }

    public AttractionOfferEditDTO findAttractionOfferForEdit(Long offerId){
        AttractionOfferEntity offer = this.findAttractionOfferById(offerId);
        AttractionDTOMapper mapper = new AttractionDTOMapper();
        return mapper.mapAttractionOfferToEditDTO(offer);
    }

    public void editAttractionOffer(Long offerId, AttractionOfferEditDTO offerToSave){
        AttractionOfferEntity offer = this.findAttractionOfferById(offerId);
        offer.setBusiness(offerToSave.getBusiness());
        offer.setTitle(offerToSave.getTitle());
        offer.setAddress(offerToSave.getAddress());
        offer.setCity(offerToSave.getCity());
        offer.setDescription(offerToSave.getDescription());
        this.addAttractionOfferTickets(offer, offerToSave.getTickets());
        this.attractionOfferDAO.save(offer);
    }

    public void removeAttractionOfferFromFavorites(AttractionOfferEntity offer){
        for (Iterator<FavouriteOffersEntity> iterator = offer.getFavourites().iterator(); iterator.hasNext();){
            FavouriteOffersEntity favourites = iterator.next();
            favourites.getAttractionOffers().remove(offer);
            iterator.remove();
        }
        this.attractionOfferDAO.save(offer);
    }

    public void deleteAttractionOffer(Long offerId){
        AttractionOfferEntity offer = this.findAttractionOfferById(offerId);
        this.deleteOfferTickets(offer.getId());
        this.deleteOfferContact(offer, offer.getOfferContact());
        this.removeAttractionOfferFromFavorites(offer);
        this.imageUtils.deleteOfferImages("attractions", offerId);
        this.attractionOfferDAO.deleteAttractionOfferEntityById(offerId);
    }

    public void deleteOfferTickets(Long offerId){
        AttractionOfferEntity offer = this.findAttractionOfferById(offerId);
        for (Iterator<TicketEntity> iterator = offer.getTickets().iterator(); iterator.hasNext();){
            TicketEntity ticket = iterator.next();
            offer.removeTicket(ticket);
            if (ticket.getAttractionOffers().size() == 0 && ticket.getActivityOffers().size() == 0){
                this.ticketDAO.deleteTicketEntityById(ticket.getId());
            }
            iterator.remove();
        }
    }

    public void changeAttractionOfferStatus(Long offerId, OfferStatus status) {
        AttractionOfferEntity offer = this.findAttractionOfferById(offerId);
        offer.setStatus(status);
        this.attractionOfferDAO.save(offer);
    }

    public AttractionOffersStatistics getAttractionOffersStatistics() {
        List<AttractionOfferEntity> offers = this.findAllAttractionOffers();
        Double averageOffersViews = offers.stream()
                .collect(Collectors.averagingDouble(AttractionOfferEntity::getNrViews));
        Double averageOffersTicketsPrice = this.getAverageOffersTicketsPrices(offers);

        Set<AttractionOfferEntity> userOffers = this.userService.findLoggedInUser().getAttractionOffers();
        Double averageUserOffersViews = userOffers.stream()
                .collect(Collectors.averagingDouble(AttractionOfferEntity::getNrViews));
        Double averageUserOffersTicketsPrice = this.getAverageOffersTicketsPrices(new ArrayList<>(userOffers));

        return new AttractionOffersStatistics(averageOffersViews, averageUserOffersViews,
                averageOffersTicketsPrice, averageUserOffersTicketsPrice);
    }

    public Double getAverageOffersTicketsPrices(List<AttractionOfferEntity> offers) {
        List<TicketEntity> tickets = new ArrayList<>();
        for (AttractionOfferEntity offer: offers) {
            tickets.addAll(offer.getTickets());
        }
        return tickets.stream().collect(Collectors.averagingDouble(TicketEntity::getPrice));
    }


}
