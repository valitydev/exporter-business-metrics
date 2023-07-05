package dev.vality.exporter.businessmetrics.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.vality.exporter.businessmetrics.entity.PaymentsMetricDto;
import dev.vality.exporter.businessmetrics.model.CustomTag;
import dev.vality.exporter.businessmetrics.model.Metric;
import dev.vality.exporter.businessmetrics.repository.PaymentRepository;
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
public class PaymentService {

    private static final String PAYMENTS_COUNT = Metric.PAYMENTS_COUNT.getName();
    private static final String PAYMENTS_AMOUNT = Metric.PAYMENTS_AMOUNT.getName();

    @Value("${interval.time}")
    private String intervalTime;

    private final PaymentRepository paymentRepository;
    private final MultiGauge multiGaugePaymentsCount;
    private final MultiGauge multiGaugePaymentsAmount;
    private final MeterRegistryService meterRegistryService;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public void registerMetrics() {
        var metrics = paymentRepository.getPaymentsMetricsByInterval(getStartPeriodDate());
        final var pendingCount = new LongAdder();
        final var failedCount = new LongAdder();
        final var capturedCount = new LongAdder();
        final var otherStatusCount = new LongAdder();
        var rows = metrics.stream()
                .peek(dto -> {
                    switch (dto.getStatus()) {
                        case "pending" -> pendingCount.increment();
                        case "captured" -> capturedCount.increment();
                        case "failed" -> failedCount.increment();
                        default -> otherStatusCount.increment();
                    }
                })
                .flatMap(dto -> {
                    final var count = Double.parseDouble(dto.getCount());
                    final var amount = Double.parseDouble(dto.getAmount());
                    return Map.of(
                            PAYMENTS_COUNT, MultiGauge.Row.of(getTags(dto), this, o -> count),
                            PAYMENTS_AMOUNT, MultiGauge.Row.of(getTags(dto), this, o -> amount)).entrySet().stream();
                })
                .collect(
                        Collectors.groupingBy(
                                Map.Entry::getKey,
                                Collectors.mapping(
                                        Map.Entry::getValue,
                                        Collectors.<MultiGauge.Row<?>>toList())));
        multiGaugePaymentsCount.register(rows.get(PAYMENTS_COUNT), true);
        multiGaugePaymentsAmount.register(rows.get(PAYMENTS_AMOUNT), true);
        var registeredMetricsSize = meterRegistryService.getRegisteredMetricsSize(Metric.PAYMENTS_COUNT.getName()) + meterRegistryService.getRegisteredMetricsSize(Metric.PAYMENTS_AMOUNT.getName());
        log.info("Actual payments metrics have been registered to 'prometheus', " +
                "count = {}, registeredMetricsSize = {}, pendingCount = {}, failedCount = {}, capturedCount = {}, otherStatusCount = {}, metrics = {}", metrics.size(), registeredMetricsSize, pendingCount, failedCount, capturedCount, otherStatusCount, objectMapper.writeValueAsString(metrics));
    }

    private LocalDateTime getStartPeriodDate() {
        return LocalDateTime.now(ZoneOffset.UTC).minus(Long.parseLong(intervalTime), ChronoUnit.SECONDS);
    }

    private Tags getTags(PaymentsMetricDto dto) {
        return Tags.of(
                CustomTag.providerId(dto.getProviderId()),
                CustomTag.providerName(dto.getProviderName()),
                CustomTag.terminalId(dto.getTerminalId()),
                CustomTag.terminalName(dto.getTerminalName()),
                CustomTag.shopId(dto.getShopId()),
                CustomTag.shopName(dto.getShopName()),
                CustomTag.currency(dto.getCurrencyCode()),
                CustomTag.issuerCountry(dto.getIssuerCountry()),
                CustomTag.issuerBank(dto.getIssuerBank()),
                CustomTag.issuerBankCardPaymentSystem(dto.getIssuerBankCardPaymentSystem()),
                CustomTag.status(dto.getStatus()));
    }
}
