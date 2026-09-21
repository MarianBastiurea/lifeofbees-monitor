package com.lifeofbees.monitor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DockerServiceTest {

    private final DockerService dockerService =
            new DockerService();

    @Test
    void shouldReturnContainerStatusMessage() {

        String result =
                dockerService.getContainerStatus();

        assertNotNull(result);
        assertFalse(result.isBlank());
        assertTrue(result.contains("spring-boot-app-new"));
    }
}