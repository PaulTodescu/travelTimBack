package com.travelTim.food;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.travelTim.business.BusinessEntity;
import com.travelTim.category.CategoryEntity;
import com.travelTim.favourites.FavouriteOffersEntity;
import com.travelTim.offer.OfferStatus;
import com.travelTim.user.UserContactDTO;
import com.travelTim.user.UserEntity;
import org.modelmapper.ModelMapper;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "food_offer")
public class FoodOfferEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    private String email;
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OfferStatus status;

    @ManyToOne
    @JoinColumn(columnDefinition = "user_id")
    private UserEntity user;

    @OneToOne
    @JoinColumn(columnDefinition = "business_id")
    private BusinessEntity business;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private Long nrViews;

    @ManyToOne
    @JoinColumn(columnDefinition = "category_id")
    @JsonIgnore
    private CategoryEntity category;

    @ManyToMany
    @JoinTable(
            name = "food_offer_menu_category",
            joinColumns = { @JoinColumn(name = "food_offer_id")},
            inverseJoinColumns = { @JoinColumn(name = "food_menu_category_id")}
    )
    private Set<FoodMenuCategory> foodMenuCategories = new HashSet<>();

    @ManyToMany(mappedBy = "foodOffers")
    @JsonIgnore
    private Set<FavouriteOffersEntity> favourites = new HashSet<>();

    public FoodOfferEntity() {
    }

    public FoodOfferEntity(BusinessEntity business, String description, String email, String phoneNumber) {
        this.business = business;
        this.description = description;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public BusinessEntity getBusiness() {
        return business;
    }

    public void setBusiness(BusinessEntity business) {
        this.business = business;
    }

    public Set<FoodMenuCategory> getFoodMenuCategories() {
        for (FoodMenuCategory category: this.foodMenuCategories){
            category.setFoodMenuItems(category.getFoodMenuItems().stream()
                    .sorted(Comparator.comparing(FoodMenuItem::getName))
                    .collect(Collectors.toCollection(LinkedHashSet::new)));
        }
        return this.foodMenuCategories.stream()
                .sorted(Comparator.comparing(FoodMenuCategory::getName))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public void setFoodMenuCategories(Set<FoodMenuCategory> foodMenuCategories) {
        this.foodMenuCategories = foodMenuCategories;
    }

    public void addFoodMenuCategory(FoodMenuCategory menuCategory){
        this.foodMenuCategories.add(menuCategory);
    }

    public void removeFoodMenuCategory(FoodMenuCategory category){
        this.foodMenuCategories.remove(category);
        category.getFoodOffers().remove(this);
    }

    public UserContactDTO getUser() {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(this.user, UserContactDTO.class);
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getNrViews() {
        return nrViews;
    }

    public void setNrViews(Long nrViews) {
        this.nrViews = nrViews;
    }

    public Set<FavouriteOffersEntity> getFavourites() {
        return favourites;
    }

    public void setFavourites(Set<FavouriteOffersEntity> favourites) {
        this.favourites = favourites;
    }

    public OfferStatus getStatus() {
        return status;
    }

    public void setStatus(OfferStatus status) {
        this.status = status;
    }
}
