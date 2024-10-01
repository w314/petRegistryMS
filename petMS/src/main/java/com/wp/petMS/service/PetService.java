package com.wp.petMS.service;

import com.wp.petMS.dto.PetDTO;
import com.wp.petMS.entity.Pet;
import com.wp.petMS.repository.PetRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PetService {

    @Autowired
    PetRepository petRepo;

    public List<PetDTO> getAllPets() {
        Iterable<Pet> pets = this.petRepo.findAll();
        List<PetDTO> petDTOS = new ArrayList<>();
        for(Pet pet: pets) {
            PetDTO petDTO = PetDTO.toPetDTO(pet);
            petDTOS.add(petDTO);
        }
        return petDTOS;
    }

    public PetDTO getPetById(int petId) {
        Optional<Pet> petOptional = this.petRepo.findById(petId);
        PetDTO petDTO = null;
        if(petOptional.isPresent()) {
            petDTO = PetDTO.toPetDTO(petOptional.get());
        }
        return petDTO;
    }
}
