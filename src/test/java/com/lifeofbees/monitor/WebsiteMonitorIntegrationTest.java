package com.lifeofbees.monitor;

import com.lifeofbees.monitor.ai.AiInvestigationResult;
import com.lifeofbees.monitor.ai.OpenAiToolAgent;
import com.lifeofbees.monitor.email.EmailService;
import com.lifeofbees.monitor.monitoring.WebsiteChecker;
import com.lifeofbees.monitor.monitoring.WebsiteMonitor;
import com.lifeofbees.monitor.monitoring.WebsiteStatus;
import com.lifeofbees.monitor.persistence.MonitoringEvent;
import com.lifeofbees.monitor.persistence.MonitoringEventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.Mockito.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(properties = {"spring.task.scheduling.enabled=false",
        "spring.autoconfigure.exclude=" +
                "org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration"})
class WebsiteMonitorIntegrationTest {

    @Autowired
    private WebsiteMonitor websiteMonitor;

    @MockitoBean
    private WebsiteChecker websiteChecker;

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private MonitoringEventRepository monitoringEventRepository;

    @MockitoBean
    private OpenAiToolAgent openAiToolAgent;


    @Test
    void shouldSendDailyReportWhenWebsiteIsUp() {

        when(websiteChecker.check("https://lifeofbees.co.uk"))
                .thenReturn(new WebsiteStatus(200, true));

        websiteMonitor.checkWebsite();

        verify(openAiToolAgent, never())
                .investigateWebsite();

        verify(monitoringEventRepository)
                .save(any(MonitoringEvent.class));

        verify(emailService).sendAlert(
                "LifeOfBees daily report - SITE UP",
                "LifeOfBees website is UP.\n"
                        + "Status code: 200\n"
        );
    }


    @Test
    void shouldInvestigateAndSendReportWhenWebsiteIsDown() {

        WebsiteStatus downStatus =
                new WebsiteStatus(502, false);

        AiInvestigationResult aiResult =
                new AiInvestigationResult(
                        "AI investigated the application and restarted the container. Website recovered.",
                        true,
                        List.of(
                                "CheckApplicationStatus: Container 'spring-boot-app-new' status: exited",
                                "ReadApplicationLog: recent application logs read",
                                "RestartApplication: Container 'spring-boot-app-new' restarted successfully.",
                                "CheckWebsite: HTTP 200, available=true"
                        )
                );

        when(websiteChecker.check("https://lifeofbees.co.uk"))
                .thenReturn(downStatus);

        when(openAiToolAgent.investigateWebsite())
                .thenReturn(aiResult);

        websiteMonitor.checkWebsite();

        verify(openAiToolAgent)
                .investigateWebsite();

        verify(monitoringEventRepository)
                .save(any(MonitoringEvent.class));

        verify(emailService).sendAlert(
                "LifeOfBees daily report - SITE DOWN",
                "LifeOfBees website is DOWN.\n"
                        + "Status code: 502\n"
                        + "\nAI INVESTIGATION:\n"
                        + "AI investigated the application and restarted the container. Website recovered.\n"
                        + "\nAI ACTIONS:\n"
                        + "- CheckApplicationStatus: Container 'spring-boot-app-new' status: exited\n"
                        + "- ReadApplicationLog: recent application logs read\n"
                        + "- RestartApplication: Container 'spring-boot-app-new' restarted successfully.\n"
                        + "- CheckWebsite: HTTP 200, available=true\n"
                        + "\nWEBSITE RECOVERED: true\n"
        );
    }


    @Test
    void shouldSaveDownEventWhenWebsiteCannotBeReached() {

        WebsiteStatus downStatus =
                new WebsiteStatus(0, false);

        AiInvestigationResult aiResult =
                new AiInvestigationResult(
                        "AI could not recover the website.",
                        false,
                        List.of(
                                "CheckApplicationStatus: application unavailable",
                                "ReadApplicationLog: application logs checked"
                        )
                );

        when(websiteChecker.check("https://lifeofbees.co.uk"))
                .thenReturn(downStatus);

        when(openAiToolAgent.investigateWebsite())
                .thenReturn(aiResult);

        websiteMonitor.checkWebsite();

        verify(openAiToolAgent)
                .investigateWebsite();

        verify(monitoringEventRepository)
                .save(any(MonitoringEvent.class));

        verify(emailService).sendAlert(
                "LifeOfBees daily report - SITE DOWN",
                "LifeOfBees website is DOWN.\n"
                        + "Status code: 0\n"
                        + "\nAI INVESTIGATION:\n"
                        + "AI could not recover the website.\n"
                        + "\nAI ACTIONS:\n"
                        + "- CheckApplicationStatus: application unavailable\n"
                        + "- ReadApplicationLog: application logs checked\n"
                        + "\nWEBSITE RECOVERED: false\n"
        );
    }


}
