package com.rvdijkz.tour.persistence.entity;

import java.io.Serializable;
import java.util.Objects;

public class StageResultId implements Serializable {

    private Long editionId;
    private Integer stageNumber;

    public StageResultId() {
    }

    public StageResultId(Long editionId, Integer stageNumber) {
        this.editionId = editionId;
        this.stageNumber = stageNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StageResultId that)) {
            return false;
        }
        return Objects.equals(editionId, that.editionId) && Objects.equals(stageNumber, that.stageNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(editionId, stageNumber);
    }
}
