package com.rvdijkz.tour.persistence.repository;

import com.rvdijkz.tour.persistence.entity.PlayerAccountEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerAccountRepository extends JpaRepository<PlayerAccountEntity, Long> {

    Optional<PlayerAccountEntity> findByUsername(String username);

    boolean existsByUsername(String username);
}
