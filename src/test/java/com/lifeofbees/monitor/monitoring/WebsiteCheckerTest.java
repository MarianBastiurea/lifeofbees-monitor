package com.lifeofbees.monitor.monitoring;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


class WebsiteCheckerTest {

    @Test
    void shouldReportWebsiteUnavailableWhenConnectionFails(){
        WebsiteChecker websiteChecker=new WebsiteChecker();
        WebsiteStatus websiteStatus= websiteChecker.check("http://localhost:9999");
        assertFalse(websiteStatus.available());
        assertEquals(0,websiteStatus.statusCode());
    }

    @Test
    void shouldReportWebsiteAvailableWhenCheckBBC(){
        WebsiteChecker websiteChecker=new WebsiteChecker();
        WebsiteStatus websiteStatus= websiteChecker.check("https://bbc.co.uk");
        assertTrue(websiteStatus.available());
        assertEquals(301,websiteStatus.statusCode());
    }
}
