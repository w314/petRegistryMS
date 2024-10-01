package com.wp.registryMS.repository;

import com.wp.registryMS.entity.Registry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RegistryRepository extends JpaRepository<Registry, Integer> {

    public List<Registry> findByOwnerId(int ownerId);
}
