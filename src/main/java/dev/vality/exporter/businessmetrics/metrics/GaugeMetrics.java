package dev.vality.exporter.businessmetrics.metrics;

import io.micrometer.core.instrument.Tags;

import java.util.List;
import java.util.Map;

public interface GaugeMetrics<T> {

    Map<Tags, Double> aggregate(List<T> values);

}
