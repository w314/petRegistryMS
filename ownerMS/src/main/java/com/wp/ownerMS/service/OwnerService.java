package com.wp.ownerMS.service;

import com.wp.ownerMS.dto.OwnerDTO;
import com.wp.ownerMS.entity.Owner;
import com.wp.ownerMS.repository.OwnerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OwnerService {

    @Autowired
    OwnerRepository ownerRepository;

    public List<OwnerDTO> getOwners() {
        Iterable<Owner> owners = this.ownerRepository.findAll();
        List<OwnerDTO> ownerDTOS = new ArrayList<>();
        for(Owner owner : owners) {
            ownerDTOS.add(OwnerDTO.toOwnerDTO(owner));
        }
        return ownerDTOS;
    }

    public OwnerDTO getOwnerById(int ownerId) {
        Optional<Owner> ownerOptional = this.ownerRepository.findById(ownerId);
        OwnerDTO ownerDTO = null;
        if(ownerOptional.isPresent()) {
            ownerDTO = OwnerDTO.toOwnerDTO(ownerOptional.get());
        }

        return ownerDTO;
    }


}
