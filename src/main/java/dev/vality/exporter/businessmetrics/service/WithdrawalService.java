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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
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

    @Value("${interval.time}")
    private String intervalTime;

    private final WithdrawalRepository withdrawalRepository;
    private final MultiGauge multiGaugeWithdrawalsFinalStatusCount;
    private final MultiGauge multiGaugeWithdrawalsAmount;
    private final MeterRegistryService meterRegistryService;

    private final Converter<WithdrawalsAggregatedMetricDto, List<WithdrawalMetricDto>> aggregatedMetricDtoConverter;

    public void registerMetrics() {
        var metrics = withdrawalRepository.getWithdrawalsFinalStatusMetricsByInterval(getStartPeriodDate());
        log.debug("Actual withdrawal metrics have been got from 'daway' db, " +
                "interval = {}, count = {}", intervalTime, metrics.size());
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
        multiGaugeWithdrawalsFinalStatusCount.register(rows.get(WITHDRAWALS_STATUS_COUNT), true);
        multiGaugeWithdrawalsAmount.register(rows.get(WITHDRAWALS_AMOUNT), true);
        var registeredMetricsSize =
                meterRegistryService.getRegisteredMetricsSize(Metric.WITHDRAWALS_STATUS_COUNT.getName()) +
                        meterRegistryService.getRegisteredMetricsSize(Metric.WITHDRAWALS_AMOUNT.getName());
        log.info("Actual withdrawal metrics have been registered to 'prometheus', " +
                "registeredMetricsSize = {}, pendingCount = {}, failedCount = {}, succeededCount = {}, " +
                "otherStatusCount = {}", registeredMetricsSize, pendingCount, failedCount, succeededCount,
                otherStatusCount);
    }

    private LocalDateTime getStartPeriodDate() {
        return LocalDateTime.now(ZoneOffset.UTC).minus(Long.parseLong(intervalTime), ChronoUnit.SECONDS);
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
