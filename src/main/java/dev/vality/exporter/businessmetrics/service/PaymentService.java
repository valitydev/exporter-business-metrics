package dev.vality.exporter.businessmetrics.service;

import dev.vality.exporter.businessmetrics.entity.payment.PaymentsAggregatedMetricDto;
import dev.vality.exporter.businessmetrics.entity.payment.PaymentsTransactionCountMetricDto;
import dev.vality.exporter.businessmetrics.model.CustomTag;
import dev.vality.exporter.businessmetrics.model.Metric;
import dev.vality.exporter.businessmetrics.model.PaymentMetricDto;
import dev.vality.exporter.businessmetrics.repository.PaymentRepository;
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
public class PaymentService {

    private static final String PAYMENTS_STATUS_COUNT = Metric.PAYMENTS_STATUS_COUNT.getName();
    private static final String PAYMENTS_TRANSACTION_COUNT = Metric.PAYMENTS_TRANSACTION_COUNT.getName();
    private static final String PAYMENTS_AMOUNT = Metric.PAYMENTS_AMOUNT.getName();

    @Value("${interval.time}")
    private String intervalTime;

    private final PaymentRepository paymentRepository;
    private final MultiGauge multiGaugePaymentsFinalStatusCount;
    private final MultiGauge multiGaugePaymentsTransactionCount;
    private final MultiGauge multiGaugePaymentsAmount;
    private final MeterRegistryService meterRegistryService;

    private final Converter<PaymentsAggregatedMetricDto, List<PaymentMetricDto>> aggregatedMetricDtoConverter;

    public void registerMetrics() {
        var startDateTime = getStartPeriodDate();
        processFinalStatusesAndAmount(startDateTime);
        processTransactionCount(startDateTime);
    }

    private void processFinalStatusesAndAmount(LocalDateTime startPeriodDate) {
        var finalStatusMetrics = paymentRepository.getPaymentsFinalStatusMetricsByInterval(startPeriodDate);
        log.debug("Payments with final statuses metrics have been got from 'daway' db, " +
                "interval = {}, count = {}", intervalTime, finalStatusMetrics.size());
        final var failedCount = new LongAdder();
        final var capturedCount = new LongAdder();
        final var otherStatusCount = new LongAdder();
        var rows = finalStatusMetrics.stream()
                .peek(dto -> {
                    switch (dto.getStatus()) {
                        case "captured" -> capturedCount.increment();
                        case "failed" -> failedCount.increment();
                        default -> otherStatusCount.increment();
                    }
                })
                .flatMap(dto -> Objects.requireNonNull(aggregatedMetricDtoConverter.convert(dto)).stream())
                .flatMap(dto -> {
                    final var count = Double.parseDouble(dto.getCount());
                    final var amount = Double.parseDouble(dto.getAmount());
                    return Map.of(
                            PAYMENTS_STATUS_COUNT, MultiGauge.Row.of(getFinalStatusCountTags(dto), this, o -> count),
                            PAYMENTS_AMOUNT, MultiGauge.Row.of(getFinalStatusCountTags(dto), this, o -> amount)).entrySet().stream();
                })
                .collect(
                        Collectors.groupingBy(
                                Map.Entry::getKey,
                                Collectors.mapping(
                                        Map.Entry::getValue,
                                        Collectors.<MultiGauge.Row<?>>toList())));
        multiGaugePaymentsFinalStatusCount.register(rows.get(PAYMENTS_STATUS_COUNT), true);
        multiGaugePaymentsAmount.register(rows.get(PAYMENTS_AMOUNT), true);
        var registeredMetricsSize = meterRegistryService.getRegisteredMetricsSize(Metric.PAYMENTS_STATUS_COUNT.getName()) + meterRegistryService.getRegisteredMetricsSize(Metric.PAYMENTS_AMOUNT.getName());
        log.info("Payments with final statuses metrics have been registered to 'prometheus', " +
                "registeredMetricsSize = {}, failedCount = {}, capturedCount = {}, otherStatusCount = {}", registeredMetricsSize, failedCount, capturedCount, otherStatusCount);
    }

    private void processTransactionCount(LocalDateTime startPeriodDate) {
        var transactionCountMetrics = paymentRepository.getPaymentsCountMetricsByInterval(startPeriodDate);
        log.debug("Payments with transaction count metrics have been got from 'daway' db, " +
                "interval = {}, count = {}", intervalTime, transactionCountMetrics.size());
        var rows = transactionCountMetrics.stream()
                .flatMap(dto -> {
                    final var count = Double.parseDouble(dto.getCount());
                    return Map.of(
                            PAYMENTS_TRANSACTION_COUNT, MultiGauge.Row.of(getTransactionCountTags(dto), this, o -> count)).entrySet().stream();
                })
                .collect(
                        Collectors.groupingBy(
                                Map.Entry::getKey,
                                Collectors.mapping(
                                        Map.Entry::getValue,
                                        Collectors.<MultiGauge.Row<?>>toList())));
        multiGaugePaymentsTransactionCount.register(rows.get(PAYMENTS_TRANSACTION_COUNT), true);
        var registeredMetricsSize = meterRegistryService.getRegisteredMetricsSize(Metric.PAYMENTS_TRANSACTION_COUNT.getName()) + meterRegistryService.getRegisteredMetricsSize(Metric.PAYMENTS_TRANSACTION_COUNT.getName());
        log.info("Payments with transaction count metrics have been registered to 'prometheus', registeredMetricsSize = {}", registeredMetricsSize);

    }

    private LocalDateTime getStartPeriodDate() {
        return LocalDateTime.now(ZoneOffset.UTC).minus(Long.parseLong(intervalTime), ChronoUnit.SECONDS);
    }

    private Tags getFinalStatusCountTags(PaymentMetricDto dto) {
        return Tags.of(
                CustomTag.providerId(dto.getProviderId()),
                CustomTag.providerName(dto.getProviderName()),
                CustomTag.terminalId(dto.getTerminalId()),
                CustomTag.terminalName(dto.getTerminalName()),
                CustomTag.shopId(dto.getShopId()),
                CustomTag.shopName(dto.getShopName()),
                CustomTag.currency(dto.getCurrencyCode()),
                CustomTag.duration(dto.getDuration()),
                CustomTag.status(dto.getStatus()));
    }

    private Tags getTransactionCountTags(PaymentsTransactionCountMetricDto dto) {
        return Tags.of(
                CustomTag.providerId(dto.getProviderId()),
                CustomTag.providerName(dto.getProviderName()),
                CustomTag.terminalId(dto.getTerminalId()),
                CustomTag.terminalName(dto.getTerminalName()),
                CustomTag.shopId(dto.getShopId()),
                CustomTag.shopName(dto.getShopName()),
                CustomTag.currency(dto.getCurrencyCode()));
    }
}
