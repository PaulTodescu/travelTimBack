package com.travelTim.contact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OfferContactDAO extends JpaRepository<OfferContactEntity, Long> {

    Optional<OfferContactEntity> findOfferContactEntityByEmailAndPhoneNumber(String email, String phoneNumber);

    void deleteOfferContactEntityById(Long id);
}
