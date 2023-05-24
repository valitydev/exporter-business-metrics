package dev.vality.exporter.businessmetrics.metrics.providerterminal;

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
public class PaymentsWithConversionStatusByProviderAndTerminalGaugeMetrics implements PaymentsGaugeMetrics {

    @Override
    public Map<Tags, Double> aggregate(List<PaymentDto> values) {
        return values.stream()
                .filter(paymentDto -> PaymentStatus.isConversionStatus(paymentDto.getStatus()))
                .collect(Collectors.groupingBy(this::getProviderTerminalDto, Collectors.counting()))
                .entrySet().stream()
                .collect(Collectors.toMap(this::getTags, e -> e.getValue().doubleValue()));
    }

    private ProviderTerminalDto getProviderTerminalDto(PaymentDto p) {
        return new ProviderTerminalDto(p.getProviderName(), p.getTerminalName());
    }

    private Tags getTags(Map.Entry<ProviderTerminalDto, Long> e) {
        return Tags.of(
                CustomTag.provider(e.getKey().getProviderName()),
                CustomTag.terminal(e.getKey().getTerminalName()),
                CustomTag.statusConversion());
    }
}
