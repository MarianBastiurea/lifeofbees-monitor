package com.lifeofbees.monitor.email;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Test
    void shouldSendEmail() {

        emailService.sendAlert(
                "LifeOfBees Monitor - Test",
                "This is a test email from the LifeOfBees website monitor."
        );
    }
}

