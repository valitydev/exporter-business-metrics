package dev.vality.exporter.businessmetrics.converter;

import dev.vality.exporter.businessmetrics.entity.withdrawal.WithdrawalsAggregatedMetricDto;
import dev.vality.exporter.businessmetrics.model.WithdrawalMetricDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
public class WithdrawalAggregatedMetricDtoToWithdrawalMetricDtoList implements Converter<WithdrawalsAggregatedMetricDto,
        List<WithdrawalMetricDto>> {
    @Override
    public List<WithdrawalMetricDto> convert(WithdrawalsAggregatedMetricDto source) {
        List<WithdrawalMetricDto> result = new ArrayList<>();
        result.add(createWithdrawalMetricDto(source, "5m", WithdrawalsAggregatedMetricDto::getCount5m,
                WithdrawalsAggregatedMetricDto::getAmount5m));
        result.add(createWithdrawalMetricDto(source, "15m", WithdrawalsAggregatedMetricDto::getCount15m,
                WithdrawalsAggregatedMetricDto::getAmount15m));
        result.add(createWithdrawalMetricDto(source, "30m", WithdrawalsAggregatedMetricDto::getCount30m,
                WithdrawalsAggregatedMetricDto::getAmount30m));
        result.add(createWithdrawalMetricDto(source, "1h", WithdrawalsAggregatedMetricDto::getCount1h,
                WithdrawalsAggregatedMetricDto::getAmount1h));
        result.add(createWithdrawalMetricDto(source, "3h", WithdrawalsAggregatedMetricDto::getCount3h,
                WithdrawalsAggregatedMetricDto::getAmount3h));
        result.add(createWithdrawalMetricDto(source, "6h", WithdrawalsAggregatedMetricDto::getCount6h,
                WithdrawalsAggregatedMetricDto::getAmount6h));
        result.add(createWithdrawalMetricDto(source, "12h", WithdrawalsAggregatedMetricDto::getCount12h,
                WithdrawalsAggregatedMetricDto::getAmount12h));
        result.add(createWithdrawalMetricDto(source, "24h", WithdrawalsAggregatedMetricDto::getCount24h,
                WithdrawalsAggregatedMetricDto::getAmount24h));
        result.add(createWithdrawalMetricDto(source, "today_msk", WithdrawalsAggregatedMetricDto::getCountTodayMsk,
                WithdrawalsAggregatedMetricDto::getAmountTodayMsk));
        return result;
    }

    private WithdrawalMetricDto createWithdrawalMetricDto(WithdrawalsAggregatedMetricDto source, String duration,
                                                          Function<WithdrawalsAggregatedMetricDto,
                                                                  String> getStatusCountFunc,
                                                          Function<WithdrawalsAggregatedMetricDto,
                                                                  String> getAmountFunc) {
        var dto = createCommonPart(source);
        dto.setDuration(duration);
        dto.setCount(getStatusCountFunc.apply(source));
        dto.setAmount(getAmountFunc.apply(source));
        return dto;
    }

    private WithdrawalMetricDto createCommonPart(WithdrawalsAggregatedMetricDto source) {
        return WithdrawalMetricDto.builder()
                .providerId(source.getProviderId())
                .providerName(source.getProviderName())
                .terminalId(source.getTerminalId())
                .terminalName(source.getTerminalName())
                .walletId(source.getWalletId())
                .walletName(source.getWalletName())
                .currencyCode(source.getCurrencyCode())
                .status(source.getStatus())
                .build();
    }
}
