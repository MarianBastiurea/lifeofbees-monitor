package com.lifeofbees.monitor.ai;

import com.lifeofbees.monitor.monitoring.WebsiteStatus;
import com.openai.client.OpenAIClient;
import com.openai.models.responses.ResponseCreateParams;
import org.springframework.stereotype.Service;

@Service
public class OpenAiDiagnosticService {

    private final OpenAIClient openAIClient;

    public OpenAiDiagnosticService(OpenAIClient openAIClient) {
        this.openAIClient = openAIClient;
    }

    public String diagnose(WebsiteStatus status) {

        String prompt = """
            You are a website monitoring diagnostic assistant.

            The website https://lifeofbees.co.uk has returned the following status:

            HTTP status code: %d
            Available: %s

            Analyse the likely cause of this situation.
            Give a short technical diagnosis.
            Do not attempt any repair yet.
            """.formatted(
                status.statusCode(),
                status.available()
        );

        ResponseCreateParams params =
                ResponseCreateParams.builder()
                        .model(com.openai.models.ChatModel.GPT_5)
                        .input(prompt)
                        .build();

        return openAIClient.responses()
                .create(params)
                .output()
                .stream()
                .flatMap(item -> item.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .map(outputText -> outputText.text())
                .collect(java.util.stream.Collectors.joining("\n"));
    }
}