package dev.vality.exporter.businessmetrics.converter;

import dev.vality.exporter.businessmetrics.entity.payment.PaymentsAggregatedMetricDto;
import dev.vality.exporter.businessmetrics.model.PaymentMetricDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PaymentAggregatedMetricDtoToPaymentMetricDtoList implements Converter<PaymentsAggregatedMetricDto,
        List<PaymentMetricDto>> {
    @Override
    public List<PaymentMetricDto> convert(PaymentsAggregatedMetricDto source) {
        List<PaymentMetricDto> result = new ArrayList<>();
        result.add(createPaymentMetricDto(source, "5m", PaymentsAggregatedMetricDto::getCount5m,
                PaymentsAggregatedMetricDto::getAmount5m));
        result.add(createPaymentMetricDto(source, "15m", PaymentsAggregatedMetricDto::getCount15m,
                PaymentsAggregatedMetricDto::getAmount15m));
        result.add(createPaymentMetricDto(source, "30m", PaymentsAggregatedMetricDto::getCount30m,
                PaymentsAggregatedMetricDto::getAmount30m));
        result.add(createPaymentMetricDto(source, "1h", PaymentsAggregatedMetricDto::getCount1h,
                PaymentsAggregatedMetricDto::getAmount1h));
        result.add(createPaymentMetricDto(source, "3h", PaymentsAggregatedMetricDto::getCount3h,
                PaymentsAggregatedMetricDto::getAmount3h));
        result.add(createPaymentMetricDto(source, "6h", PaymentsAggregatedMetricDto::getCount6h,
                PaymentsAggregatedMetricDto::getAmount6h));
        result.add(createPaymentMetricDto(source, "12h", PaymentsAggregatedMetricDto::getCount12h,
                PaymentsAggregatedMetricDto::getAmount12h));
        result.add(createPaymentMetricDto(source, "24h", PaymentsAggregatedMetricDto::getCount24h,
                PaymentsAggregatedMetricDto::getAmount24h));
        return result;
    }

    private PaymentMetricDto createPaymentMetricDto(PaymentsAggregatedMetricDto source, String duration,
                                                    Function<PaymentsAggregatedMetricDto,
                                                            String> getStatusCountFunc,
                                                    Function<PaymentsAggregatedMetricDto,
                                                            String> getAmountFunc) {
        var dto = createCommonPart(source);
        dto.setDuration(duration);
        dto.setCount(getStatusCountFunc.apply(source));
        dto.setAmount(getAmountFunc.apply(source));
        return dto;
    }

    private PaymentMetricDto createCommonPart(PaymentsAggregatedMetricDto source) {
        return PaymentMetricDto.builder()
                .providerId(source.getProviderId())
                .providerName(source.getProviderName())
                .terminalId(source.getTerminalId())
                .terminalName(source.getTerminalName())
                .shopId(source.getShopId())
                .shopName(source.getShopName())
                .currencyCode(source.getCurrencyCode())
                .status(source.getStatus())
                .build();
    }
}
