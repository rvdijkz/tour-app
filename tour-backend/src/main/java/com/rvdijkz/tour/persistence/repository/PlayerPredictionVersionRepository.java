package com.rvdijkz.tour.persistence.repository;

import com.rvdijkz.tour.api.model.EntryStatus;
import com.rvdijkz.tour.persistence.entity.PlayerEditionVersionId;
import com.rvdijkz.tour.persistence.entity.PlayerPredictionVersionEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerPredictionVersionRepository extends JpaRepository<PlayerPredictionVersionEntity, PlayerEditionVersionId> {

    Optional<PlayerPredictionVersionEntity> findByIdEditionIdAndIdPlayerIdAndIdStatus(Long editionId, Long playerId, EntryStatus status);

    List<PlayerPredictionVersionEntity> findByIdEditionIdAndIdStatus(Long editionId, EntryStatus status);
}

