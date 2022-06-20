package com.travelTim.recommendations;

import com.travelTim.activities.ActivityDTOMapper;
import com.travelTim.activities.ActivityOfferDTO;
import com.travelTim.activities.ActivityOfferEntity;
import com.travelTim.attractions.AttractionDTOMapper;
import com.travelTim.attractions.AttractionOfferDTO;
import com.travelTim.attractions.AttractionOfferEntity;
import com.travelTim.business.BusinessEntity;
import com.travelTim.business.BusinessService;
import com.travelTim.currency.Currency;
import com.travelTim.currency.CurrencyConverter;
import com.travelTim.files.ImageService;
import com.travelTim.food.FoodDTOMapper;
import com.travelTim.food.FoodOfferDTO;
import com.travelTim.food.FoodOfferEntity;
import com.travelTim.lodging.*;
import com.travelTim.offer.OfferStatus;
import com.travelTim.review.ReviewService;
import com.travelTim.user.UserEntity;
import com.travelTim.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationsService {

    private final UserService userService;
    private final BusinessService businessService;
    private final ReviewService reviewService;
    private final ImageService imageService;

    @Autowired
    public RecommendationsService(UserService userService, BusinessService businessService,
                                  ReviewService reviewService, ImageService imageService) {
        this.userService = userService;
        this.businessService = businessService;
        this.reviewService = reviewService;
        this.imageService = imageService;
    }

    // gets top-rated users and returns first 10 offers from these users combined
    public List<LodgingOfferEntity> getRecommendedLodgingOffersForUsers() {
        List<LodgingOfferEntity> offers = new ArrayList<>();
        List<UserEntity> sortedUsersByRating = this.getSortedUsersByRating();
        Double maxRating;
        if (sortedUsersByRating.size() > 0) {
            maxRating = this.reviewService.getUserRating(sortedUsersByRating.get(0).getId()).getRating();
            for (int i = 0; i < sortedUsersByRating.size(); i++) {
                UserEntity user = sortedUsersByRating.get(i);
                offers.addAll(user.getLodgingOffers().stream()
                        .filter((o) -> o.getStatus() == OfferStatus.active &&
                                o instanceof PhysicalPersonLodgingOfferEntity)
                        .sorted(Comparator.comparing(LodgingOfferEntity::getNrViews).reversed())
                        .limit(3)
                        .collect(Collectors.toList()));
                if (Double.compare(
                        this.reviewService.getUserRating(user.getId()).getRating(), maxRating) != 0 && i > 5) {
                    break;
                }
            }
        }
        return offers;
    }

    // gets top-rated businesses and returns first 10 offers from these businesses combined
    public List<LodgingOfferEntity> getRecommendedLodgingOffersForBusinesses() {
        List<LodgingOfferEntity> offers = new ArrayList<>();
        List<BusinessEntity> sortedBusinessesByRating = this.getSortedBusinessesByRating();
        Double maxRating;
        if (sortedBusinessesByRating.size() > 0) {
            maxRating = this.reviewService.getBusinessRating(sortedBusinessesByRating.get(0).getId()).getRating();
            for (int i = 0; i < sortedBusinessesByRating.size(); i++) {
                BusinessEntity business = sortedBusinessesByRating.get(i);
                if (business.getLodgingOffers().size() > 0) {
                    for (LodgingOfferEntity offer: business.getLodgingOffers()) {
                        if (offer.getStatus() == OfferStatus.active) {
                            offers.add(business.getLodgingOffers().iterator().next());
                            break;
                        }
                    }
                }
                if (Double.compare(
                        this.reviewService.getBusinessRating(business.getId()).getRating(), maxRating) != 0 && i > 5) {
                    break;
                }
            }
        }
        return offers;
    }

    public List<LodgingOfferDTO> mapRecommendedLodgingOffers(List<LodgingOfferEntity> offers) throws IOException {
        LodgingDTOMapper mapper = new LodgingDTOMapper();
        // offers with price in EUR need to be converted to RON
        CurrencyConverter currencyConverter = new CurrencyConverter();
        Float conversionRateFromEUR = currencyConverter.getCurrencyConversionRate(Currency.EUR.name(), Currency.RON.name());
        List<LodgingOfferDTO> mappedOffers = new ArrayList<>(mapper.mapLodgingOffersToDTOs(offers));
        for (LodgingOfferDTO offer: mappedOffers){
            if (offer.getCurrency() == Currency.EUR){
                offer.setPrice(currencyConverter.getConvertedPrice(offer.getPrice(), conversionRateFromEUR));
                offer.setCurrency(Currency.RON);
            }
            if (offer.getBusiness() != null) { // offer is of legal type
                offer.setImage(this.imageService.getBusinessImages(offer.getBusiness().getId()).get(0));
                // for offers which have a business
                //      - set creation date to the date of the latest offer in that business
                //      - set price to the cheapest offer (conversion between eur and ron is also handled)
                Set<LegalPersonLodgingOfferEntity> legalLodgingOffers = offer.getBusiness().getLodgingOffers();

                List<Float> offerPrices = new ArrayList<>();
                for (LodgingOfferEntity legalOffer: legalLodgingOffers) {
                    Float price = legalOffer.getPrice();
                    if (legalOffer.getCurrency() == Currency.EUR) {
                        price = currencyConverter.getConvertedPrice(price, conversionRateFromEUR);
                    }
                    offerPrices.add(price);
                }

                Float cheapestOfferPrice = offerPrices.stream()
                        .min(Comparator.comparing(Float::floatValue))
                        .orElseThrow(NoSuchElementException::new);
                offer.setPrice(cheapestOfferPrice);

            } else { // offer is of physical type
               offer.setImage(this.imageService.getOfferFrontImage("lodging", offer.getId()));
            }
        }
        return mappedOffers;
    }

    public List<FoodOfferDTO> getRecommendedFoodOffers() {
        List<FoodOfferEntity> offers = new ArrayList<>();
        List<BusinessEntity> sortedBusinessesByRating = this.getSortedBusinessesByRating();
        Double maxRating;
        if (sortedBusinessesByRating.size() > 0) {
            maxRating = this.reviewService.getBusinessRating(sortedBusinessesByRating.get(0).getId()).getRating();
            for (int i = 0; i < sortedBusinessesByRating.size(); i++) {
                BusinessEntity business = sortedBusinessesByRating.get(i);
                FoodOfferEntity offer = business.getFoodOffer();
                if (offer != null) {
                    if (offer.getStatus() == OfferStatus.active) {
                        offers.add(business.getFoodOffer());
                    }
                }
                if (Double.compare(
                        this.reviewService.getBusinessRating(business.getId()).getRating(), maxRating) != 0 && i > 5) {
                    break;
                }
            }
        }
        FoodDTOMapper mapper = new FoodDTOMapper();
        List<FoodOfferDTO> mappedOffers = mapper.mapFoodOffersToDTOs(offers);
        for (FoodOfferDTO offer: mappedOffers){
            offer.setImage(this.imageService.getBusinessFrontImage(offer.getBusiness().getId()));
        }
        return mappedOffers;
    }

    public List<AttractionOfferEntity> getRecommendedAttractionOffersForUsers() {
        List<AttractionOfferEntity> offers = new ArrayList<>();
        List<UserEntity> sortedUsersByRating = this.getSortedUsersByRating();
        Double maxRating;
        if (sortedUsersByRating.size() > 0) {
            maxRating = this.reviewService.getUserRating(sortedUsersByRating.get(0).getId()).getRating();
            for (int i = 0; i < sortedUsersByRating.size(); i++) {
                UserEntity user = sortedUsersByRating.get(i);
                if (user.getAttractionOffers().size() > 0) {
                    offers.addAll(user.getAttractionOffers().stream()
                            .filter((o) -> o.getStatus() == OfferStatus.active && o.getBusiness() == null)
                            .sorted(Comparator.comparing(AttractionOfferEntity::getNrViews).reversed())
                            .limit(3)
                            .collect(Collectors.toList()));
                }
                if (Double.compare(
                        this.reviewService.getUserRating(user.getId()).getRating(), maxRating) != 0 && i > 5) {
                    break;
                }
            }
        }
        return offers;
    }

    public List<AttractionOfferEntity> getRecommendedAttractionOffersForBusinesses() {
        List<AttractionOfferEntity> offers = new ArrayList<>();
        List<BusinessEntity> sortedBusinessesByRating = this.getSortedBusinessesByRating();
        Double maxRating;
        if (sortedBusinessesByRating.size() > 0) {
            maxRating = this.reviewService.getBusinessRating(sortedBusinessesByRating.get(0).getId()).getRating();
            for (int i = 0; i < sortedBusinessesByRating.size(); i++) {
                BusinessEntity business = sortedBusinessesByRating.get(i);
                if (business.getAttractionOffers().size() > 0) {
                    offers.addAll(business.getAttractionOffers().stream()
                            .filter((o) -> o.getStatus() == OfferStatus.active)
                            .sorted(Comparator.comparing(AttractionOfferEntity::getNrViews).reversed())
                            .limit(3)
                            .collect(Collectors.toList()));
                }
                if (Double.compare(
                        this.reviewService.getBusinessRating(business.getId()).getRating(), maxRating) != 0 && i > 5) {
                    break;
                }
            }
        }
        return offers;
    }

    public List<AttractionOfferDTO> mapRecommendedAttractionOffers(List<AttractionOfferEntity> offers) {
        AttractionDTOMapper mapper = new AttractionDTOMapper();
        List<AttractionOfferDTO> mappedOffers = mapper.mapAttractionOffersToDTOs(offers);
        for (AttractionOfferDTO offer: mappedOffers){
            offer.setImage(this.imageService.getOfferFrontImage("attractions", offer.getId()));
        }
        return mappedOffers;
    }

    public List<ActivityOfferEntity> getRecommendedActivityOffersForUsers() {
        List<ActivityOfferEntity> offers = new ArrayList<>();
        List<UserEntity> sortedUsersByRating = this.getSortedUsersByRating();
        Double maxRating;
        if (sortedUsersByRating.size() > 0) {
            maxRating = this.reviewService.getUserRating(sortedUsersByRating.get(0).getId()).getRating();
            for (int i = 0; i < sortedUsersByRating.size(); i++) {
                UserEntity user = sortedUsersByRating.get(i);
                if (user.getActivityOffers().size() > 0) {
                    offers.addAll(user.getActivityOffers().stream()
                            .filter((o) -> o.getStatus() == OfferStatus.active && o.getBusiness() == null)
                            .sorted(Comparator.comparing(ActivityOfferEntity::getNrViews).reversed())
                            .limit(3)
                            .collect(Collectors.toList()));
                }
                if (Double.compare(
                        this.reviewService.getUserRating(user.getId()).getRating(), maxRating) != 0 && i > 5) {
                    break;
                }
            }
        }
        return offers;
    }

    public List<ActivityOfferEntity> getRecommendedActivityOffersForBusinesses() {
        List<ActivityOfferEntity> offers = new ArrayList<>();
        List<BusinessEntity> sortedBusinessesByRating = this.getSortedBusinessesByRating();
        Double maxRating;
        if (sortedBusinessesByRating.size() > 0) {
            maxRating = this.reviewService.getBusinessRating(sortedBusinessesByRating.get(0).getId()).getRating();
            for (int i = 0; i < sortedBusinessesByRating.size(); i++) {
                BusinessEntity business = sortedBusinessesByRating.get(i);
                if (business.getActivityOffers().size() > 0) {
                    offers.addAll(business.getActivityOffers().stream()
                            .filter((o) -> o.getStatus() == OfferStatus.active)
                            .sorted(Comparator.comparing(ActivityOfferEntity::getNrViews).reversed())
                            .limit(3)
                            .collect(Collectors.toList()));
                }
                if (Double.compare(
                        this.reviewService.getBusinessRating(business.getId()).getRating(), maxRating) != 0 && i > 5) {
                    break;
                }
            }
        }
        return offers;
    }

    public List<ActivityOfferDTO> mapRecommendedActivityOffers(List<ActivityOfferEntity> offers) {
        ActivityDTOMapper mapper = new ActivityDTOMapper();
        List<ActivityOfferDTO> mappedOffers = mapper.mapActivityOffersToDTOs(offers);
        for (ActivityOfferDTO offer: mappedOffers){
            offer.setImage(this.imageService.getOfferFrontImage("attractions", offer.getId()));
        }
        return mappedOffers;
    }

    public List<UserEntity> getSortedUsersByRating() {
        List<UserEntity> users = this.userService.findAllUsers();
        users.sort(Comparator.comparingDouble((UserEntity user) ->
                RecommendationsService.this.reviewService.getUserRating(user.getId()).getRating()).reversed());
        return users;
    }

    public List<BusinessEntity> getSortedBusinessesByRating() {
        List<BusinessEntity> businesses = this.businessService.getAllBusinesses();
        businesses.sort(Comparator.comparingDouble((BusinessEntity business) ->
                RecommendationsService.this.reviewService.getBusinessRating(business.getId()).getRating()).reversed());
        return businesses;
    }


}
