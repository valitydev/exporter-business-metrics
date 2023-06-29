package dev.vality.exporter.businessmetrics.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Metric {

    PAYMENTS_COUNT(
            formatWithPrefix("payments_count"),
            "Payments count since last scrape"),

    WITHDRAWALS_COUNT(
            formatWithPrefix("withdrawals_count"),
            "Withdrawals count since last scrape"),

    PAYMENTS_AMOUNT(
            formatWithPrefix("payments_amount"),
            "Payments amount since last scrape"),

    WITHDRAWALS_AMOUNT(
            formatWithPrefix("withdrawals_amount"),
            "Withdrawals amount since last scrape");

    @Getter
    private final String name;
    @Getter
    private final String description;

    private static String formatWithPrefix(String name) {
        return String.format("ebm_%s", name);
    }
}
