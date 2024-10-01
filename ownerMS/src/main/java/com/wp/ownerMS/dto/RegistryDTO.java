package com.wp.ownerMS.dto;

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

    @Override
    public String toString() {
        return "RegistryDTO{" +
                "id=" + id +
                ", ownerId=" + ownerId +
                ", petId=" + petId +
                '}';
    }
}
