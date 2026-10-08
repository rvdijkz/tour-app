package com.rvdijkz.tour.persistence.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "edition_double_points_stage")
public class DoublePointsStageEntity {

    @EmbeddedId
    private DoublePointsStageId id;

    public DoublePointsStageId getId() {
        return id;
    }

    public void setId(DoublePointsStageId id) {
        this.id = id;
    }
}

