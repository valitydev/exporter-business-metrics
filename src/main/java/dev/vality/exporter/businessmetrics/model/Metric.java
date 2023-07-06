package dev.vality.exporter.businessmetrics.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Metric {

    PAYMENTS_FINAL_STATUS_COUNT(
            formatWithPrefix("payments_final_status_count"),
            "Payments with final statuses count since last scrape"),

    PAYMENTS_TRANSACTION_COUNT(
            formatWithPrefix("payments_transaction_count"),
            "Payments new transactions since last scrape"),

    WITHDRAWALS_FINAL_STATUS_COUNT(
            formatWithPrefix("withdrawals_final_status_count"),
            "Withdrawals with final statuses count since last scrape"),

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
