package com.travelTim.food;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface FoodOfferDAO extends JpaRepository<FoodOfferEntity, Long> {

    Optional<FoodOfferEntity> findFoodOfferEntityById(Long id);

    @Modifying
    @Query("delete from FoodOfferEntity offer where offer.id = :id")
    void deleteFoodOfferEntityById(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE FoodOfferEntity offer SET offer.email = :email, offer.phoneNumber = :phoneNumber WHERE offer.id = :id")
    void setContact(@Param("email") String email, @Param("phoneNumber") String phoneNumber, @Param("id") Long id);
}
