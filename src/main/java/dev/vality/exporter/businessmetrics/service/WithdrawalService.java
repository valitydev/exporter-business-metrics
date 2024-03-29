package dev.vality.exporter.businessmetrics.service;

import dev.vality.exporter.businessmetrics.entity.withdrawal.WithdrawalsAggregatedMetricDto;
import dev.vality.exporter.businessmetrics.model.CustomTag;
import dev.vality.exporter.businessmetrics.model.Metric;
import dev.vality.exporter.businessmetrics.model.WithdrawalMetricDto;
import dev.vality.exporter.businessmetrics.repository.WithdrawalRepository;
import io.micrometer.core.instrument.MultiGauge;
import io.micrometer.core.instrument.Tags;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("LineLength")
public class WithdrawalService {

    private static final String WITHDRAWALS_STATUS_COUNT = Metric.WITHDRAWALS_STATUS_COUNT.getName();
    private static final String WITHDRAWALS_AMOUNT = Metric.WITHDRAWALS_AMOUNT.getName();

    private final WithdrawalRepository withdrawalRepository;
    private final MultiGauge multiGaugeWithdrawalsFinalStatusCount;
    private final MultiGauge multiGaugeWithdrawalsAmount;
    private final MeterRegistryService meterRegistryService;

    private final Converter<WithdrawalsAggregatedMetricDto, List<WithdrawalMetricDto>> aggregatedMetricDtoConverter;

    public void registerMetrics() {
        var metrics = withdrawalRepository.getWithdrawalsMetrics();
        log.debug("Actual withdrawal metrics have been got from 'daway' db, count = {}", metrics.size());
        final var pendingCount = new LongAdder();
        final var failedCount = new LongAdder();
        final var succeededCount = new LongAdder();
        final var otherStatusCount = new LongAdder();
        var rows = metrics.stream()
                .peek(dto -> {
                    switch (dto.getStatus()) {
                        case "pending" -> pendingCount.increment();
                        case "succeeded" -> succeededCount.increment();
                        case "failed" -> failedCount.increment();
                        default -> otherStatusCount.increment();
                    }
                })
                .flatMap(dto -> Objects.requireNonNull(aggregatedMetricDtoConverter.convert(dto)).stream())
                .flatMap(dto -> {
                    final var count = Double.parseDouble(dto.getCount());
                    final var amount = Double.parseDouble(dto.getAmount());
                    return Map.of(
                            WITHDRAWALS_STATUS_COUNT, MultiGauge.Row.of(getTags(dto), this, o -> count),
                            WITHDRAWALS_AMOUNT, MultiGauge.Row.of(getTags(dto), this, o -> amount)).entrySet().stream();
                })
                .collect(
                        Collectors.groupingBy(
                                Map.Entry::getKey,
                                Collectors.mapping(
                                        Map.Entry::getValue,
                                        Collectors.<MultiGauge.Row<?>>toList())));
        multiGaugeWithdrawalsFinalStatusCount.register(rows.getOrDefault(WITHDRAWALS_STATUS_COUNT, Collections.emptyList()), true);
        multiGaugeWithdrawalsAmount.register(rows.getOrDefault(WITHDRAWALS_AMOUNT, Collections.emptyList()), true);
        var registeredMetricsSize =
                meterRegistryService.getRegisteredMetricsSize(Metric.WITHDRAWALS_STATUS_COUNT.getName()) +
                        meterRegistryService.getRegisteredMetricsSize(Metric.WITHDRAWALS_AMOUNT.getName());
        log.info("Actual withdrawal metrics have been registered to 'prometheus', " +
                "registeredMetricsSize = {}, pendingCount = {}, failedCount = {}, succeededCount = {}, " +
                "otherStatusCount = {}", registeredMetricsSize, pendingCount, failedCount, succeededCount,
                otherStatusCount);
    }

    private Tags getTags(WithdrawalMetricDto dto) {
        return Tags.of(
                CustomTag.providerId(dto.getProviderId()),
                CustomTag.providerName(dto.getProviderName()),
                CustomTag.terminalId(dto.getTerminalId()),
                CustomTag.terminalName(dto.getTerminalName()),
                CustomTag.walletId(dto.getWalletId()),
                CustomTag.walletName(dto.getWalletName()),
                CustomTag.currency(dto.getCurrencyCode()),
                CustomTag.duration(dto.getDuration()),
                CustomTag.status(dto.getStatus()));
    }
}
