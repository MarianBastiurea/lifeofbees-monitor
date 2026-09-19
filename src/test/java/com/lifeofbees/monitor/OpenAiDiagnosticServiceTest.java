package com.lifeofbees.monitor;

import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenAiDiagnosticServiceTest {

    @Test
    void shouldGetDiagnosisFromOpenAI() {

        OpenAiDiagnosticService service =
                new OpenAiDiagnosticService(
                        OpenAIOkHttpClient.fromEnv()
                );

        WebsiteStatus status =
                new WebsiteStatus(502, false);

        String diagnosis =
                service.diagnose(status);

        System.out.println("AI DIAGNOSIS:");
        System.out.println(diagnosis);

        assertNotNull(diagnosis);
        assertFalse(diagnosis.isBlank());
    }
}