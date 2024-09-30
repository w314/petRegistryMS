package com.wp.petMS.repository;

import com.wp.petMS.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Integer> {
    public List<Pet> findByOwnerId(int ownerId);
}
