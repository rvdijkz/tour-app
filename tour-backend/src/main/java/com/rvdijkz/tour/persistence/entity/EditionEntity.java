package com.rvdijkz.tour.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "edition")
public class EditionEntity {

    @Id
    private Long id;

    @Column(name = "edition_year", nullable = false)
    private Integer year;

    @Column(name = "entry_deadline", nullable = false)
    private OffsetDateTime entryDeadline;

    @Column(name = "reserve_budget", nullable = false)
    private Integer reserveBudget;

    @Column(name = "required_nationality", nullable = false, length = 3)
    private String requiredNationality;

    @Column(name = "current_stage", nullable = false)
    private Integer currentStage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public OffsetDateTime getEntryDeadline() {
        return entryDeadline;
    }

    public void setEntryDeadline(OffsetDateTime entryDeadline) {
        this.entryDeadline = entryDeadline;
    }

    public Integer getReserveBudget() {
        return reserveBudget;
    }

    public void setReserveBudget(Integer reserveBudget) {
        this.reserveBudget = reserveBudget;
    }

    public String getRequiredNationality() {
        return requiredNationality;
    }

    public void setRequiredNationality(String requiredNationality) {
        this.requiredNationality = requiredNationality;
    }

    public Integer getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(Integer currentStage) {
        this.currentStage = currentStage;
    }
}

