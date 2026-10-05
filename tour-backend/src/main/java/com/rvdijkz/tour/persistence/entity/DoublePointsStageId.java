package com.rvdijkz.tour.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DoublePointsStageId implements Serializable {

    @Column(name = "edition_id")
    private Long editionId;

    @Column(name = "stage_number")
    private Integer stageNumber;

    public Long getEditionId() {
        return editionId;
    }

    public void setEditionId(Long editionId) {
        this.editionId = editionId;
    }

    public Integer getStageNumber() {
        return stageNumber;
    }

    public void setStageNumber(Integer stageNumber) {
        this.stageNumber = stageNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DoublePointsStageId that)) {
            return false;
        }
        return Objects.equals(editionId, that.editionId) && Objects.equals(stageNumber, that.stageNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(editionId, stageNumber);
    }
}

