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

    @Bean
    public MultiGauge multiGaugeWithdrawalsCount(MeterRegistry meterRegistry) {
        return MultiGauge.builder(Metric.WITHDRAWALS_COUNT.getName())
                .description(Metric.WITHDRAWALS_COUNT.getDescription())
                .baseUnit(Metric.WITHDRAWALS_COUNT.getUnit())
                .register(meterRegistry);
    }

    @Bean
    public MultiGauge multiGaugePaymentsAmount(MeterRegistry meterRegistry) {
        return MultiGauge.builder(Metric.PAYMENTS_AMOUNT.getName())
                .description(Metric.PAYMENTS_AMOUNT.getDescription())
                .baseUnit(Metric.PAYMENTS_AMOUNT.getUnit())
                .register(meterRegistry);
    }

    @Bean
    public MultiGauge multiGaugeWithdrawalsAmount(MeterRegistry meterRegistry) {
        return MultiGauge.builder(Metric.WITHDRAWALS_AMOUNT.getName())
                .description(Metric.WITHDRAWALS_AMOUNT.getDescription())
                .baseUnit(Metric.WITHDRAWALS_AMOUNT.getUnit())
                .register(meterRegistry);
    }
}
