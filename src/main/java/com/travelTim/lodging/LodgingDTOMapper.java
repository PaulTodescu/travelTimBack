package com.travelTim.lodging;

import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LodgingDTOMapper {

    private final ModelMapper modelMapper;

    public LodgingDTOMapper() {
        this.modelMapper = new ModelMapper();
    }

    public LodgingOfferDTO mapLodgingOfferToDTO(LodgingOfferEntity offer){
        return this.modelMapper.map(offer, LodgingOfferDTO.class);
    }

    public Set<LodgingOfferDTO> mapLodgingOffersToDTOs(Set<LodgingOfferEntity> offers){
        return offers.stream().map(this::mapLodgingOfferToDTO).collect(Collectors.toSet());
    }

    public List<LodgingOfferDTO> mapLodgingOffersToDTOs(List<LodgingOfferEntity> offers){
        return offers.stream().map(this::mapLodgingOfferToDTO).collect(Collectors.toList());
    }

    public LegalPersonLodgingOfferDTO mapLegalLodgingOfferToDTO(LegalPersonLodgingOfferEntity offer){
        return this.modelMapper.map(offer, LegalPersonLodgingOfferDTO.class);
    }

    public Set<LegalPersonLodgingOfferDTO> mapLegalLodgingOffersToDTOs(Set<LegalPersonLodgingOfferEntity> offers){
        return offers.stream().map(this::mapLegalLodgingOfferToDTO).collect(Collectors.toSet());
    }

    public LegalPersonLodgingOfferBaseDetailsDTO mapLegalLodgingOfferToBaseDetailsDTO(LegalPersonLodgingOfferEntity offer){
        return this.modelMapper.map(offer, LegalPersonLodgingOfferBaseDetailsDTO.class);
    }

    public LegalPersonLodgingOfferDetailsDTO mapLegalLodgingOfferToLodgingDetailsDTO(LegalPersonLodgingOfferEntity offer){
        return this.modelMapper.map(offer, LegalPersonLodgingOfferDetailsDTO.class);
    }

    public Set<LegalPersonLodgingOfferDetailsDTO> mapLodgingOffersToLodgingDetailsDTOs(Set<LegalPersonLodgingOfferEntity> offers){
        return offers.stream().map(this::mapLegalLodgingOfferToLodgingDetailsDTO).collect(Collectors.toSet());
    }

    public LodgingOfferBaseDetailsDTO mapLodgingOfferToBaseDetailsDTO(LodgingOfferEntity lodgingOffer){
        return this.modelMapper.map(lodgingOffer, LodgingOfferBaseDetailsDTO.class);
    }

    public Set<LodgingOfferBaseDetailsDTO> mapLodgingOffersToBaseDetailsDTOs(Set<LodgingOfferEntity> offers){
        return offers.stream().map(this::mapLodgingOfferToBaseDetailsDTO).collect(Collectors.toSet());
    }

    public LodgingOfferPriceDTO mapLodgingOfferToPriceDTO(LodgingOfferEntity offer){
        return this.modelMapper.map(offer, LodgingOfferPriceDTO.class);
    }

    public PhysicalPersonLodgingOfferEditDTO mapLodgingOfferToPhysicalPersonOfferEditDTO(LodgingOfferEntity offer){
        return this.modelMapper.map(offer, PhysicalPersonLodgingOfferEditDTO.class);
    }

    public LegalPersonLodgingOfferEditDTO mapLodgingOfferToLegalPersonOfferEditDTO(LodgingOfferEntity offer){
        return this.modelMapper.map(offer, LegalPersonLodgingOfferEditDTO.class);
    }

    public PhysicalPersonLodgingOfferDTO mapPhysicalPersonLodgingOfferToDTO(PhysicalPersonLodgingOfferEntity offer){
        return this.modelMapper.map(offer, PhysicalPersonLodgingOfferDTO.class);
    }

    public Set<PhysicalPersonLodgingOfferDTO> mapPhysicalPersonLodgingOffersToDTOs(Set<PhysicalPersonLodgingOfferEntity> offers){
        return offers.stream().map(this::mapPhysicalPersonLodgingOfferToDTO).collect(Collectors.toSet());
    }

//    public LodgingOfferForReservationDetailsDTO mapLodgingOfferToReservationDetailsDTO(LodgingOfferEntity offer){
//        return this.modelMapper.map(offer, LodgingOfferForReservationDetailsDTO.class);
//    }

    public LodgingOfferDetailsForReservationDTO mapLodgingOfferToReservationDetailsDTO(LodgingOfferEntity offer){
        return this.modelMapper.map(offer, LodgingOfferDetailsForReservationDTO.class);
    }

    public PhysicalPersonLodgingOfferDetailsDTO mapPhysicalPersonLodgingOfferToDetailsDTO(PhysicalPersonLodgingOfferEntity offer) {
        return this.modelMapper.map(offer, PhysicalPersonLodgingOfferDetailsDTO.class);
    }

}
