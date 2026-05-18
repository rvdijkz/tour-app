package com.rvdijkz.tour.application.port.in;

public interface GetSystemInfoUseCase {

    SystemInfo getSystemInfo();

    record SystemInfo(String serviceName, String version) {
    }
}

