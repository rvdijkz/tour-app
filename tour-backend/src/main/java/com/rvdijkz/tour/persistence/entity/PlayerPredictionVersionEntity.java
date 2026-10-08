package com.rvdijkz.tour.persistence.entity;

import com.rvdijkz.tour.persistence.converter.LongListCsvConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "player_prediction_version")
public class PlayerPredictionVersionEntity {

    @EmbeddedId
    private PlayerEditionVersionId id;

    @Convert(converter = LongListCsvConverter.class)
    @Column(name = "general_rider_ids", nullable = false, length = 128)
    private List<Long> generalRiderIds;

    @Convert(converter = LongListCsvConverter.class)
    @Column(name = "points_rider_ids", nullable = false, length = 128)
    private List<Long> pointsRiderIds;

    @Convert(converter = LongListCsvConverter.class)
    @Column(name = "mountains_rider_ids", nullable = false, length = 128)
    private List<Long> mountainsRiderIds;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public PlayerEditionVersionId getId() {
        return id;
    }

    public void setId(PlayerEditionVersionId id) {
        this.id = id;
    }

    public List<Long> getGeneralRiderIds() {
        return generalRiderIds;
    }

    public void setGeneralRiderIds(List<Long> generalRiderIds) {
        this.generalRiderIds = generalRiderIds;
    }

    public List<Long> getPointsRiderIds() {
        return pointsRiderIds;
    }

    public void setPointsRiderIds(List<Long> pointsRiderIds) {
        this.pointsRiderIds = pointsRiderIds;
    }

    public List<Long> getMountainsRiderIds() {
        return mountainsRiderIds;
    }

    public void setMountainsRiderIds(List<Long> mountainsRiderIds) {
        this.mountainsRiderIds = mountainsRiderIds;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
