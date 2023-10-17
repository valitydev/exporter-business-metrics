package dev.vality.exporter.businessmetrics.converter;

import dev.vality.exporter.businessmetrics.entity.withdrawal.WithdrawalsAggregatedMetricDto;
import dev.vality.exporter.businessmetrics.model.WithdrawalMetricDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class WithdrawalAggregatedMetricDtoToWithdrawalMetricDtoList implements Converter<WithdrawalsAggregatedMetricDto,
        List<WithdrawalMetricDto>> {
    @Override
    public List<WithdrawalMetricDto> convert(WithdrawalsAggregatedMetricDto source) {
        if (source.getStatusCounters().size() != source.getAmountCounters().size()) {
            throw new IllegalStateException(
                    String.format("Status counters size must be equal to amount counters size! Found %s " +
                            "statuses and %s amounts!",
                            source.getStatusCounters().size(),
                            source.getAmountCounters().size()));
        }

        return source.getStatusCounters().keySet().stream().map(duration -> WithdrawalMetricDto.builder()
                        .providerId(source.getProviderId())
                        .providerName(source.getProviderName())
                        .terminalId(source.getTerminalId())
                        .terminalName(source.getTerminalName())
                        .walletId(source.getWalletId())
                        .walletName(source.getWalletName())
                        .currencyCode(source.getCurrencyCode())
                        .status(source.getStatus())
                        .duration(duration)
                        .count(source.getStatusCounters().get(duration))
                        .amount(source.getAmountCounters().get(duration))
                        .build())
                .collect(Collectors.toList());
    }
}
