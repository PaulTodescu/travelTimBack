package com.travelTim.food;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FoodOfferDAO extends JpaRepository<FoodOfferEntity, Long> {

    Optional<FoodOfferEntity> findFoodOfferEntityById(Long id);

//    void deleteFoodOfferEntityById(Long id);

    @Modifying
    @Query("delete from FoodOfferEntity o where o.id=:id")
    void deleteFoodOfferEntityById(@Param("id") Long id);
}
