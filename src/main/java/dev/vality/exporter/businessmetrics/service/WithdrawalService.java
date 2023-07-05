package dev.vality.exporter.businessmetrics.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.vality.exporter.businessmetrics.entity.WithdrawalsMetricDto;
import dev.vality.exporter.businessmetrics.model.CustomTag;
import dev.vality.exporter.businessmetrics.model.Metric;
import dev.vality.exporter.businessmetrics.repository.WithdrawalRepository;
import io.micrometer.core.instrument.MultiGauge;
import io.micrometer.core.instrument.Tags;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("LineLength")
public class WithdrawalService {

    private static final String WITHDRAWALS_COUNT = Metric.WITHDRAWALS_COUNT.getName();
    private static final String WITHDRAWALS_AMOUNT = Metric.WITHDRAWALS_AMOUNT.getName();

    @Value("${interval.time}")
    private String intervalTime;

    private final WithdrawalRepository withdrawalRepository;
    private final MultiGauge multiGaugeWithdrawalsCount;
    private final MultiGauge multiGaugeWithdrawalsAmount;
    private final MeterRegistryService meterRegistryService;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public void registerMetrics() {
        var metrics = withdrawalRepository.getWithdrawalsMetricsByInterval(getStartPeriodDate());
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
                .flatMap(dto -> {
                    final var count = Double.parseDouble(dto.getCount());
                    final var amount = Double.parseDouble(dto.getAmount());
                    return Map.of(
                            WITHDRAWALS_COUNT, MultiGauge.Row.of(getTags(dto), this, o -> count),
                            WITHDRAWALS_AMOUNT, MultiGauge.Row.of(getTags(dto), this, o -> amount)).entrySet().stream();
                })
                .collect(
                        Collectors.groupingBy(
                                Map.Entry::getKey,
                                Collectors.mapping(
                                        Map.Entry::getValue,
                                        Collectors.<MultiGauge.Row<?>>toList())));
        multiGaugeWithdrawalsCount.register(rows.get(WITHDRAWALS_COUNT), true);
        multiGaugeWithdrawalsAmount.register(rows.get(WITHDRAWALS_AMOUNT), true);
        var registeredMetricsSize = meterRegistryService.getRegisteredMetricsSize(Metric.WITHDRAWALS_COUNT.getName()) + meterRegistryService.getRegisteredMetricsSize(Metric.WITHDRAWALS_AMOUNT.getName());
        log.info("Actual withdrawal metrics have been registered to 'prometheus', " +
                "count = {}, pendingCount = {}, failedCount = {}, succeededCount = {}, otherStatusCount = {}, metrics = {}", metrics.size(), pendingCount, failedCount, succeededCount, otherStatusCount, objectMapper.writeValueAsString(metrics));
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
