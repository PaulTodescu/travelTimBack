package com.travelTim.category;

import com.travelTim.activities.ActivityDTOMapper;
import com.travelTim.activities.ActivityOfferDTO;
import com.travelTim.activities.ActivityOfferEntity;
import com.travelTim.attractions.AttractionDTOMapper;
import com.travelTim.attractions.AttractionOfferDTO;
import com.travelTim.attractions.AttractionOfferEntity;
import com.travelTim.currency.CurrencyConverter;
import com.travelTim.files.ImageService;
import com.travelTim.food.FoodDTOMapper;
import com.travelTim.food.FoodOfferDTO;
import com.travelTim.currency.Currency;
import com.travelTim.food.FoodOfferEntity;
import com.travelTim.lodging.LegalPersonLodgingOfferEntity;
import com.travelTim.lodging.LodgingDTOMapper;
import com.travelTim.lodging.LodgingOfferDTO;
import com.travelTim.lodging.LodgingOfferEntity;
import com.travelTim.offer.OfferStatus;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryDAO categoryDAO;
    private final ImageService imageService;

    @Autowired
    public CategoryService(CategoryDAO categoryDAO, ImageService imageService) {
        this.categoryDAO = categoryDAO;
        this.imageService = imageService;
    }

    public List<CategoryDTO> findAllCategories(){
        List<CategoryEntity> categories = categoryDAO.findAll();
        return mapCategoriesToDTOs(categories);
    }

    public CategoryDTO mapCategoryToDTO(CategoryEntity category){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(category, CategoryDTO.class);
    }

    public List<CategoryDTO> mapCategoriesToDTOs(List<CategoryEntity> categories){
        return categories.stream().map(this::mapCategoryToDTO).collect(Collectors.toList());
    }

    public CategoryEntity findCategoryById(Long categoryId) {
        Optional<CategoryEntity> category = this.categoryDAO.findCategoryEntityById(categoryId);
        return category.orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category with id: " + categoryId + " was not found"));
    }

    public CategoryEntity findCategoryByName(CategoryType categoryType) {
        Optional<CategoryEntity> category = this.categoryDAO.findCategoryEntityByName(categoryType.toString());
        return category.orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category with name: " + categoryType + " was not found"));
    }


    public Set<LodgingOfferDTO> getLodgingOffers() throws IOException {
        CategoryEntity category = this.findCategoryByName(CategoryType.lodging);
        LodgingDTOMapper mapper = new LodgingDTOMapper();

        // offers with price in EUR need to be converted to RON
        CurrencyConverter currencyConverter = new CurrencyConverter();
        //Float conversionRateFromEUR = currencyConverter.getCurrencyConversionRate(Currency.EUR.name(), Currency.RON.name());
        Set<LodgingOfferEntity> activeOffers = category.getLodgingOffers().stream()
                .filter(offer -> offer.getStatus() == OfferStatus.active)
                .collect(Collectors.toSet());
        Set<LodgingOfferDTO> offers = mapper.mapLodgingOffersToDTOs(activeOffers);
        for (LodgingOfferDTO offer: offers){
            if (offer.getCurrency() == Currency.EUR){
                //offer.setPrice(currencyConverter.getConvertedPrice(offer.getPrice(), conversionRateFromEUR));
                offer.setCurrency(Currency.RON);
            }
            if (offer.getBusiness() != null) { // offer is of legal type
                offer.setImage(this.imageService.getBusinessImages(offer.getBusiness().getId()).get(0));
                // for offers which have a business
                //      - set creation date to the date of the latest offer in that business
                //      - set price to the cheapest offer (conversion between eur and ron is also handled)
                Set<LegalPersonLodgingOfferEntity> lodgingOffers = offer.getBusiness().getLodgingOffers();

                List<LocalDateTime> offerDates = lodgingOffers.stream()
                        .map(LodgingOfferEntity::getCreatedAt).sorted().collect(Collectors.toList());
                offer.setCreatedAt(offerDates.get(offerDates.size() - 1));

                List<LodgingOfferEntity> offersListSortedByPrice = new ArrayList<>(lodgingOffers);

//                offersListSortedByPrice.sort((o1, o2) -> {
//                    Float priceOffer1 = currencyConverter.getConvertedPrice(o1.getPrice(), conversionRateFromEUR);
//                    Float priceOffer2 = currencyConverter.getConvertedPrice(o2.getPrice(), conversionRateFromEUR);
//                    if (priceOffer1 < priceOffer2) {
//                        return -1;
//                    } else if (priceOffer1 > priceOffer2) {
//                        return 1;
//                    }
//                    return 0;
//                });

                LodgingOfferEntity cheapestOffer = offersListSortedByPrice.get(0);
                Float cheapestOfferPrice;
                if (cheapestOffer.getCurrency() == Currency.EUR){
                    //cheapestOfferPrice = currencyConverter.getConvertedPrice(cheapestOffer.getPrice(), conversionRateFromEUR);
                } else {
                    cheapestOfferPrice = cheapestOffer.getPrice();
                }
                //offer.setPrice(cheapestOfferPrice);

            } else { // offer is of physical type
               offer.setImage(this.imageService.getOfferFrontImage("lodging", offer.getId()));
            }
        }
        return offers;
    }

    public Set<FoodOfferDTO> getFoodOffers() {
        CategoryEntity category = this.findCategoryByName(CategoryType.food);
        FoodDTOMapper mapper = new FoodDTOMapper();
        Set<FoodOfferEntity> activeOffers = category.getFoodOffers().stream()
                .filter(offer -> offer.getStatus() == OfferStatus.active)
                .collect(Collectors.toSet());
        Set<FoodOfferDTO> offers = mapper.mapFoodOffersToDTOs(activeOffers);
        for (FoodOfferDTO offer: offers){
            offer.setImage(this.imageService.getBusinessFrontImage(offer.getBusiness().getId()));
        }
        return offers;
    }

    public Set<AttractionOfferDTO> getAttractionOffers() {
        CategoryEntity category = this.findCategoryByName(CategoryType.attractions);
        AttractionDTOMapper mapper = new AttractionDTOMapper();
        Set<AttractionOfferEntity> activeOffers = category.getAttractionOffers().stream()
                .filter(offer -> offer.getStatus() == OfferStatus.active)
                .collect(Collectors.toSet());
        Set<AttractionOfferDTO> offers = mapper.mapAttractionOffersToDTOs(activeOffers);
        for (AttractionOfferDTO offer: offers){
            offer.setImage(this.imageService.getOfferFrontImage("attractions", offer.getId()));
        }
        return offers;
    }

    public Set<ActivityOfferDTO> getActivityOffers() {
        CategoryEntity category = this.findCategoryByName(CategoryType.activities);
        ActivityDTOMapper mapper = new ActivityDTOMapper();
        Set<ActivityOfferEntity> activeOffers = category.getActivityOffers().stream()
                .filter(offer -> offer.getStatus() == OfferStatus.active)
                .collect(Collectors.toSet());
        Set<ActivityOfferDTO> offers = mapper.mapActivityOffersToDTOs(activeOffers);
        for (ActivityOfferDTO offer: offers){
            offer.setImage(this.imageService.getOfferFrontImage("activities", offer.getId()));
        }
        return offers;
    }
}
