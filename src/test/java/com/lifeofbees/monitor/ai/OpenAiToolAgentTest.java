package com.lifeofbees.monitor.ai;

import com.lifeofbees.monitor.monitoring.WebsiteChecker;
import com.lifeofbees.monitor.monitoring.WebsiteStatus;
import com.lifeofbees.monitor.tool.ApplicationStatusTool;
import com.lifeofbees.monitor.tool.ReadApplicationLogTool;
import com.lifeofbees.monitor.tool.RestartApplicationTool;
import com.lifeofbees.monitor.tool.WebsiteCheckTool;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    @Test
    void shouldVerifyWebsiteAfterRestart() {

        RestartApplicationTool restartApplicationTool =
                mock(RestartApplicationTool.class);

        when(restartApplicationTool.restartApplication())
                .thenReturn(
                        "Container 'spring-boot-app-new' restarted successfully."
                );

        WebsiteChecker websiteChecker =
                mock(WebsiteChecker.class);

        when(websiteChecker.check("https://lifeofbees.co.uk"))
                .thenReturn(
                        new WebsiteStatus(502, false),
                        new WebsiteStatus(200, true)
                );

        OpenAiToolAgent agent =
                new OpenAiToolAgent(
                        OpenAIOkHttpClient.fromEnv(),
                        new WebsiteCheckTool(websiteChecker),
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

        assertTrue(result.websiteRecovered());

        verify(restartApplicationTool, times(1))
                .restartApplication();

        verify(websiteChecker, times(2))
                .check("https://lifeofbees.co.uk");

        assertTrue(
                result.actions().stream()
                        .anyMatch(action ->
                                action.contains(
                                        "CheckWebsite: HTTP 200, available=true"
                                )
                        )
        );
    }
}
