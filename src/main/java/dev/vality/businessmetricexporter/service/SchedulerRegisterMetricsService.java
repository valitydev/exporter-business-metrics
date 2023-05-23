package dev.vality.businessmetricexporter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerRegisterMetricsService {

    private final MetricsService metricsService;

    @Scheduled(cron = "${schedule.cron:-}")
    public void registerMetricsTask() {
        log.info("Start of registration of business metrics in prometheus");
        metricsService.registerMetrics();
        log.info("Finished of registration of business metrics in prometheus");
    }
}
