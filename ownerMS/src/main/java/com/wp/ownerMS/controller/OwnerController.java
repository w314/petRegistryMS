package com.wp.ownerMS.controller;

import com.wp.ownerMS.dto.OwnerDTO;
import com.wp.ownerMS.dto.PetDTO;
import com.wp.ownerMS.dto.RegistryDTO;
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
        OwnerDTO ownerDTO = this.ownerService.getOwnerById(ownerId);
        String registryUrl = registryUri + "/owners/" + ownerDTO.getId() + "/pets";
        System.out.println(registryUrl);
        List<Integer> petIds = new RestTemplate().getForEntity(registryUrl, List.class).getBody();
        List<PetDTO> petDTOS = new ArrayList<>();
        for(Integer petId: petIds) {
            String petUrl = petUri + "/pets/" + petId;
            System.out.println(petUrl);
            PetDTO petDTO = new RestTemplate().getForObject(petUrl,PetDTO.class);
            petDTOS.add(petDTO);
        }
        ownerDTO.setPets(petDTOS);
        return new ResponseEntity<>(ownerDTO, HttpStatus.OK);

    }
}
