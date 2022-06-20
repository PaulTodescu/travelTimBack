package com.travelTim.lodging;

import com.travelTim.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LodgingOfferDAO extends JpaRepository<LodgingOfferEntity, Long> {

    Optional<LodgingOfferEntity> findLodgingOfferEntityById(Long id);

    void deleteLodgingOfferEntityById(Long id);
}
