package com.lifeofbees.monitor;

public record DiagnosticResult(
        boolean healthy,
        String reason
) {
}