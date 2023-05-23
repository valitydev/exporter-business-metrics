package dev.vality.businessmetricexporter.metrics.provider;

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
public class PaymentsWithFailedStatusByProviderGaugeMetrics implements PaymentsGaugeMetrics {

    @Override
    public Map<Tags, Double> aggregate(List<PaymentDto> values) {
        return values.stream()
                .filter(paymentDto -> PaymentStatus.isFailedStatus(paymentDto.getStatus()))
                .collect(Collectors.groupingBy(PaymentDto::getProviderName, Collectors.counting()))
                .entrySet().stream()
                .collect(Collectors.toMap(this::getTags, e -> e.getValue().doubleValue()));
    }

    private Tags getTags(Map.Entry<String, Long> e) {
        return Tags.of(
                CustomTag.provider(e.getKey()),
                CustomTag.status(PaymentStatus.failed.name()));
    }
}
