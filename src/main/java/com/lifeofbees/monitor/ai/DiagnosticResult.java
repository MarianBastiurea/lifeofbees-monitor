package com.lifeofbees.monitor.ai;

public record DiagnosticResult(
        boolean healthy,
        String reason
) {
}