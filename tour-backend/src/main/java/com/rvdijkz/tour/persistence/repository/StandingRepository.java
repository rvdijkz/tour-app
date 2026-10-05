package com.rvdijkz.tour.persistence.repository;

import com.rvdijkz.tour.persistence.entity.StandingEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StandingRepository extends JpaRepository<StandingEntity, Long> {

    List<StandingEntity> findByEditionIdAndStageOrderByRankPositionAsc(Long editionId, Integer stage);

    Optional<StandingEntity> findByEditionIdAndStageAndPlayerId(Long editionId, Integer stage, Long playerId);

    Optional<StandingEntity> findTopByOrderByIdDesc();

    void deleteByEditionIdAndStage(Long editionId, Integer stage);
}

