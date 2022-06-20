package com.travelTim.review;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.travelTim.user.UserEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReviewDTO {

    private Long id;
    private String review;
    private Short rating;
    private LocalDateTime createdAt;
    @JsonIgnore
    private UserEntity ownerUser;
    private String image;

    public ReviewDTO() {
    }

    public ReviewDTO(Long id, String review, Short rating, LocalDateTime createdAt,
                     UserEntity ownerUser, String image) {
        this.id = id;
        this.review = review;
        this.rating = rating;
        this.createdAt = createdAt;
        this.ownerUser = ownerUser;
        this.image = image;
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

    public UserEntity getOwnerUser() {
        return ownerUser;
    }

    public void setOwnerUser(UserEntity ownerUser) {
        this.ownerUser = ownerUser;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAuthorName() {
        return this.ownerUser.getFirstName() + " " + this.ownerUser.getLastName();
    }
}
