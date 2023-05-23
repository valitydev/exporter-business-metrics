package dev.vality.exporter.businessmetrics.metrics.provider;

import dev.vality.exporter.businessmetrics.entity.PaymentDto;
import dev.vality.exporter.businessmetrics.metrics.PaymentsGaugeMetrics;
import dev.vality.exporter.businessmetrics.model.CustomTag;
import dev.vality.exporter.businessmetrics.model.PaymentStatus;
import io.micrometer.core.instrument.Tags;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PaymentsWithCapturedStatusByProviderGaugeMetrics implements PaymentsGaugeMetrics {

    @Override
    public Map<Tags, Double> aggregate(List<PaymentDto> values) {
        return values.stream()
                .filter(paymentDto -> PaymentStatus.isCapturedStatus(paymentDto.getStatus()))
                .collect(Collectors.groupingBy(PaymentDto::getProviderName, Collectors.counting()))
                .entrySet().stream()
                .collect(Collectors.toMap(this::getTags, e -> e.getValue().doubleValue()));
    }

    private Tags getTags(Map.Entry<String, Long> e) {
        return Tags.of(
                CustomTag.provider(e.getKey()),
                CustomTag.status(PaymentStatus.captured.name()));
    }
}
