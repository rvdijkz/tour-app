package com.rvdijkz.tour.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "rider")
public class RiderEntity {

    @Id
    private Long id;

    @Column(name = "edition_id", nullable = false)
    private Long editionId;

    @Column(name = "game_rider_number", nullable = false)
    private Integer gameRiderNumber;

    @Column(name = "race_bib_number", nullable = false)
    private Integer raceBibNumber;

    @Column(nullable = false)
    private String name;

    @Column(name = "rider_value", nullable = false)
    private Integer value;

    @Column(nullable = false, length = 3)
    private String nationality;

    @Column(nullable = false)
    private Boolean withdrawn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEditionId() {
        return editionId;
    }

    public void setEditionId(Long editionId) {
        this.editionId = editionId;
    }

    public Integer getGameRiderNumber() {
        return gameRiderNumber;
    }

    public void setGameRiderNumber(Integer gameRiderNumber) {
        this.gameRiderNumber = gameRiderNumber;
    }

    public Integer getRaceBibNumber() {
        return raceBibNumber;
    }

    public void setRaceBibNumber(Integer raceBibNumber) {
        this.raceBibNumber = raceBibNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public Boolean getWithdrawn() {
        return withdrawn;
    }

    public void setWithdrawn(Boolean withdrawn) {
        this.withdrawn = withdrawn;
    }
}

