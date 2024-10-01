package com.wp.registryMS.service;

import com.wp.registryMS.dto.RegistryDTO;
import com.wp.registryMS.entity.Registry;
import com.wp.registryMS.repository.RegistryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class RegistryService {

    @Autowired
    RegistryRepository registryRepository;

    public List<Integer> getPetsByOwner(int ownerId) {
        List<Registry> registries = this.registryRepository.findByOwnerId(ownerId);
        List<Integer> pets = new ArrayList<>();
        for(Registry registry: registries) {
            pets.add(registry.getPetId());
        }
        return pets;
    }
}
