package com.lifeofbees.monitor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.*;

@SpringBootTest(
        properties = {
                "spring.task.scheduling.enabled=false"
        }
)
class WebsiteMonitorIntegrationTest {

    @Autowired
    private WebsiteMonitor websiteMonitor;

    @MockitoBean
    private WebsiteChecker websiteChecker;

    @MockitoBean
    private EmailService emailService;

    @Test
    void shouldSendDownAlertWhenWebsiteGoesDown() {

        // Website is initially UP
        when(websiteChecker.check("https://lifeofbees.co.uk"))
                .thenReturn(new WebsiteStatus(200, true));

        websiteMonitor.checkWebsite();

        // Website then goes DOWN
        when(websiteChecker.check("https://lifeofbees.co.uk"))
                .thenReturn(new WebsiteStatus(502, false));

        websiteMonitor.checkWebsite();

        verify(emailService).sendAlert(
                "ALERT: LifeOfBees website is DOWN",
                "The LifeOfBees website is currently unavailable.\n"
                        + "Status code: 502\n"
                        + "Diagnosis: Bad Gateway - the web server cannot reach the application"
        );
    }
}