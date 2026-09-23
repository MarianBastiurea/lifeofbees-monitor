package com.lifeofbees.monitor;

import java.util.List;

public record AiInvestigationResult(
        String report,
        boolean websiteRecovered,
        List<String> actions
) {}