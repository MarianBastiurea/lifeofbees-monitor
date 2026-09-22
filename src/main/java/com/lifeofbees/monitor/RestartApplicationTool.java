package com.lifeofbees.monitor;

import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestartApplicationTool {

    private static final String CONTAINER_NAME =
            "spring-boot-app-new";

    private final DockerCommandExecutor dockerCommandExecutor;

    public RestartApplicationTool(
            DockerCommandExecutor dockerCommandExecutor) {

        this.dockerCommandExecutor =
                dockerCommandExecutor;
    }

    public String restartApplication() {

        try {

            int exitCode =
                    dockerCommandExecutor.execute(
                            "docker",
                            "restart",
                            CONTAINER_NAME
                    );

            if (exitCode != 0) {
                return "Unable to restart container '"
                        + CONTAINER_NAME + "'.";
            }

            return "Container '" + CONTAINER_NAME
                    + "' restarted successfully.";

        } catch (IOException e) {

            return "Unable to execute Docker restart command: "
                    + e.getMessage();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            return "Docker restart was interrupted.";
        }
    }
}