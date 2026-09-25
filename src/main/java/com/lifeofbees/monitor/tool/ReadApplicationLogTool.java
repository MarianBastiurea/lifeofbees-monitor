package com.lifeofbees.monitor.tool;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class ReadApplicationLogTool {

    private static final String CONTAINER_NAME =
            "spring-boot-app-new";

    public String readApplicationLog() {

        try {
            Process process = new ProcessBuilder(
                    "docker",
                    "logs",
                    "--tail",
                    "100",
                    CONTAINER_NAME
            ).start();

            StringBuilder logs = new StringBuilder();

            try (BufferedReader reader =
                         new BufferedReader(
                                 new InputStreamReader(
                                         process.getInputStream()))) {

                String line;

                while ((line = reader.readLine()) != null) {
                    logs.append(line).append(System.lineSeparator());
                }
            }

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                return "Unable to read logs for container '"
                        + CONTAINER_NAME + "'.";
            }

            return logs.toString();

        } catch (IOException e) {
            return "Unable to execute Docker logs command: "
                    + e.getMessage();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Docker log reading was interrupted.";
        }
    }
}