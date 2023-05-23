package dev.vality.exporter.businessmetrics.model;

import io.micrometer.core.instrument.Tag;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CustomTag {

    public static final String PROVIDER_TAG = "provider";
    public static final String TERMINAL_TAG = "terminal";
    public static final String STATUS_TAG = "status";
    public static final String CURRENCY_TAG = "currency";
    public static final String COUNTRY_TAG = "issuer_country";

    public static Tag provider(String providerName) {
        return Tag.of(PROVIDER_TAG, providerName);
    }

    public static Tag terminal(String terminalName) {
        return Tag.of(TERMINAL_TAG, terminalName);
    }

    public static Tag status(String status) {
        return Tag.of(STATUS_TAG, status);
    }

    public static Tag statusConversion() {
        return Tag.of(STATUS_TAG, PaymentStatus.conversionStatus());
    }

    public static Tag currency(String currency) {
        return Tag.of(CURRENCY_TAG, currency);
    }

    public static Tag country(String country) {
        return Tag.of(COUNTRY_TAG, country);
    }
}
