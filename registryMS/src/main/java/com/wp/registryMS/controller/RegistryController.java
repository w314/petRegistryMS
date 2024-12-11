package com.wp.registryMS.controller;

import com.wp.registryMS.dto.RegistryDTO;
import com.wp.registryMS.service.RegistryService;
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
public class RegistryController {

    @Autowired
    RegistryService registryService;

    @GetMapping("/owners/{ownerId}/pets")
    public ResponseEntity<List<Integer>> getPetsByOwner(@PathVariable int ownerId) {
        List<Integer> pets = this.registryService.getPetsByOwner(ownerId);
        if(ownerId == 1) 
        	throw new RuntimeException();
        return new ResponseEntity<>(pets, HttpStatus.OK);
    }
}
