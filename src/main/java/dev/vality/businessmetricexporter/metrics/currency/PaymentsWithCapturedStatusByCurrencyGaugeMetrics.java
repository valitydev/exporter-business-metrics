package dev.vality.businessmetricexporter.metrics.currency;

import dev.vality.businessmetricexporter.entity.PaymentDto;
import dev.vality.businessmetricexporter.metrics.PaymentsGaugeMetrics;
import dev.vality.businessmetricexporter.model.CustomTag;
import dev.vality.businessmetricexporter.model.PaymentStatus;
import io.micrometer.core.instrument.Tags;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PaymentsWithCapturedStatusByCurrencyGaugeMetrics implements PaymentsGaugeMetrics {

    @Override
    public Map<Tags, Double> aggregate(List<PaymentDto> values) {
        return values.stream()
                .filter(paymentDto -> PaymentStatus.isCapturedStatus(paymentDto.getStatus()))
                .collect(Collectors.groupingBy(PaymentDto::getCurrencyCode, Collectors.counting()))
                .entrySet().stream()
                .collect(Collectors.toMap(this::getTags, e -> e.getValue().doubleValue()));
    }

    private Tags getTags(Map.Entry<String, Long> e) {
        return Tags.of(
                CustomTag.currency(e.getKey()),
                CustomTag.status(PaymentStatus.captured.name()));
    }
}
