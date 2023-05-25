package dev.vality.exporter.businessmetrics.config;

import dev.vality.exporter.businessmetrics.model.Metric;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.MultiGauge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GaugeConfig {

    @Bean
    public MultiGauge multiGaugePaymentsCount(MeterRegistry meterRegistry) {
        return MultiGauge.builder(Metric.PAYMENTS_COUNT.getName())
                .description(Metric.PAYMENTS_COUNT.getDescription())
                .baseUnit(Metric.PAYMENTS_COUNT.getUnit())
                .register(meterRegistry);
    }
}
