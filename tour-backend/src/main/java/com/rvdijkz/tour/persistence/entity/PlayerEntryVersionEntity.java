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
@Table(name = "player_entry_version")
public class PlayerEntryVersionEntity {

    @EmbeddedId
    private PlayerEditionVersionId id;

    @Column(name = "team_name", nullable = false, length = 64)
    private String teamName;

    @Convert(converter = LongListCsvConverter.class)
    @Column(name = "rider_ids", nullable = false, length = 255)
    private List<Long> riderIds;

    @Column(name = "finisher_count_prediction")
    private Integer finisherCountPrediction;

    @Column(name = "submitted_at")
    private OffsetDateTime submittedAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public PlayerEditionVersionId getId() {
        return id;
    }

    public void setId(PlayerEditionVersionId id) {
        this.id = id;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public List<Long> getRiderIds() {
        return riderIds;
    }

    public void setRiderIds(List<Long> riderIds) {
        this.riderIds = riderIds;
    }

    public Integer getFinisherCountPrediction() {
        return finisherCountPrediction;
    }

    public void setFinisherCountPrediction(Integer finisherCountPrediction) {
        this.finisherCountPrediction = finisherCountPrediction;
    }

    public OffsetDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(OffsetDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
