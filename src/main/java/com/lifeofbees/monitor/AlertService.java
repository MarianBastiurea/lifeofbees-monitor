package com.lifeofbees.monitor;

import org.springframework.stereotype.Service;

@Service
public class AlertService {

    private final EmailService emailService;

    public AlertService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void sendDailyReport(
            WebsiteStatus status,
            AiInvestigationResult aiResult) {

        String subject;

        StringBuilder text = new StringBuilder();

        if (status.available()) {

            subject = "LifeOfBees daily report - SITE UP";

            text.append("LifeOfBees website is UP.\n")
                    .append("Status code: ")
                    .append(status.statusCode())
                    .append("\n");

        } else {

            subject = "LifeOfBees daily report - SITE DOWN";

            text.append("LifeOfBees website is DOWN.\n")
                    .append("Status code: ")
                    .append(status.statusCode())
                    .append("\n");

            if (aiResult != null) {

                text.append("\nAI INVESTIGATION:\n")
                        .append(aiResult.report())
                        .append("\n");

                text.append("\nAI ACTIONS:\n");

                if (aiResult.actions() != null) {
                    for (String action : aiResult.actions()) {
                        text.append("- ")
                                .append(action)
                                .append("\n");
                    }
                }

                text.append("\nWEBSITE RECOVERED: ")
                        .append(aiResult.websiteRecovered())
                        .append("\n");
            }
        }

        emailService.sendAlert(
                subject,
                text.toString()
        );
    }
}

