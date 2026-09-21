package com.lifeofbees.monitor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class WebsiteCheckToolTest {

    private final WebsiteChecker websiteChecker =
            mock(WebsiteChecker.class);

    private final WebsiteCheckTool websiteCheckTool =
            new WebsiteCheckTool(websiteChecker);

    @Test
    void shouldCheckLifeOfBeesWebsite() {

        WebsiteStatus expected =
                new WebsiteStatus(502, false);

        when(websiteChecker.check("https://lifeofbees.co.uk"))
                .thenReturn(expected);

        WebsiteStatus result =
                websiteCheckTool.checkWebsite();

        assertEquals(expected, result);

        verify(websiteChecker)
                .check("https://lifeofbees.co.uk");
    }
}