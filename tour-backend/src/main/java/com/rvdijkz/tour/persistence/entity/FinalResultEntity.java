package com.rvdijkz.tour.persistence.entity;

import com.rvdijkz.tour.persistence.converter.LongListCsvConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "final_result")
public class FinalResultEntity {

    @Id
    @Column(name = "edition_id", nullable = false)
    private Long editionId;

    @Convert(converter = LongListCsvConverter.class)
    @Column(name = "general_top5_rider_ids", nullable = false, length = 128)
    private List<Long> generalTop5RiderIds;

    @Convert(converter = LongListCsvConverter.class)
    @Column(name = "points_top5_rider_ids", nullable = false, length = 128)
    private List<Long> pointsTop5RiderIds;

    @Convert(converter = LongListCsvConverter.class)
    @Column(name = "mountains_top5_rider_ids", nullable = false, length = 128)
    private List<Long> mountainsTop5RiderIds;

    @Column(name = "finisher_count", nullable = false)
    private Integer finisherCount;

    @Column(name = "saved_at", nullable = false)
    private OffsetDateTime savedAt;

    @Column(name = "calculated_at")
    private OffsetDateTime calculatedAt;

    @Column(nullable = false)
    private Boolean published;

    public Long getEditionId() {
        return editionId;
    }

    public void setEditionId(Long editionId) {
        this.editionId = editionId;
    }

    public List<Long> getGeneralTop5RiderIds() {
        return generalTop5RiderIds;
    }

    public void setGeneralTop5RiderIds(List<Long> generalTop5RiderIds) {
        this.generalTop5RiderIds = generalTop5RiderIds;
    }

    public List<Long> getPointsTop5RiderIds() {
        return pointsTop5RiderIds;
    }

    public void setPointsTop5RiderIds(List<Long> pointsTop5RiderIds) {
        this.pointsTop5RiderIds = pointsTop5RiderIds;
    }

    public List<Long> getMountainsTop5RiderIds() {
        return mountainsTop5RiderIds;
    }

    public void setMountainsTop5RiderIds(List<Long> mountainsTop5RiderIds) {
        this.mountainsTop5RiderIds = mountainsTop5RiderIds;
    }

    public Integer getFinisherCount() {
        return finisherCount;
    }

    public void setFinisherCount(Integer finisherCount) {
        this.finisherCount = finisherCount;
    }

    public OffsetDateTime getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(OffsetDateTime savedAt) {
        this.savedAt = savedAt;
    }

    public OffsetDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(OffsetDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }
}
