package com.lifeofbees.monitor;

import org.springframework.stereotype.Service;

@Service
public class AlertService {

    private EmailService emailService;


    public AlertService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void siteDown(WebsiteStatus status,DiagnosticResult diagnostic) {
        String subject = "ALERT: LifeOfBees website is DOWN";

        String text = "The LifeOfBees website is currently unavailable.\n"
                + "Status code: " + status.statusCode()+"\n"
                + "Diagnosis: " + diagnostic.reason();

        emailService.sendAlert(subject, text);
    }

    public void siteRecovered(WebsiteStatus status, DiagnosticResult diagnostic) {
        String subject = "RECOVERY: LifeOfBees website is UP";

        String text = "The LifeOfBees website is available again.\n"
                + "Status code: " + status.statusCode()+"\n"
                + "Diagnosis: " + diagnostic.reason();

        emailService.sendAlert(subject, text);
    }
}