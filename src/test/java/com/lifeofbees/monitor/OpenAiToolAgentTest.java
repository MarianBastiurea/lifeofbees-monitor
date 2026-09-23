package com.lifeofbees.monitor;

import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OpenAiToolAgentTest {

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

        AiInvestigationResult result =
                agent.investigateWebsite();

        System.out.println("=================================");
        System.out.println("AI INVESTIGATION:");
        System.out.println(result.report());
        System.out.println("---------------------------------");
        System.out.println("WEBSITE RECOVERED: "
                + result.websiteRecovered());
        System.out.println("---------------------------------");
        System.out.println("ACTIONS:");
        result.actions().forEach(System.out::println);
        System.out.println("=================================");

        assertNotNull(result);
        assertNotNull(result.report());
        assertFalse(result.report().isBlank());

        assertNotNull(result.actions());
    }
}
