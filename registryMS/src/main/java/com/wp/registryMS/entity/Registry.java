package com.wp.registryMS.entity;

import jakarta.persistence.*;

@Entity
@Table(name="registry")
public class Registry {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @Column(nullable = false)
    private int ownerId;

    @Column(nullable = false)
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
        return "Registry{" +
                "id=" + id +
                ", ownerId=" + ownerId +
                ", petId=" + petId +
                '}';
    }
}
