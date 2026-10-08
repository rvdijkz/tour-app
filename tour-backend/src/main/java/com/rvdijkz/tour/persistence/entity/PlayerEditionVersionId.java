package com.rvdijkz.tour.persistence.entity;

import com.rvdijkz.tour.api.model.EntryStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PlayerEditionVersionId implements Serializable {

    @Column(name = "edition_id")
    private Long editionId;

    @Column(name = "player_id")
    private Long playerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private EntryStatus status;

    public Long getEditionId() {
        return editionId;
    }

    public void setEditionId(Long editionId) {
        this.editionId = editionId;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public EntryStatus getStatus() {
        return status;
    }

    public void setStatus(EntryStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlayerEditionVersionId that)) {
            return false;
        }
        return Objects.equals(editionId, that.editionId)
            && Objects.equals(playerId, that.playerId)
            && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(editionId, playerId, status);
    }
}
