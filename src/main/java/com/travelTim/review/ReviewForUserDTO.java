package com.travelTim.review;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.travelTim.business.BusinessEntity;
import com.travelTim.user.UserEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReviewForUserDTO {

    private Long id;
    private String review;
    private Short rating;
    private LocalDateTime createdAt;
    @JsonIgnore
    private UserEntity targetUser;
    @JsonIgnore
    private BusinessEntity business;

    public ReviewForUserDTO() {
    }

    public ReviewForUserDTO(Long id, String review, Short rating, LocalDateTime createdAt,
                            UserEntity targetUser, BusinessEntity business) {
        this.id = id;
        this.review = review;
        this.rating = rating;
        this.createdAt = createdAt;
        this.targetUser = targetUser;
        this.business = business;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Short getRating() {
        return rating;
    }

    public void setRating(Short rating) {
        this.rating = rating;
    }

    public String getCreatedAt() {
        LocalDate formattedDateTime = this.createdAt.toLocalDate();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return formattedDateTime.format(dateFormatter);
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UserEntity getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(UserEntity targetUser) {
        this.targetUser = targetUser;
    }

    public BusinessEntity getBusiness() {
        return business;
    }

    public void setBusiness(BusinessEntity business) {
        this.business = business;
    }

    public String getTargetName() {
        if (this.targetUser != null) {
            return this.targetUser.getFirstName() + " " + this.targetUser.getLastName();
        } else {
            return this.business.getName();
        }
    }

}
