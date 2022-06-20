package com.travelTim.activities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActivityOfferDAO extends JpaRepository<ActivityOfferEntity, Long> {

    Optional<ActivityOfferEntity> findActivityOfferEntityById(Long id);

    void deleteActivityOfferEntityById(Long id);
}
