package com.lifeofbees.monitor;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import org.springframework.stereotype.Service;

@Service
public class DockerService {

    private static final String CONTAINER_NAME =
            "spring-boot-app-new";

    private final DockerClient dockerClient;

    public DockerService() {

        DefaultDockerClientConfig config =
                DefaultDockerClientConfig.createDefaultConfigBuilder()
                        .withDockerHost("unix:///var/run/docker.sock")
                        .build();

        ApacheDockerHttpClient httpClient =
                new ApacheDockerHttpClient.Builder()
                        .dockerHost(config.getDockerHost())
                        .build();

        this.dockerClient =
                DockerClientImpl.getInstance(
                        config,
                        httpClient
                );
    }

    public String getContainerStatus() {

        return dockerClient
                .inspectContainerCmd(CONTAINER_NAME)
                .exec()
                .getState()
                .getStatus();
    }
}