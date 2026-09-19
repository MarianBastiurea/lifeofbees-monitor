package com.lifeofbees.monitor;

import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DiagnosticAgentOpenAITest {

    @Test
    void shouldGetRealDiagnosisFromOpenAI() {

        OpenAiDiagnosticService openAiDiagnosticService =
                new OpenAiDiagnosticService(
                        OpenAIOkHttpClient.fromEnv()
                );

        DiagnosticAgent diagnosticAgent =
                new DiagnosticAgent(openAiDiagnosticService);

        WebsiteStatus status =
                new WebsiteStatus(502, false);

        DiagnosticResult result =
                diagnosticAgent.diagnose(status);

        System.out.println("AI DIAGNOSIS:");
        System.out.println(result.reason());

        assertFalse(result.healthy());
        assertNotNull(result.reason());
        assertFalse(result.reason().isBlank());
    }
}