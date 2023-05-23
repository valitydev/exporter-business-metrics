package dev.vality.exporter.businessmetrics.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Metric {

    PAYMENTS_COUNT(
            formatWithPrefix("payments"),
            "Count of payments",
            MetricUnit.COUNT.getUnit());

    @Getter
    private final String name;
    @Getter
    private final String description;
    @Getter
    private final String unit;

    private static String formatWithPrefix(String name) {
        return String.format("bme_%s", name);
    }
}
