package com.lifeofbees.monitor;

import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DockerCommandExecutor {

    public int execute(String... command)
            throws IOException, InterruptedException {

        Process process =
                new ProcessBuilder(command).start();

        return process.waitFor();
    }
}