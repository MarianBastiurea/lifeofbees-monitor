package com.lifeofbees.monitor.tool;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class ApplicationStatusTool {

    private static final String CONTAINER_NAME = "spring-boot-app-new";

    public String checkApplicationStatus() {

        try {
            Process process = new ProcessBuilder(
                    "docker",
                    "inspect",
                    "--format={{.State.Status}}",
                    CONTAINER_NAME
            ).start();

            try (BufferedReader reader =
                         new BufferedReader(
                                 new InputStreamReader(
                                         process.getInputStream()))) {

                String status = reader.readLine();

                int exitCode = process.waitFor();

                if (exitCode != 0 || status == null) {
                    return "Container '" + CONTAINER_NAME
                            + "' could not be found.";
                }

                return "Container '" + CONTAINER_NAME
                        + "' status: " + status;
            }

        } catch (IOException e) {
            return "Unable to execute Docker command: "
                    + e.getMessage();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Docker status check was interrupted.";
        }
    }
}