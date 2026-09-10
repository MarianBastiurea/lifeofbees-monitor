package com.lifeofbees.monitor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WebsiteMonitorTest {

    @Test
    void shouldReturnWebsiteStatusFromChecker() {

        WebsiteChecker websiteChecker = mock(WebsiteChecker.class);
        AlertService   alertService=mock(AlertService.class);
        WebsiteMonitor websiteMonitor = new WebsiteMonitor(websiteChecker,alertService);

        WebsiteStatus expectedStatus = new WebsiteStatus(200, true);

        when(websiteChecker.check("https://bbc.co.uk"))
                .thenReturn(expectedStatus);

        WebsiteStatus actualStatus =
                websiteMonitor.monitor("https://bbc.co.uk");

        assertEquals(expectedStatus, actualStatus);

        verify(websiteChecker).check("https://bbc.co.uk");
    }
}

