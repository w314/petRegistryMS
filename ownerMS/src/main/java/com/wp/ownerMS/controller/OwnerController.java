package com.wp.ownerMS.controller;

import com.wp.ownerMS.dto.OwnerDTO;
import com.wp.ownerMS.dto.PetDTO;
import com.wp.ownerMS.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping
public class OwnerController {

    @Autowired
    OwnerService ownerService;

//     get uri-s of other microservices needed
    @Value("${registryUri}")
    String registryUri;
    @Value("${petUri}")
    String petUri;

    @GetMapping("/owners")
    public ResponseEntity<List<OwnerDTO>> getOwners() {
        List<OwnerDTO> ownerDTOS = this.ownerService.getOwners();
        return new ResponseEntity<>(ownerDTOS, HttpStatus.OK);
    }

    @GetMapping("/owners/{ownerId}")
    public ResponseEntity<OwnerDTO> getOwner(@PathVariable int ownerId) {
    	// get owner from service
    	// this will include an empty list of petDTOs
        OwnerDTO ownerDTO = this.ownerService.getOwnerById(ownerId);
        // define registry microservice url needed to get list of pet ids
        String registryUrl = registryUri + "/owners/" + ownerDTO.getId() + "/pets";
        System.out.println(registryUrl);
        // get the ids of pets belonging to the owner
        List<Integer> petIds = new RestTemplate().getForEntity(registryUrl, List.class).getBody();
        // create empty petDTO array to store pet information
        List<PetDTO> petDTOS = new ArrayList<>();
        // get details for each pet that belongs to the owner
        for(Integer petId: petIds) {
        	// define pet microservice url for getting pet details
        	String petUrl = petUri + "/pets/" + petId;
        	System.out.println(petUrl);
            PetDTO petDTO = new RestTemplate().getForObject(petUrl,PetDTO.class);
            petDTOS.add(petDTO);
        }
        // use the information received from the pet microservice 
        // to set the pets property of the owner
        ownerDTO.setPets(petDTOS);
        return new ResponseEntity<>(ownerDTO, HttpStatus.OK);

    }
}
