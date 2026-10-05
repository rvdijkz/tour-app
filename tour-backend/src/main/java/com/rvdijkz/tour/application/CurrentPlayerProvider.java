package com.rvdijkz.tour.application;

import com.rvdijkz.tour.api.error.ApiException;
import com.rvdijkz.tour.persistence.entity.PlayerAccountEntity;
import com.rvdijkz.tour.persistence.repository.PlayerAccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentPlayerProvider {

    private final PlayerAccountRepository playerAccountRepository;
    private final long devPlayerId;

    public CurrentPlayerProvider(
        PlayerAccountRepository playerAccountRepository,
        @Value("${tour.dev-player-id:1}") long devPlayerId
    ) {
        this.playerAccountRepository = playerAccountRepository;
        this.devPlayerId = devPlayerId;
    }

    public PlayerAccountEntity getCurrentPlayer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
            && authentication.isAuthenticated()
            && !(authentication instanceof AnonymousAuthenticationToken)
            && authentication.getName() != null
            && !authentication.getName().isBlank()) {
            return playerAccountRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> ApiException.notFound("PLAYER_NOT_FOUND", "Authenticated player account was not found."));
        }

        return playerAccountRepository.findById(devPlayerId)
            .orElseThrow(() -> ApiException.notFound("PLAYER_NOT_FOUND", "Current development player account was not found."));
    }
}
