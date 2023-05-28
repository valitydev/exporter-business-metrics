package dev.vality.exporter.businessmetrics.service;

import dev.vality.exporter.businessmetrics.entity.WithdrawalsMetricDto;
import dev.vality.exporter.businessmetrics.model.CustomTag;
import dev.vality.exporter.businessmetrics.model.Metric;
import dev.vality.exporter.businessmetrics.repository.WithdrawalRepository;
import io.micrometer.core.instrument.MultiGauge;
import io.micrometer.core.instrument.Tags;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("LineLength")
public class WithdrawalService {

    @Value("${interval.time}")
    private String intervalTime;

    private final WithdrawalRepository withdrawalRepository;
    private final MultiGauge multiGaugeWithdrawalsCount;
    private final MeterRegistryService meterRegistryService;

    public void registerMetrics() {
        var metrics = withdrawalRepository.getWithdrawalsMetricsByInterval(getStartPeriodDate());
        log.info("Actual withdrawal metrics have been got from 'daway' db, " +
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
                .map(dto -> {
                    final var value = Double.parseDouble(dto.getCount());
                    return MultiGauge.Row.of(getTags(dto), this, o -> value);
                })
                .collect(Collectors.<MultiGauge.Row<?>>toList());
        multiGaugeWithdrawalsCount.register(rows, true);
        var registeredMetricsSize = meterRegistryService.getRegisteredMetricsSize(Metric.WITHDRAWALS_COUNT.getName());
        log.info("Actual withdrawal metrics have been registered to 'prometheus', " +
                "registeredMetricsSize = {}, pendingCount = {}, failedCount = {}, succeededCount = {}, otherStatusCount = {}", registeredMetricsSize, pendingCount, failedCount, succeededCount, otherStatusCount);
    }

    private LocalDateTime getStartPeriodDate() {
        return LocalDateTime.now(ZoneOffset.UTC).minus(Long.parseLong(intervalTime), ChronoUnit.SECONDS);
    }

    private Tags getTags(WithdrawalsMetricDto dto) {
        return Tags.of(
                CustomTag.providerId(dto.getProviderId()),
                CustomTag.providerName(dto.getProviderName()),
                CustomTag.terminalId(dto.getTerminalId()),
                CustomTag.terminalName(dto.getTerminalName()),
                CustomTag.walletId(dto.getWalletId()),
                CustomTag.walletName(dto.getWalletName()),
                CustomTag.currency(dto.getCurrencyCode()),
                CustomTag.status(dto.getStatus()));
    }
}
