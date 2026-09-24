package com.lifeofbees.monitor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private EmailService emailService;

    private final WebsiteStatus upStatus =
            new WebsiteStatus(200, true);

    private final WebsiteStatus downStatus =
            new WebsiteStatus(502, false);

    @Test
    void shouldSendEmailWhenWebsiteIsUp() {

        AlertService alertService =
                new AlertService(emailService);

        alertService.sendDailyReport(
                upStatus,
                null
        );

        verify(emailService).sendAlert(
                "LifeOfBees daily report - SITE UP",
                "LifeOfBees website is UP.\n"
                        + "Status code: 200\n"
        );
    }

    @Test
    void shouldSendEmailWhenWebsiteIsDownWithAiResult() {

        AlertService alertService =
                new AlertService(emailService);

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

        alertService.sendDailyReport(
                downStatus,
                aiResult
        );

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
}

