package com.lifeofbees.monitor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MonitoringController.class)
class MonitoringControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MonitoringHistoryService historyService;

    @MockitoBean
    private WebsiteMonitor websiteMonitor;

    @Test
    void shouldReturnMonitoringHistory() throws Exception {

        MonitoringEvent event1 = new MonitoringEvent(
                LocalDateTime.of(2026, 9, 17, 10, 0),
                200,
                true,
                "Website is responding normally"
        );

        MonitoringEvent event2 = new MonitoringEvent(
                LocalDateTime.of(2026, 9, 17, 10, 10),
                502,
                false,
                "Bad Gateway - the web server cannot reach the application"
        );

        when(historyService.getAllEvents())
                .thenReturn(List.of(event1, event2));

        mockMvc.perform(get("/api/monitoring/history"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].statusCode").value(200))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[1].statusCode").value(502))
                .andExpect(jsonPath("$[1].available").value(false));
    }

    @Test
    void shouldReturnLatestMonitoringEvent() throws Exception {

        MonitoringEvent event = new MonitoringEvent(
                LocalDateTime.of(2026, 9, 17, 10, 10),
                502,
                false,
                "Bad Gateway - the web server cannot reach the application"
        );

        when(historyService.getLatestEvent())
                .thenReturn(event);

        mockMvc.perform(get("/api/monitoring/latest"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value(502))
                .andExpect(jsonPath("$.available").value(false))
                .andExpect(jsonPath("$.diagnosis")
                        .value("Bad Gateway - the web server cannot reach the application"));
    }
}