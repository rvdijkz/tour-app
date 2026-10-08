package com.rvdijkz.tour.persistence.repository;

import com.rvdijkz.tour.persistence.entity.EditionEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EditionRepository extends JpaRepository<EditionEntity, Long> {

	Optional<EditionEntity> findTopByOrderByYearDescIdDesc();
}

