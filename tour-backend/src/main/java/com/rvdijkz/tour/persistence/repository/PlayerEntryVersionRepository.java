package com.rvdijkz.tour.persistence.repository;

import com.rvdijkz.tour.api.model.EntryStatus;
import com.rvdijkz.tour.persistence.entity.PlayerEditionVersionId;
import com.rvdijkz.tour.persistence.entity.PlayerEntryVersionEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerEntryVersionRepository extends JpaRepository<PlayerEntryVersionEntity, PlayerEditionVersionId> {

    Optional<PlayerEntryVersionEntity> findByIdEditionIdAndIdPlayerIdAndIdStatus(Long editionId, Long playerId, EntryStatus status);

    List<PlayerEntryVersionEntity> findByIdEditionIdAndIdStatus(Long editionId, EntryStatus status);

    boolean existsByIdEditionId(Long editionId);
}
