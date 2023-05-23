package dev.vality.businessmetricexporter.metrics.providerterminal;

import dev.vality.businessmetricexporter.entity.PaymentDto;
import dev.vality.businessmetricexporter.metrics.PaymentsGaugeMetrics;
import dev.vality.businessmetricexporter.model.CustomTag;
import dev.vality.businessmetricexporter.model.PaymentStatus;
import io.micrometer.core.instrument.Tags;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PaymentsWithPendingStatusByProviderAndTerminalGaugeMetrics implements PaymentsGaugeMetrics {

    @Override
    public Map<Tags, Double> aggregate(List<PaymentDto> values) {
        return values.stream()
                .filter(paymentDto -> PaymentStatus.isPendingStatus(paymentDto.getStatus()))
                .collect(Collectors.groupingBy(this::getPaymentTerminalDto, Collectors.counting()))
                .entrySet().stream()
                .collect(Collectors.toMap(this::getTags, e -> e.getValue().doubleValue()));
    }

    private PaymentTerminalDto getPaymentTerminalDto(PaymentDto p) {
        return new PaymentTerminalDto(p.getProviderName(), p.getTerminalName());
    }

    private Tags getTags(Map.Entry<PaymentTerminalDto, Long> e) {
        return Tags.of(
                CustomTag.provider(e.getKey().getProviderName()),
                CustomTag.terminal(e.getKey().getTerminalName()),
                CustomTag.status(PaymentStatus.pending.name()));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaymentTerminalDto {

        private String providerName;
        private String terminalName;

    }
}
