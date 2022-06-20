package com.travelTim.food;

import com.travelTim.category.CategoryEntity;
import com.travelTim.category.CategoryService;
import com.travelTim.category.CategoryType;
import com.travelTim.contact.OfferContactDAO;
import com.travelTim.contact.OfferContactEntity;
import com.travelTim.favourites.FavouriteOffersEntity;
import com.travelTim.files.ImageService;
import com.travelTim.offer.OfferStatus;
import com.travelTim.user.UserEntity;
import com.travelTim.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class FoodOfferService {

    private final FoodOfferDAO foodOfferDAO;
    private final FoodMenuCategoryDAO foodMenuCategoryDAO;
    private final FoodMenuItemDAO foodMenuItemDAO;
    private final UserService userService;
    private final OfferContactDAO offerContactDAO;
    private final CategoryService categoryService;
    private final ImageService imageService;

    @Autowired
    public FoodOfferService(FoodOfferDAO foodOfferDAO, FoodMenuCategoryDAO foodMenuCategoryDAO,
                            FoodMenuItemDAO foodMenuItemDAO, UserService userService,
                            OfferContactDAO offerContactDAO, CategoryService categoryService,
                            ImageService imageService) {
        this.foodOfferDAO = foodOfferDAO;
        this.foodMenuCategoryDAO = foodMenuCategoryDAO;
        this.foodMenuItemDAO = foodMenuItemDAO;
        this.userService = userService;
        this.offerContactDAO = offerContactDAO;
        this.categoryService = categoryService;
        this.imageService = imageService;
    }

    public Long addFoodOffer(FoodOfferEntity foodOffer) {
        UserEntity loggedInUser = this.userService.findLoggedInUser();
        foodOffer.setUser(loggedInUser);
        foodOffer.setStatus(OfferStatus.active);
        CategoryEntity category = this.categoryService.findCategoryByName(CategoryType.food);
        foodOffer.setCategory(category);
        foodOffer.setNrViews(0L);
        try {
            return this.foodOfferDAO.save(foodOffer).getId();
        } catch (DataIntegrityViolationException exception){
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Selected business already has a food offer. Manage from your account",
                    exception);
        }
    }

    public FoodOfferEntity findFoodOfferById(Long foodOfferId){
        FoodOfferEntity offer = this.foodOfferDAO.findFoodOfferEntityById(foodOfferId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Food offer with id: " + foodOfferId + " was not found")
        );
        offer.setNrViews(offer.getNrViews() + 1);
        return offer;
    }

    public List<FoodOfferEntity> findAllFoodOffers() {
        return this.foodOfferDAO.findAll();
    }

    public FoodOfferDetailsDTO getFoodOfferDetails(Long foodOfferId){
        FoodDTOMapper mapper = new FoodDTOMapper();
        FoodOfferEntity offer = this.foodOfferDAO.findFoodOfferEntityById(foodOfferId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Food offer with id: " + foodOfferId + " was not found")
        );
        return mapper.mapFoodOfferToDetailsDTO(offer);
    }

    public FoodOfferEditDTO findFoodOfferForEdit(Long offerId){
        FoodOfferEntity foodOffer = this.findFoodOfferById(offerId);
        FoodDTOMapper mapper = new FoodDTOMapper();
        return mapper.mapFoodOfferToEditDTO(foodOffer);
    }

    public void addFoodOfferMenu(Long foodOfferId, Map<FoodMenuCategory, List<FoodMenuItem>> foodMenu) {
        FoodOfferEntity foodOffer = this.findFoodOfferById(foodOfferId);
        for (Map.Entry<FoodMenuCategory, List<FoodMenuItem>> menuEntry: foodMenu.entrySet()){

            FoodMenuCategory menuCategoryToSave;
            String categoryName = menuEntry.getKey().getName();
            if (this.foodMenuCategoryDAO.findFoodMenuCategoryByName(categoryName).isEmpty()){
                menuCategoryToSave = this.foodMenuCategoryDAO.save(menuEntry.getKey());
            } else {
                menuCategoryToSave = this.foodMenuCategoryDAO.findFoodMenuCategoryByName(categoryName).get();
            }
            Set<FoodMenuItem> menuItems = new HashSet<>();
            for (FoodMenuItem menuItem: menuEntry.getValue()){
                FoodMenuItem menuItemToSave = this.foodMenuItemDAO.save(menuItem);
                menuItems.add(menuItemToSave);
            }
            menuCategoryToSave.setFoodMenuItems(menuItems);
            foodOffer.addFoodMenuCategory(menuCategoryToSave);
        }
        this.foodOfferDAO.save(foodOffer);
    }

    public void editFoodOffer(FoodOfferEditDTO offerToSave, Long offerId){
        FoodOfferEntity offer = this.findFoodOfferById(offerId);
        offer.setBusiness(offerToSave.getBusiness());
        offer.setDescription(offerToSave.getDescription());
        this.deleteFoodOfferMenu(offer);
        this.foodOfferDAO.save(offer);
    }

    public void deleteFoodOfferMenu(FoodOfferEntity offer) {
        for (Iterator<FoodMenuCategory> iterator1 = offer.getFoodMenuCategories().iterator(); iterator1.hasNext();){
            FoodMenuCategory category = iterator1.next();
            offer.removeFoodMenuCategory(category);
            if (category.getFoodOffers().size() == 0){
                this.foodMenuCategoryDAO.deleteFoodMenuCategoryById(category.getId());
            }
            iterator1.remove();
            for (Iterator<FoodMenuItem> iterator2 = category.getFoodMenuItems().iterator(); iterator2.hasNext();){
                FoodMenuItem item = iterator2.next();
                if (item.getFoodMenuCategories().size() == 0){
                    this.foodMenuItemDAO.deleteFoodMenuItemById(item.getId());
                }
                iterator2.remove();
            }
        }
    }

    public void addContactDetails(Long offerId, OfferContactEntity offerContact){
        FoodOfferEntity offer = this.findFoodOfferById(offerId);
        this.setContactDetails(offer, offerContact);
    }

    public void editContactDetails(Long offerId, OfferContactEntity offerContact){
        FoodOfferEntity offer = this.findFoodOfferById(offerId);
        OfferContactEntity initialOfferContact = offer.getOfferContact();
        if (initialOfferContact != null && !initialOfferContact.equals(offerContact)) {
            this.deleteOfferContact(offer, initialOfferContact);
        }
        this.setContactDetails(offer, offerContact);
    }

    public void setContactDetails(FoodOfferEntity offer, OfferContactEntity offerContact){
        Optional<OfferContactEntity> contactOptional = this.offerContactDAO
                .findOfferContactEntityByEmailAndPhoneNumber(
                        offerContact.getEmail(),
                        offerContact.getPhoneNumber());
        if (contactOptional.isPresent()) {
            offer.setOfferContact(contactOptional.get());
        } else {
            offer.setOfferContact(this.offerContactDAO.save(offerContact));
        }
        this.foodOfferDAO.save(offer);
    }

    public OfferContactEntity getContactDetails(Long offerId) {
        FoodOfferEntity offer = this.findFoodOfferById(offerId);
        return offer.getOfferContact();
    }

    public void deleteOfferContact(FoodOfferEntity offer, OfferContactEntity offerContact) {
        if (offerContact != null) {
            offer.setOfferContact(null);
            offerContact.getFoodOffers().remove(offer);
            if (offerContact.getLodgingOffers().size() == 0 &&
                    offerContact.getFoodOffers().size() == 0 &&
                    offerContact.getAttractionOffers().size() == 0 &&
                    offerContact.getActivityOffers().size() == 0) {
                this.offerContactDAO.deleteOfferContactEntityById(offerContact.getId());
            }
            this.foodOfferDAO.save(offer);
        }
    }

    public void removeFoodOfferFromFavorites(FoodOfferEntity offer){
        for (Iterator<FavouriteOffersEntity> iterator = offer.getFavourites().iterator(); iterator.hasNext();){
            FavouriteOffersEntity favourites = iterator.next();
            favourites.getFoodOffers().remove(offer);
            iterator.remove();
        }
        this.foodOfferDAO.save(offer);
    }

    public void deleteFoodOffer(Long offerId) {
        FoodOfferEntity offer = this.findFoodOfferById(offerId);
        this.deleteFoodOfferMenu(offer);
        this.deleteOfferContact(offer, offer.getOfferContact());
        this.removeFoodOfferFromFavorites(offer);
        this.imageService.deleteOfferImages("food", offerId);
        this.foodOfferDAO.deleteFoodOfferEntityById(offerId);
    }

    public void changeFoodOfferStatus(Long offerId, OfferStatus status) {
        FoodOfferEntity offer = this.findFoodOfferById(offerId);
        offer.setStatus(status);
        this.foodOfferDAO.save(offer);
    }

    public FoodOffersStatistics getFoodOffersStatistics() {
        List<FoodOfferEntity> offers = this.findAllFoodOffers();
        Double averageOffersViews = offers.stream()
                .collect(Collectors.averagingDouble(FoodOfferEntity::getNrViews));

        Set<FoodOfferEntity> userOffers = this.userService.findLoggedInUser().getFoodOffers();
        Double averageUserOffersViews = userOffers.stream()
                .collect(Collectors.averagingDouble(FoodOfferEntity::getNrViews));

        return new FoodOffersStatistics(averageOffersViews, averageUserOffersViews);
    }
}
