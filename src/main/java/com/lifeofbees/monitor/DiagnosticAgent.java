package com.lifeofbees.monitor;

import org.springframework.stereotype.Service;

@Service
public class DiagnosticAgent {

    private final OpenAiDiagnosticService openAiDiagnosticService;

    public DiagnosticAgent(OpenAiDiagnosticService openAiDiagnosticService) {
        this.openAiDiagnosticService = openAiDiagnosticService;
    }

    public DiagnosticResult diagnose(WebsiteStatus status) {

        if (status.available()) {
            return new DiagnosticResult(
                    true,
                    "Website is responding normally"
            );
        }

        String aiDiagnosis =
                openAiDiagnosticService.diagnose(status);

        return new DiagnosticResult(
                false,
                aiDiagnosis
        );
    }
}