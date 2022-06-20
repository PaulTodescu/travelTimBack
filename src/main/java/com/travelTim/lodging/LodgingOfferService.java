package com.travelTim.lodging;

import com.travelTim.business.BusinessDaySchedule;
import com.travelTim.business.BusinessEntity;
import com.travelTim.business.BusinessService;
import com.travelTim.category.CategoryService;
import com.travelTim.category.CategoryType;
import com.travelTim.currency.Currency;
import com.travelTim.currency.CurrencyConverter;
import com.travelTim.favourites.FavouriteOffersEntity;
import com.travelTim.favourites.FavouriteOffersService;
import com.travelTim.files.ImageService;
import com.travelTim.offer.OfferStatus;
import com.travelTim.user.UserEntity;
import com.travelTim.user.UserService;
import com.travelTim.category.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class LodgingOfferService {

    private final LodgingOfferDAO lodgingOfferDAO;
    private final LegalPersonLodgingOfferDAO legalPersonLodgingOfferDAO;
    private final PhysicalPersonLodgingOfferDAO physicalPersonLodgingOfferDAO;
    private final LodgingOfferUtilityDAO lodgingOfferUtilityDAO;
    private final UserService userService;
    private final CategoryService categoryService;
    private final ImageService imageService;
    private final BusinessService businessService;
    private final FavouriteOffersService favouriteOffersService;
    private final LodgingOfferRequestedPriceDAO requestedPriceDAO;

    @Autowired
    public LodgingOfferService(LodgingOfferDAO lodgingOfferDAO, LegalPersonLodgingOfferDAO legalPersonLodgingOfferDAO,
                               PhysicalPersonLodgingOfferDAO physicalPersonLodgingOfferDAO,
                               LodgingOfferUtilityDAO lodgingOfferUtilityDAO, UserService userService,
                               CategoryService categoryService, ImageService imageService,
                               @Lazy BusinessService businessService,
                               @Lazy FavouriteOffersService favouriteOffersService,
                               LodgingOfferRequestedPriceDAO requestedPriceDAO) {
        this.lodgingOfferDAO = lodgingOfferDAO;
        this.legalPersonLodgingOfferDAO = legalPersonLodgingOfferDAO;
        this.physicalPersonLodgingOfferDAO = physicalPersonLodgingOfferDAO;
        this.lodgingOfferUtilityDAO = lodgingOfferUtilityDAO;
        this.userService = userService;
        this.categoryService = categoryService;
        this.imageService = imageService;
        this.businessService = businessService;
        this.favouriteOffersService = favouriteOffersService;
        this.requestedPriceDAO = requestedPriceDAO;
    }

    public Long addPhysicalPersonLodgingOffer(PhysicalPersonLodgingOfferEntity lodgingOffer){
        UserEntity loggedInUser = this.userService.findLoggedInUser();
        lodgingOffer.setUser(loggedInUser);
        CategoryEntity category = this.categoryService.findCategoryByName(CategoryType.lodging);
        lodgingOffer.setCategory(category);
        lodgingOffer.setStatus(OfferStatus.active);
        lodgingOffer.setNrViews(0L);
        this.addLodgingOfferUtilities(lodgingOffer, lodgingOffer.getUtilities());
        return this.lodgingOfferDAO.save(lodgingOffer).getId();
    }

    public Long addLegalPersonLodgingOffer(LegalPersonLodgingOfferEntity lodgingOffer){
        UserEntity loggedInUser = this.userService.findLoggedInUser();
        lodgingOffer.setUser(loggedInUser);
        CategoryEntity category = this.categoryService.findCategoryByName(CategoryType.lodging);
        lodgingOffer.setCategory(category);
        lodgingOffer.setStatus(OfferStatus.active);
        lodgingOffer.setNrViews(0L);
        this.addLodgingOfferUtilities(lodgingOffer, lodgingOffer.getUtilities());
        LegalPersonLodgingOfferEntity offer = this.lodgingOfferDAO.save(lodgingOffer);
        this.addLodgingOfferToFavouritesIfNeeded(offer, offer.getBusiness().getId());
        return offer.getId();
    }

    public List<LodgingOfferEntity> findAllLodgingOffers(){
        return lodgingOfferDAO.findAll();
    }

    public LodgingOfferEntity findLodgingOfferEntityById(Long lodgingOfferId){
        return this.lodgingOfferDAO.findLodgingOfferEntityById(lodgingOfferId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Lodging offer with id: " + lodgingOfferId + " was not found")
        );
    }

    public void addLodgingOfferUtilities(LodgingOfferEntity lodgingOffer, Set<LodgingOfferUtilityEntity> lodgingOfferUtilities) {
        Set<LodgingOfferUtilityEntity> utilities = new HashSet<>();
        for (LodgingOfferUtilityEntity utility: lodgingOfferUtilities){
            if (this.lodgingOfferUtilityDAO.
                    findLodgingOfferUtilityEntityByName(utility.getName()).isEmpty()){
                this.lodgingOfferUtilityDAO.save(utility);
                utilities.add(utility);
            } else {
                LodgingOfferUtilityEntity lodgingOfferUtility =
                        this.lodgingOfferUtilityDAO
                                .findLodgingOfferUtilityEntityByName(utility.getName()).get();
                utilities.add(lodgingOfferUtility);
            }
        }
        lodgingOffer.setUtilities(utilities);
        this.lodgingOfferDAO.save(lodgingOffer);
    }

    public void deleteLodgingOfferUtilities(LodgingOfferEntity offer){
        for (Iterator<LodgingOfferUtilityEntity> iterator =
             offer.getUtilities().iterator(); iterator.hasNext();){
            LodgingOfferUtilityEntity utility = iterator.next();
            utility.getLodgingOffers().remove(offer);
            if (utility.getLodgingOffers().size() == 0){
                this.lodgingOfferUtilityDAO.deleteLodgingOfferUtilityEntityById(utility.getId());
            }
            iterator.remove();
        }
    }

    public LegalPersonLodgingOfferBaseDetailsDTO findLegalPersonLodgingOfferById(Long offerId) {
        LegalPersonLodgingOfferEntity offer =
                this.legalPersonLodgingOfferDAO.findLegalPersonLodgingOfferEntityById(offerId)
                        .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Lodging offer of type legal person with id: " + offerId + " was not found")
                        );
        for (LegalPersonLodgingOfferEntity legalOffer: offer.getBusiness().getLodgingOffers()) {
            legalOffer.setNrViews(offer.getNrViews() + 1);
        }
        LodgingDTOMapper mapper = new LodgingDTOMapper();
        return mapper.mapLegalLodgingOfferToBaseDetailsDTO(offer);
    }

    public LegalPersonLodgingOfferEditDTO findLegalPersonLodgingOfferForEdit(Long offerId) {
        LodgingOfferEntity offer =
                this.legalPersonLodgingOfferDAO.findLegalPersonLodgingOfferEntityById(offerId)
                        .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Lodging offer of type legal person with id: " + offerId + " was not found")
                        );
        LodgingDTOMapper mapper = new LodgingDTOMapper();
        return mapper.mapLodgingOfferToLegalPersonOfferEditDTO(offer);
    }

    public List<LodgingOfferRequestedPrice> findAllLodgingOfferRequestedPrices() {
        return this.requestedPriceDAO.findAll();
    }

