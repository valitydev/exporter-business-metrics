package dev.vality.exporter.businessmetrics.service;

import dev.vality.exporter.businessmetrics.entity.currency.CurrencyEntity;
import dev.vality.exporter.businessmetrics.model.CustomTag;
import dev.vality.exporter.businessmetrics.model.Metric;
import dev.vality.exporter.businessmetrics.repository.CurrencyRepository;
import io.micrometer.core.instrument.MultiGauge;
import io.micrometer.core.instrument.Tags;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("LineLength")
public class CurrencyService {

    private static final String CURRENCY_EXPONENT = Metric.CURRENCY_EXPONENT.getName();

    private final CurrencyRepository currencyRepository;
    private final MultiGauge multiGaugeCurrencyExponent;
    private final MeterRegistryService meterRegistryService;

    public void registerMetrics() {
        processCurrencies();
    }

    private void processCurrencies() {
        var currencyMetrics = currencyRepository.findAllByCurrentIsTrue();
        log.debug("Currencies metrics have been got from 'daway' db, count = {}",
                currencyMetrics.size());
        var rows = currencyMetrics.stream()
                .flatMap(dto -> {
                    final var count = Double.parseDouble(dto.getExponent());
                    return Map.of(
                            CURRENCY_EXPONENT, MultiGauge.Row.of(getCurrencyTags(dto), this, o -> count)).entrySet().stream();
                })
                .collect(
                        Collectors.groupingBy(
                                Map.Entry::getKey,
                                Collectors.mapping(
                                        Map.Entry::getValue,
                                        Collectors.<MultiGauge.Row<?>>toList())));
        multiGaugeCurrencyExponent.register(rows.getOrDefault(CURRENCY_EXPONENT, Collections.emptyList()), true);
        var registeredMetricsSize = meterRegistryService.getRegisteredMetricsSize(Metric.CURRENCY_EXPONENT.getName());
        log.info("Currencies metrics have been registered to 'prometheus', registeredMetricsSize = {}", registeredMetricsSize);

    }

    private Tags getCurrencyTags(CurrencyEntity dto) {
        return Tags.of(
                CustomTag.numericCode(dto.getNumericCode()),
                CustomTag.symbolicCode(dto.getSymbolicCode()));
    }
}
