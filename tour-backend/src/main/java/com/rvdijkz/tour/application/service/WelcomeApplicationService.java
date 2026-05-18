package com.rvdijkz.tour.application.service;

import com.rvdijkz.tour.application.port.in.GetWelcomeMessageUseCase;
import com.rvdijkz.tour.domain.service.WelcomeDomainService;
import org.springframework.stereotype.Service;

@Service
public class WelcomeApplicationService implements GetWelcomeMessageUseCase {

    private final WelcomeDomainService welcomeDomainService;

    public WelcomeApplicationService(WelcomeDomainService welcomeDomainService) {
        this.welcomeDomainService = welcomeDomainService;
    }

    @Override
    public String getWelcomeMessage() {
        return welcomeDomainService.buildWelcomeMessage();
    }
}

