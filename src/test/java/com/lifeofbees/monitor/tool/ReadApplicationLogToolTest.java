package com.lifeofbees.monitor.tool;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReadApplicationLogToolTest {

    @Test
    void shouldReadApplicationLogs() {

        ReadApplicationLogTool tool =
                new ReadApplicationLogTool();

        String result =
                tool.readApplicationLog();

        System.out.println("APPLICATION LOGS:");
        System.out.println(result);

        assertNotNull(result);
        assertFalse(result.isBlank());
    }
}