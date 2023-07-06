package dev.vality.exporter.businessmetrics.service;

import dev.vality.exporter.businessmetrics.entity.withdrawal.WithdrawalsMetricDto;
import dev.vality.exporter.businessmetrics.entity.withdrawal.WithdrawalsTransactionCountMetricDto;
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
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("LineLength")
public class WithdrawalService {

    private static final String WITHDRAWALS_FINAL_STATUS_COUNT = Metric.WITHDRAWALS_FINAL_STATUS_COUNT.getName();
    private static final String WITHDRAWALS_TRANSACTION_COUNT = Metric.WITHDRAWALS_TRANSACTION_COUNT.getName();
    private static final String WITHDRAWALS_AMOUNT = Metric.WITHDRAWALS_AMOUNT.getName();

    @Value("${interval.time}")
    private String intervalTime;

    private final WithdrawalRepository withdrawalRepository;
    private final MultiGauge multiGaugeWithdrawalsFinalStatusCount;
    private final MultiGauge multiGaugeWithdrawalsTransactionCount;
    private final MultiGauge multiGaugeWithdrawalsAmount;
    private final MeterRegistryService meterRegistryService;

    public void registerMetrics() {
        var startDateTime = getStartPeriodDate();
        processFinalStatusesAndAmount(startDateTime);
        processTransactionCount(startDateTime);
    }

    private void processFinalStatusesAndAmount(LocalDateTime startPeriodDate) {
        var metrics = withdrawalRepository.getWithdrawalsFinalStatusMetricsByInterval(startPeriodDate);
        log.debug("Withdrawals with final statuses metrics have been got from 'daway' db, " +
                "interval = {}, count = {}", intervalTime, metrics.size());
        final var failedCount = new LongAdder();
        final var succeededCount = new LongAdder();
        final var otherStatusCount = new LongAdder();
        var rows = metrics.stream()
                .peek(dto -> {
                    switch (dto.getStatus()) {
                        case "succeeded" -> succeededCount.increment();
                        case "failed" -> failedCount.increment();
                        default -> otherStatusCount.increment();
                    }
                })
                .flatMap(dto -> {
                    final var count = Double.parseDouble(dto.getCount());
                    final var amount = Double.parseDouble(dto.getAmount());
                    return Map.of(
                            WITHDRAWALS_FINAL_STATUS_COUNT, MultiGauge.Row.of(getFinalStatusCountTags(dto), this,
                                    o -> count),
                            WITHDRAWALS_AMOUNT, MultiGauge.Row.of(getFinalStatusCountTags(dto), this, o -> amount)).entrySet().stream();
                })
                .collect(
                        Collectors.groupingBy(
                                Map.Entry::getKey,
                                Collectors.mapping(
                                        Map.Entry::getValue,
                                        Collectors.<MultiGauge.Row<?>>toList())));
        multiGaugeWithdrawalsFinalStatusCount.register(rows.get(WITHDRAWALS_FINAL_STATUS_COUNT), true);
        multiGaugeWithdrawalsAmount.register(rows.get(WITHDRAWALS_AMOUNT), true);
        var registeredMetricsSize =
                meterRegistryService.getRegisteredMetricsSize(Metric.WITHDRAWALS_FINAL_STATUS_COUNT.getName()) + meterRegistryService.getRegisteredMetricsSize(Metric.WITHDRAWALS_AMOUNT.getName());
        log.info("Withdrawals with final statuses metrics have been registered to 'prometheus', " +
                "registeredMetricsSize = {}, failedCount = {}, succeededCount = {}, otherStatusCount = {}",
                registeredMetricsSize, failedCount, succeededCount, otherStatusCount);
    }

    private void processTransactionCount(LocalDateTime startPeriodDate) {
        var metrics = withdrawalRepository.getWithdrawalsCountMetricsByInterval(startPeriodDate);
        log.debug("Withdrawals transaction count metrics have been got from 'daway' db, " +
                "interval = {}, count = {}", intervalTime, metrics.size());
        var rows = metrics.stream()
                .flatMap(dto -> {
                    final var count = Double.parseDouble(dto.getCount());
                    return Map.of(
                            WITHDRAWALS_TRANSACTION_COUNT, MultiGauge.Row.of(getTransactionCountTags(dto), this,
                                    o -> count)
                    ).entrySet().stream();
                })
                .collect(
                        Collectors.groupingBy(
                                Map.Entry::getKey,
                                Collectors.mapping(
                                        Map.Entry::getValue,
                                        Collectors.<MultiGauge.Row<?>>toList())));
        multiGaugeWithdrawalsTransactionCount.register(rows.get(WITHDRAWALS_TRANSACTION_COUNT), true);
        var registeredMetricsSize =
                meterRegistryService.getRegisteredMetricsSize(Metric.WITHDRAWALS_TRANSACTION_COUNT.getName());
        log.info("Withdrawals transaction count metrics have been registered to 'prometheus', " +
                "registeredMetricsSize = {}", registeredMetricsSize);


    }

    private LocalDateTime getStartPeriodDate() {
        return LocalDateTime.now(ZoneOffset.UTC).minus(Long.parseLong(intervalTime), ChronoUnit.SECONDS);
    }

    private Tags getFinalStatusCountTags(WithdrawalsMetricDto dto) {
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

    private Tags getTransactionCountTags(WithdrawalsTransactionCountMetricDto dto) {
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