//    public LodgingOfferDetailsDTO getLodgingOfferDetails(Long offerId){
//        LodgingOfferEntity offer = this.findLodgingOfferEntityById(offerId);
//        LodgingDTOMapper mapper = new LodgingDTOMapper();
//        return mapper.mapLodgingOfferToDetailsDTO(offer);
//    }

    public Set<BusinessDaySchedule> getBusinessScheduleForLegalLodgingOffer(Long offerId) {
        LodgingOfferEntity offer = this.findLodgingOfferEntityById(offerId);
        if (offer instanceof LegalPersonLodgingOfferEntity) {
            return ((LegalPersonLodgingOfferEntity) offer).getBusiness().getSchedule();
        }
        return new HashSet<>();
    }

    // if the user already added offers from same business,
    // then add new offers to favourites as well
    public void addLodgingOfferToFavouritesIfNeeded(
            LegalPersonLodgingOfferEntity offer, Long businessId){
        BusinessEntity business = this.businessService.findBusinessById(businessId);
        List<UserEntity> users = this.userService.findAllUsers();
        for (UserEntity user: users){
            if (checkIfUserAddedAnyOfferFromBusinessToFavourites(user, business.getLodgingOffers())) {
                // check if user has any legal offer to favourites
                if (!user.getFavourites().getLodgingOffers().contains(offer)) {
                    this.favouriteOffersService.addLodgingOfferToFavourites(user.getId(), offer);
                }
            }
        }
    }

    public boolean checkIfUserAddedAnyOfferFromBusinessToFavourites(
            UserEntity user, Set<LegalPersonLodgingOfferEntity> offers){
        for (LegalPersonLodgingOfferEntity offer: offers){
            if (user.getFavourites().getLodgingOffers().contains(offer)){
                return true;
            }
        }
        return false;
    }

    public LodgingOfferEntity findPhysicalPersonLodgingOfferById(Long offerId) {
        PhysicalPersonLodgingOfferEntity offer = this.physicalPersonLodgingOfferDAO
                .findPhysicalPersonLodgingOfferEntityById(offerId)
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Lodging offer of type physical person with id: " + offerId +
                                " was not found")
                );
        offer.setNrViews(offer.getNrViews() + 1);
        if (offer.getCurrency() == Currency.EUR){
             try {
            CurrencyConverter currencyConverter = new CurrencyConverter();
            //Float conversionRateFromEUR = currencyConverter
            //        .getCurrencyConversionRate(Currency.EUR.name(), Currency.RON.name());
            offer.setPrice(currencyConverter.getConvertedPrice(offer.getPrice(), 1F));
            offer.setCurrency(Currency.RON);
            } catch (Exception e) {}
             //catch (IOException ioException) {
//                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
//                        "Could not retrieve currency for monetary conversion");
 //           }
        }
        return offer;
    }

    public PhysicalPersonLodgingOfferEditDTO findPhysicalPersonLodgingOfferForEdit(Long offerId) {
        LodgingOfferEntity offer = this.physicalPersonLodgingOfferDAO.findPhysicalPersonLodgingOfferEntityById(offerId)
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Lodging offer of type physical person with id: " + offerId + " was not found")
                );
        LodgingDTOMapper mapper = new LodgingDTOMapper();
        return mapper.mapLodgingOfferToPhysicalPersonOfferEditDTO(offer);
    }

    public void editLegalPersonLodgingOffer(LegalPersonLodgingOfferEditDTO offerToSave,
                                            Long offerId){
        LegalPersonLodgingOfferEntity offer =
                this.legalPersonLodgingOfferDAO.findLegalPersonLodgingOfferEntityById(offerId)
                        .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Lodging offer of type legal person with id: " + offerId +
                                        " was not found")
                        );
        offer.setNrRooms(offerToSave.getNrRooms());
        offer.setNrBathrooms(offerToSave.getNrBathrooms());
        offer.setNrSingleBeds(offerToSave.getNrSingleBeds());
        offer.setNrDoubleBeds(offerToSave.getNrDoubleBeds());
        offer.setFloor(offerToSave.getFloor());
        offer.setPrice(offerToSave.getPrice());
        offer.setCurrency(offerToSave.getCurrency());
        offer.setDescription(offerToSave.getDescription());
        offer.setBusiness(offerToSave.getBusiness());
        this.addLodgingOfferUtilities(offer, offerToSave.getUtilities());
        this.lodgingOfferDAO.save(offer);
    }

    public void editPhysicalPersonLodgingOffer(PhysicalPersonLodgingOfferEditDTO offerToSave,
                                               Long offerId){
        PhysicalPersonLodgingOfferEntity offer =
                this.physicalPersonLodgingOfferDAO.findPhysicalPersonLodgingOfferEntityById(offerId)
                        .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Lodging offer of type legal person with id: " + offerId +
                                        " was not found")
                        );
        offer.setTitle(offerToSave.getTitle());
        offer.setAddress(offerToSave.getAddress());
        offer.setCity(offerToSave.getCity());
        offer.setNrRooms(offerToSave.getNrRooms());
        offer.setNrBathrooms(offerToSave.getNrBathrooms());
        offer.setNrSingleBeds(offerToSave.getNrSingleBeds());
        offer.setNrDoubleBeds(offerToSave.getNrDoubleBeds());
        offer.setFloor(offerToSave.getFloor());
        offer.setPrice(offerToSave.getPrice());
        offer.setCurrency(offerToSave.getCurrency());
        offer.setDescription(offerToSave.getDescription());
        this.addLodgingOfferUtilities(offer, offerToSave.getUtilities());
        this.lodgingOfferDAO.save(offer);
    }

    public void removeLodgingOfferFromFavorites(LodgingOfferEntity offer){
        for (Iterator<FavouriteOffersEntity> iterator = offer.getFavourites().iterator(); iterator.hasNext();){
            FavouriteOffersEntity favourites = iterator.next();
            favourites.getLodgingOffers().remove(offer);
            iterator.remove();
        }
        this.lodgingOfferDAO.save(offer);
    }

    public void deleteLodgingOffer(Long offerId) {
        LodgingOfferEntity offer = this.findLodgingOfferEntityById(offerId);
        this.deleteLodgingOfferUtilities(offer);
        this.removeLodgingOfferFromFavorites(offer);
        this.imageService.deleteOfferImages("lodging", offerId);
        this.lodgingOfferDAO.deleteLodgingOfferEntityById(offerId);
    }

    public void changeLodgingOfferStatus(Long offerId, OfferStatus status) {
        LodgingOfferEntity offer = this.findLodgingOfferEntityById(offerId);
        offer.setStatus(status);
        this.lodgingOfferDAO.save(offer);
    }

    public LodgingOffersStatistics getLodgingOffersStatistics() {
        List<LodgingOfferEntity> offers = this.findAllLodgingOffers();
        List<Float> offersPrices = this.getLodgingOffersPrices(offers);

        Double averageOffersViews = offers.stream()
                .collect(Collectors.averagingDouble(LodgingOfferEntity::getNrViews));
        Double averageOffersPrice = offersPrices.stream().mapToDouble((o) -> o).summaryStatistics().getAverage();

        List<LodgingOfferEntity> userOffers =
                new ArrayList<>(this.userService.findLoggedInUser().getLodgingOffers());
        List<Float> userOffersPrices = this.getLodgingOffersPrices(userOffers);

        Double averageUserOffersViews = userOffers.stream()
                .collect(Collectors.averagingDouble(LodgingOfferEntity::getNrViews));
        Double averageUserOffersPrice = userOffersPrices.stream().mapToDouble((o) -> o).summaryStatistics().getAverage();

        Double averageRequestedPrice = this.findAllLodgingOfferRequestedPrices().stream()
                .collect(Collectors.averagingDouble(LodgingOfferRequestedPrice::getPrice));

        return new LodgingOffersStatistics(averageOffersViews, averageUserOffersViews,
                averageOffersPrice, averageUserOffersPrice, averageRequestedPrice);
    }

    public void addRequestedLodgingOfferPrice(LodgingOfferRequestedPrice requestedPrice) {
        this.requestedPriceDAO.save(requestedPrice);
    }

    public List<Float> getLodgingOffersPrices(List<LodgingOfferEntity> offers) {
        try {
            CurrencyConverter currencyConverter = new CurrencyConverter();
            Float conversionRateFromEUR = currencyConverter.getCurrencyConversionRate(Currency.EUR.name(), Currency.RON.name());
            return offers.stream()
                    .map((offer) -> {
                        if (offer.getCurrency() == Currency.EUR) {
                            return currencyConverter.getConvertedPrice(offer.getPrice(), conversionRateFromEUR);
                        } else {
                            return offer.getPrice();
                        }
                    })
                    .collect(Collectors.toList());
        } catch (IOException ioException) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not retrieve currency for monetary conversion");
        }
    }

}
