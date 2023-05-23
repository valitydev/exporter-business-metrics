package dev.vality.businessmetricexporter.metrics;

import dev.vality.businessmetricexporter.entity.PaymentDto;
import io.micrometer.core.instrument.Tags;

import java.util.List;
import java.util.Map;

public interface PaymentsGaugeMetrics extends GaugeMetrics<PaymentDto> {

    Map<Tags, Double> aggregate(List<PaymentDto> values);

}
