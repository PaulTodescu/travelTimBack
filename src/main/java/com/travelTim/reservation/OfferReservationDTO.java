package com.travelTim.reservation;

public class OfferReservationDTO {

    private Long id;
    private String date;
    private String title;

    public OfferReservationDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String createdAt) {
        this.date = createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
