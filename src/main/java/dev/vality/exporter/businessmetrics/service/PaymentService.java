package dev.vality.exporter.businessmetrics.service;

import dev.vality.exporter.businessmetrics.entity.PaymentsMetricDto;
import dev.vality.exporter.businessmetrics.model.CustomTag;
import dev.vality.exporter.businessmetrics.model.Metric;
import dev.vality.exporter.businessmetrics.repository.PaymentRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.function.ToDoubleFunction;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    @Value("${interval.time}")
    private String intervalTime;

    private final PaymentRepository paymentRepository;

    public void registerMetrics(MeterRegistry meterRegistry) {
        var paymentsMetrics = paymentRepository.getPaymentsMetricsByInterval(intervalTime);
        log.info("Actual payments metrics by {} seconds interval size = {}", intervalTime, paymentsMetrics.size());
        paymentsMetrics.forEach(dto -> Gauge.builder(Metric.PAYMENTS_COUNT.getName(), dto, getValue())
                .description(Metric.PAYMENTS_COUNT.getDescription())
                .baseUnit(Metric.PAYMENTS_COUNT.getUnit())
                .tags(getTags(dto))
                .register(meterRegistry));
    }

    private ToDoubleFunction<PaymentsMetricDto> getValue() {
        return dto -> Double.parseDouble(dto.getCount());
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
                CustomTag.status(dto.getStatus()));
    }
}
