package com.wp.ownerMS.controller;

import com.wp.ownerMS.dto.OwnerDTO;
import com.wp.ownerMS.dto.PetDTO;
import com.wp.ownerMS.service.OwnerService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import org.springframework.beans.factory.annotation.Autowired;
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
    
    // autowire load balanced rest template
    @Autowired
    RestTemplate template;

    // define variables to store microservice urls
    // used only for non-load balanced microservices
    private String registryUri;

    @GetMapping("/owners")
    public ResponseEntity<List<OwnerDTO>> getOwners() {
        List<OwnerDTO> ownerDTOS = this.ownerService.getOwners();
        return new ResponseEntity<>(ownerDTOS, HttpStatus.OK);
    }

    // display owner with with the list of its pets with all pet details
    // apply resiliency by resilience4j's circuitBreaker
    // provide name of instance name provided in application.yml configuration file
    // provide name of fallback method
    @CircuitBreaker(name="ownerMS", fallbackMethod="getOwnerFallback")
    @GetMapping("/owners/{ownerId}")
    public ResponseEntity<OwnerDTO> getOwner(@PathVariable int ownerId) {
    	
    	// get owner from service
    	// this will include an empty list of petDTOs
        OwnerDTO ownerDTO = this.ownerService.getOwnerById(ownerId);
        System.out.println(ownerDTO);
        
        // use service discovery to get microservice urls
        // used only for non-load-balanced microservices
        // to get registryMS microservice urls from service discovery
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
        
        
        // define registry microservice url needed to get list of pet ids
        String registryUrl = registryUri + "/owners/" + ownerDTO.getId() + "/pets";
        System.out.println(registryUrl);
        // get the ids of pets belonging to the owner
        List<Integer> petIds = new RestTemplate().getForEntity(registryUrl, List.class).getBody();
        // create empty petDTO array to store pet information
        List<PetDTO> petDTOS = new ArrayList<>();

        // get details for each pet that belongs to the owner
        for(Integer petId: petIds) {
        	// use load balanced rest template to contact petMS
        	// use spring application name instead and form proper url
            PetDTO petDTO = template.getForObject("http://petMS/pets/"+petId,PetDTO.class);
            petDTOS.add(petDTO);
        }
        // use the information received from the pet microservice 
        // to set the pets property of the owner
        ownerDTO.setPets(petDTOS);
        
        return new ResponseEntity<>(ownerDTO, HttpStatus.OK);

    }
    
    
    // Fallback Method for getOwner function
    public OwnerDTO getOwnerFallback(int ownerId, Throwable throwable) {
    	System.out.println("In fallback method");
    	return new OwnerDTO();
    }
}
