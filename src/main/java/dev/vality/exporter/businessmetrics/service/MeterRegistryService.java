package dev.vality.exporter.businessmetrics.service;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeterRegistryService {

    private final MeterRegistry meterRegistry;

    public List<Meter.Id> getRegisteredMetrics(String name) {
        return meterRegistry.getMeters().stream()
                .filter(meter -> meter.getId().getName().equals(name))
                .filter(Gauge.class::isInstance)
                .map(meter -> (Gauge) meter)
                .map(Meter::getId)
                .collect(Collectors.toList());
    }
}
