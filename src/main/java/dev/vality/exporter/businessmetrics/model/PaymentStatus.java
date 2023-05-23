package dev.vality.exporter.businessmetrics.model;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum PaymentStatus {

    pending,
    processed,
    captured,
    cancelled,
    refunded,
    failed,
    charged_back;

    public static boolean isConversionStatus(String status) {
        return conversionStatusStream().anyMatch(s -> s.equals(status));
    }

    public static boolean isPendingStatus(String status) {
        return status.equals(pending.name());
    }

    public static boolean isCapturedStatus(String status) {
        return captured.name().equals(status);
    }

    public static boolean isFailedStatus(String status) {
        return failed.name().equals(status);
    }

    public static String conversionStatus() {
        return conversionStatusStream().collect(Collectors.joining("|"));
    }

    public static Stream<String> conversionStatusStream() {
        return Stream.of(captured.name(), failed.name());
    }
}
