package com.lifeofbees.monitor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private EmailService emailService;

    private final WebsiteStatus downStatus =
            new WebsiteStatus(502, false);

    private final WebsiteStatus recoveredStatus =
            new WebsiteStatus(200, true);

    @Test
    void shouldSendEmailWhenWebsiteIsDown() {

        AlertService alertService = new AlertService(emailService);

        alertService.siteDown(downStatus);

        verify(emailService).sendAlert(
                "ALERT: LifeOfBees website is DOWN",
                "The LifeOfBees website is currently unavailable.\n"
                        + "Status code: 502"
        );
    }

    @Test
    void shouldSendEmailWhenWebsiteRecovers() {

        AlertService alertService = new AlertService(emailService);

        alertService.siteRecovered(recoveredStatus);

        verify(emailService).sendAlert(
                "RECOVERY: LifeOfBees website is UP",
                "The LifeOfBees website is available again.\n"
                        + "Status code: 200"
        );
    }
}

