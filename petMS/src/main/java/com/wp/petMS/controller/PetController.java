package com.wp.petMS.controller;

import com.wp.petMS.dto.PetDTO;
import com.wp.petMS.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class PetController {

    @Autowired
    PetService petService;

    @GetMapping("/pets")
    public ResponseEntity<List<PetDTO>> getAllPets() {
        List<PetDTO> petDTOS = this.petService.getAllPets();
        return new ResponseEntity<>(petDTOS, HttpStatus.OK);
    }

    @GetMapping("owners/{ownerId}/pets")
    public ResponseEntity<List<PetDTO>> getAllPetsByOwnerId(@PathVariable int ownerId) {
        List<PetDTO> petDTOS = this.petService.getPetsByOwner(ownerId);
        return new ResponseEntity<>(petDTOS, HttpStatus.OK);
    }
}
