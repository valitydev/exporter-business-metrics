package dev.vality.exporter.businessmetrics.converter;

import dev.vality.exporter.businessmetrics.entity.payment.PaymentsAggregatedMetricDto;
import dev.vality.exporter.businessmetrics.model.PaymentMetricDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PaymentAggregatedMetricDtoToPaymentMetricDtoList implements Converter<PaymentsAggregatedMetricDto,
        List<PaymentMetricDto>> {
    @Override
    public List<PaymentMetricDto> convert(PaymentsAggregatedMetricDto source) {
        if (source.getStatusCounters().size() != source.getAmountCounters().size()) {
            throw new IllegalStateException(
                    String.format("Status counters size must be equal to amount counters size! Found %s " +
                            "statuses and %s amounts!",
                            source.getStatusCounters().size(),
                            source.getAmountCounters().size()));
        }

        return source.getStatusCounters().keySet().stream().map(duration -> PaymentMetricDto.builder()
                        .providerId(source.getProviderId())
                        .providerName(source.getProviderName())
                        .terminalId(source.getTerminalId())
                        .terminalName(source.getTerminalName())
                        .shopId(source.getShopId())
                        .shopName(source.getShopName())
                        .currencyCode(source.getCurrencyCode())
                        .status(source.getStatus())
                        .duration(duration)
                        .count(source.getStatusCounters().get(duration))
                        .amount(source.getAmountCounters().get(duration))
                        .build())
                .collect(Collectors.toList());
    }
}
