package com.rvdijkz.tour.persistence.repository;

import com.rvdijkz.tour.persistence.entity.StageHistoryEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StageHistoryRepository extends JpaRepository<StageHistoryEntity, Long> {

    List<StageHistoryEntity> findByEditionIdAndPlayerIdOrderByStageAsc(Long editionId, Long playerId);

    List<StageHistoryEntity> findByEditionIdAndStageOrderByPlayerIdAsc(Long editionId, Integer stage);

    Optional<StageHistoryEntity> findByEditionIdAndPlayerIdAndStage(Long editionId, Long playerId, Integer stage);

    Optional<StageHistoryEntity> findTopByOrderByIdDesc();

    void deleteByEditionIdAndStage(Long editionId, Integer stage);
}

