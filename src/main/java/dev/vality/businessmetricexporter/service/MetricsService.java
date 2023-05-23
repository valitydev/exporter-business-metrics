package dev.vality.businessmetricexporter.service;

import dev.vality.businessmetricexporter.model.Metric;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.MultiGauge;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsService {

    private final MeterRegistry meterRegistry;
    private final PaymentService paymentService;

    public void registerMetrics() {
        registerPaymentsMetrics();
    }

    private void registerPaymentsMetrics() {
        paymentService.registerMetrics(meterRegistry);
    }
}
