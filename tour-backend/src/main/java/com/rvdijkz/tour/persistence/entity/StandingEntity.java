package com.rvdijkz.tour.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "standing")
public class StandingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "edition_id", nullable = false)
    private Long editionId;

    @Column(nullable = false)
    private Integer stage;

    @Column(name = "rank_position", nullable = false)
    private Integer rankPosition;

    @Column(name = "player_id", nullable = false)
    private Long playerId;

    @Column(name = "player_name", nullable = false)
    private String playerName;

    @Column(name = "team_name", nullable = false)
    private String teamName;

    @Column(name = "total_points", nullable = false)
    private Integer totalPoints;

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

    public Integer getStage() {
        return stage;
    }

    public void setStage(Integer stage) {
        this.stage = stage;
    }

    public Integer getRankPosition() {
        return rankPosition;
    }

    public void setRankPosition(Integer rankPosition) {
        this.rankPosition = rankPosition;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Integer getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(Integer totalPoints) {
        this.totalPoints = totalPoints;
    }
}

