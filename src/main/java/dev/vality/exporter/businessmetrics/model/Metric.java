package dev.vality.exporter.businessmetrics.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Metric {

    PAYMENTS_COUNT(
            formatWithPrefix("payments"),
            "Payments count since last scrape",
            "count");

    @Getter
    private final String name;
    @Getter
    private final String description;
    @Getter
    private final String unit;

    private static String formatWithPrefix(String name) {
        return String.format("ebm_%s", name);
    }
}
