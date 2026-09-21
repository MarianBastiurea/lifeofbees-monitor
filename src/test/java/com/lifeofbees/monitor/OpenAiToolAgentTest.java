package com.lifeofbees.monitor;

import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenAiToolAgentTest {

    @Test
    void shouldUseToolsAndGetDiagnosis() {

        OpenAiToolAgent agent =
                new OpenAiToolAgent(
                        OpenAIOkHttpClient.fromEnv(),
                        new WebsiteCheckTool(
                                new WebsiteChecker()
                        ),
                        new ApplicationStatusTool(),
                        new ReadApplicationLogTool()
                );

        String result =
                agent.investigateWebsite();

        System.out.println("AI INVESTIGATION:");
        System.out.println(result);

        assertNotNull(result);
        assertFalse(result.isBlank());
    }
}