package com.rvdijkz.tour.persistence.repository;

import com.rvdijkz.tour.persistence.entity.StageResultEntity;
import com.rvdijkz.tour.persistence.entity.StageResultId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StageResultRepository extends JpaRepository<StageResultEntity, StageResultId> {

    Optional<StageResultEntity> findByEditionIdAndStageNumber(Long editionId, Integer stageNumber);

    Optional<StageResultEntity> findTopByEditionIdAndPublishedTrueOrderByStageNumberDesc(Long editionId);

    Optional<StageResultEntity> findTopByEditionIdOrderByStageNumberDesc(Long editionId);

    boolean existsByEditionId(Long editionId);
}
