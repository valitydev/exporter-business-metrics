package dev.vality.exporter.businessmetrics.metrics;

import dev.vality.exporter.businessmetrics.entity.PaymentDto;
import io.micrometer.core.instrument.Tags;

import java.util.List;
import java.util.Map;

public interface PaymentsGaugeMetrics extends GaugeMetrics<PaymentDto> {

    Map<Tags, Double> aggregate(List<PaymentDto> values);

}
