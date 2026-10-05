package com.rvdijkz.tour.persistence.repository;

import com.rvdijkz.tour.persistence.entity.RiderEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiderRepository extends JpaRepository<RiderEntity, Long> {

    List<RiderEntity> findByEditionIdOrderByGameRiderNumberAsc(Long editionId);

    List<RiderEntity> findByEditionIdAndIdIn(Long editionId, List<Long> riderIds);

    List<RiderEntity> findByEditionIdAndRaceBibNumberIn(Long editionId, List<Integer> raceBibNumbers);

    Optional<RiderEntity> findByEditionIdAndRaceBibNumber(Long editionId, Integer raceBibNumber);
}

