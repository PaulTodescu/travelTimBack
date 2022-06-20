package com.travelTim.attractions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttractionOfferDAO extends JpaRepository<AttractionOfferEntity, Long> {

    Optional<AttractionOfferEntity> findAttractionsOfferEntityById(Long id);

    void deleteAttractionOfferEntityById(Long id);

}
