package com.lifeofbees.monitor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.mockito.Mockito.times;

import static org.mockito.Mockito.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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

    @MockitoBean
    private MonitoringEventRepository monitoringEventRepository;



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

    @Test
    void shouldSendRecoveryAlertWhenWebsiteComesBackUp() {// Website is initially DOWN
        when(websiteChecker.check("https://lifeofbees.co.uk"))
                .thenReturn(new WebsiteStatus(502, false));

        websiteMonitor.checkWebsite();

        // Website then comes back UP
        when(websiteChecker.check("https://lifeofbees.co.uk"))
                .thenReturn(new WebsiteStatus(200, true));

        websiteMonitor.checkWebsite();

        verify(emailService).sendAlert(
                "RECOVERY: LifeOfBees website is UP",
                "The LifeOfBees website is available again.\n"
                        + "Status code: 200\n"
                        + "Diagnosis: Website is responding normally"
        );
    }

    @Test
    void shouldSendDownAlertWhenWebsiteIsAlreadyDownAtStartup() {

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

    @Test
    void shouldNotSendRepeatedDownAlerts() {

        // Website is initially UP
        when(websiteChecker.check("https://lifeofbees.co.uk"))
                .thenReturn(new WebsiteStatus(200, true));

        websiteMonitor.checkWebsite();

        // Website goes DOWN
        when(websiteChecker.check("https://lifeofbees.co.uk"))
                .thenReturn(new WebsiteStatus(502, false));

        websiteMonitor.checkWebsite();

        // Website is still DOWN
        websiteMonitor.checkWebsite();

        // Only one alert should have been sent
        verify(emailService, times(1)).sendAlert(
                "ALERT: LifeOfBees website is DOWN",
                "The LifeOfBees website is currently unavailable.\n"
                        + "Status code: 502\n"
                        + "Diagnosis: Bad Gateway - the web server cannot reach the application"
        );
    }
    @Test
    void shouldNotSendRepeatedRecoveryAlerts() {

        // Website is initially DOWN
        when(websiteChecker.check("https://lifeofbees.co.uk"))
                .thenReturn(new WebsiteStatus(502, false));

        websiteMonitor.checkWebsite();

        // Website comes back UP
        when(websiteChecker.check("https://lifeofbees.co.uk"))
                .thenReturn(new WebsiteStatus(200, true));

        websiteMonitor.checkWebsite();

        // Website is still UP
        websiteMonitor.checkWebsite();

        verify(emailService, times(1)).sendAlert(
                "RECOVERY: LifeOfBees website is UP",
                "The LifeOfBees website is available again.\n"
                        + "Status code: 200\n"
                        + "Diagnosis: Website is responding normally"
        );
    }

    @Test
    void shouldSendDownAlertWhenWebsiteCannotBeReached() {

        when(websiteChecker.check("https://lifeofbees.co.uk"))
                .thenReturn(new WebsiteStatus(0, false));

        websiteMonitor.checkWebsite();

        verify(emailService).sendAlert(
                "ALERT: LifeOfBees website is DOWN",
                "The LifeOfBees website is currently unavailable.\n"
                        + "Status code: 0\n"
                        + "Diagnosis: Website is unavailable - HTTP status 0"
        );
    }
}