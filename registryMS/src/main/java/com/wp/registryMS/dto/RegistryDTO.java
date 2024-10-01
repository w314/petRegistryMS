package com.wp.registryMS.dto;

import com.wp.registryMS.entity.Registry;

public class RegistryDTO {

    private int id;
    private int ownerId;
    private int petId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }

    public static RegistryDTO toRegistryDTO(Registry registry) {
        RegistryDTO registryDTO = new RegistryDTO();
        registryDTO.setId(registry.getId());
        registryDTO.setOwnerId(registry.getOwnerId());
        registryDTO.setPetId(registry.getPetId());
        return registryDTO;
    }

    @Override
    public String toString() {
        return "RegistryDTO{" +
                "id=" + id +
                ", ownerId=" + ownerId +
                ", petId=" + petId +
                '}';
    }
}
