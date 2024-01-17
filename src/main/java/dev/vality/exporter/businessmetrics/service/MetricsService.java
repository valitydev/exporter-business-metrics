package dev.vality.exporter.businessmetrics.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsService {

    private final PaymentService paymentService;
    private final WithdrawalService withdrawalService;
    private final CurrencyService currencyService;

    public void registerMetrics() {
        paymentService.registerMetrics();
        withdrawalService.registerMetrics();
        currencyService.registerMetrics();
    }
}
