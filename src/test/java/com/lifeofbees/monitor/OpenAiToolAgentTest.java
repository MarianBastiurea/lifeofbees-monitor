package com.lifeofbees.monitor;

import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OpenAiToolAgentTest {

    /*
    @Test
    void shouldUseToolsAndGetDiagnosis() {

        OpenAiToolAgent agent =
                new OpenAiToolAgent(
                        OpenAIOkHttpClient.fromEnv(),
                        new WebsiteCheckTool(
                                new WebsiteChecker()
                        ),
                        new ApplicationStatusTool(),
                        new ReadApplicationLogTool(),
                        new RestartApplicationTool(new DockerCommandExecutor())
                );

        String result =
                agent.investigateWebsite();

        System.out.println("AI INVESTIGATION:");
        System.out.println(result);

        assertNotNull(result);
        assertFalse(result.isBlank());
    }
*/

    @Test
    void shouldInvestigateWebsiteAndUseAvailableTools() {

        RestartApplicationTool restartApplicationTool =
                mock(RestartApplicationTool.class);

        when(restartApplicationTool.restartApplication())
                .thenReturn(
                        "Container 'spring-boot-app-new' restarted successfully."
                );

        OpenAiToolAgent agent =
                new OpenAiToolAgent(
                        OpenAIOkHttpClient.fromEnv(),
                        new WebsiteCheckTool(
                                new WebsiteChecker()
                        ),
                        new ApplicationStatusTool(),
                        new ReadApplicationLogTool(),
                        restartApplicationTool
                );

        String result =
                agent.investigateWebsite();

        System.out.println("AI INVESTIGATION:");
        System.out.println(result);

        assertNotNull(result);
        assertFalse(result.isBlank());
    }
}