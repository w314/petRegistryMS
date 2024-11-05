package com.wp.ownerMS.controller;

import com.wp.ownerMS.dto.OwnerDTO;
import com.wp.ownerMS.dto.PetDTO;
import com.wp.ownerMS.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
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
    
    @Autowired
    private DiscoveryClient client;

    // define variables to store microservice urls
    private String registryUri;
    private String petUri ;

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
        System.out.println(ownerDTO);
        
        // use service discovery to get microservice urls
        // get registryMS microservice urls from service discovery
        // use microservice name
        // returns list of serviceIntance (can be several instances of the MS running)
        List<ServiceInstance> listOfRegistryInstances = client.getInstances("registryMS");
        // check if there is at least one instance returned
        if(listOfRegistryInstances != null && !listOfRegistryInstances.isEmpty()) {
        	// get microservice uri from the first instance
        	// will contain host and port number
        	registryUri = listOfRegistryInstances.get(0).getUri().toString();
        	System.out.println("REGISTRY URL: " + registryUri);
        }
        // get petMS microservice urls from service registry
        List<ServiceInstance> listOfPetInstances = client.getInstances("petMS");
        if(listOfPetInstances != null && !listOfPetInstances.isEmpty()) {
        	petUri = listOfPetInstances.get(0).getUri().toString();
        }
        
        
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
