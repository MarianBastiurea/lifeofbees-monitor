package com.lifeofbees.monitor;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RestartApplicationToolTest {

    private final DockerCommandExecutor dockerCommandExecutor =
            mock(DockerCommandExecutor.class);

    private final RestartApplicationTool restartApplicationTool =
            new RestartApplicationTool(dockerCommandExecutor);

    @Test
    void shouldRestartApplicationContainer() throws Exception {

        when(dockerCommandExecutor.execute(
                "docker",
                "restart",
                "spring-boot-app-new"
        )).thenReturn(0);

        String result =
                restartApplicationTool.restartApplication();

        System.out.println("RESTART RESULT:");
        System.out.println(result);

        assertEquals(
                "Container 'spring-boot-app-new' restarted successfully.",
                result
        );

        verify(dockerCommandExecutor).execute(
                "docker",
                "restart",
                "spring-boot-app-new"
        );
    }
}