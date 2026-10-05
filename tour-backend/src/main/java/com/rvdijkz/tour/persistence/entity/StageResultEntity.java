package com.rvdijkz.tour.persistence.entity;

import com.rvdijkz.tour.persistence.converter.IntegerListCsvConverter;
import com.rvdijkz.tour.persistence.converter.PlacedBibListCsvConverter;
import com.rvdijkz.tour.persistence.value.PlacedBibData;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@IdClass(StageResultId.class)
@Table(name = "stage_result")
public class StageResultEntity {

    @Id
    @Column(name = "edition_id", nullable = false)
    private Long editionId;

    @Id
    @Column(name = "stage_number", nullable = false)
    private Integer stageNumber;

    @Convert(converter = PlacedBibListCsvConverter.class)
    @Column(name = "finishers", nullable = false, length = 512)
    private List<PlacedBibData> finishers;

    @Convert(converter = PlacedBibListCsvConverter.class)
    @Column(name = "last_five", nullable = false, length = 128)
    private List<PlacedBibData> lastFive;

    @Column(name = "yellow_race_bib", nullable = false)
    private Integer yellowRaceBib;

    @Column(name = "points_race_bib", nullable = false)
    private Integer pointsRaceBib;

    @Column(name = "mountains_race_bib", nullable = false)
    private Integer mountainsRaceBib;

    @Column(name = "white_race_bib", nullable = false)
    private Integer whiteRaceBib;

    @Column(name = "most_combative_race_bib")
    private Integer mostCombativeRaceBib;

    @Convert(converter = IntegerListCsvConverter.class)
    @Column(name = "withdrawals", nullable = false, length = 255)
    private List<Integer> withdrawals;

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

    public Integer getStageNumber() {
        return stageNumber;
    }

    public void setStageNumber(Integer stageNumber) {
        this.stageNumber = stageNumber;
    }

    public List<PlacedBibData> getFinishers() {
        return finishers;
    }

    public void setFinishers(List<PlacedBibData> finishers) {
        this.finishers = finishers;
    }

    public List<PlacedBibData> getLastFive() {
        return lastFive;
    }

    public void setLastFive(List<PlacedBibData> lastFive) {
        this.lastFive = lastFive;
    }

    public Integer getYellowRaceBib() {
        return yellowRaceBib;
    }

    public void setYellowRaceBib(Integer yellowRaceBib) {
        this.yellowRaceBib = yellowRaceBib;
    }

    public Integer getPointsRaceBib() {
        return pointsRaceBib;
    }

    public void setPointsRaceBib(Integer pointsRaceBib) {
        this.pointsRaceBib = pointsRaceBib;
    }

    public Integer getMountainsRaceBib() {
        return mountainsRaceBib;
    }

    public void setMountainsRaceBib(Integer mountainsRaceBib) {
        this.mountainsRaceBib = mountainsRaceBib;
    }

    public Integer getWhiteRaceBib() {
        return whiteRaceBib;
    }

    public void setWhiteRaceBib(Integer whiteRaceBib) {
        this.whiteRaceBib = whiteRaceBib;
    }

    public Integer getMostCombativeRaceBib() {
        return mostCombativeRaceBib;
    }

    public void setMostCombativeRaceBib(Integer mostCombativeRaceBib) {
        this.mostCombativeRaceBib = mostCombativeRaceBib;
    }

    public List<Integer> getWithdrawals() {
        return withdrawals;
    }

    public void setWithdrawals(List<Integer> withdrawals) {
        this.withdrawals = withdrawals;
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
