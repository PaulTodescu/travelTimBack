package com.travelTim.reservation;

import java.time.LocalDateTime;

public class OfferReservationDTO {

    private Long id;
    private LocalDateTime date;
    private String title;

    public OfferReservationDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime createdAt) {
        this.date = createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
