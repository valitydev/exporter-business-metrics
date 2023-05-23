package dev.vality.businessmetricexporter.service;

import dev.vality.businessmetricexporter.metrics.PaymentsGaugeMetrics;
import dev.vality.businessmetricexporter.repository.PaymentRepository;
import io.micrometer.core.instrument.MultiGauge;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    @Value("${interval.time}")
    private String intervalTime;

    private final PaymentRepository paymentRepository;
    private final List<PaymentsGaugeMetrics> paymentsGaugeMetrics;

    public void registerMetrics(MultiGauge multiGauge) {
        var actualPayments = paymentRepository.getPaymentDtoList(intervalTime);
        log.debug("Actual payments by {} seconds interval = {}", intervalTime, actualPayments);
        paymentsGaugeMetrics.stream()
                .map(comp -> comp.aggregate(actualPayments))
                .forEach(tags -> multiGauge.register(tags.entrySet().stream()
                        .map(e -> MultiGauge.Row.of(e.getKey(), e.getValue()))
                        .collect(toList())));
    }
}
