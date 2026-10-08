package com.rvdijkz.tour.persistence.repository;

import com.rvdijkz.tour.persistence.entity.DoublePointsStageEntity;
import com.rvdijkz.tour.persistence.entity.DoublePointsStageId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoublePointsStageRepository extends JpaRepository<DoublePointsStageEntity, DoublePointsStageId> {

    List<DoublePointsStageEntity> findByIdEditionIdOrderByIdStageNumberAsc(Long editionId);

    void deleteByIdEditionId(Long editionId);
}

