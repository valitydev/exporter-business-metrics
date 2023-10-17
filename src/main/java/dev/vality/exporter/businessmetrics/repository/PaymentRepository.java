package dev.vality.exporter.businessmetrics.repository;

import dev.vality.exporter.businessmetrics.entity.payment.PaymentEntity;
import dev.vality.exporter.businessmetrics.entity.payment.PaymentPk;
import dev.vality.exporter.businessmetrics.entity.payment.PaymentsAggregatedMetricDto;
import dev.vality.exporter.businessmetrics.entity.payment.PaymentsTransactionCountMetricDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@SuppressWarnings("LineLength")
public interface PaymentRepository extends JpaRepository<PaymentEntity, PaymentPk> {

    @Query(name = "getPaymentsStatusMetrics", nativeQuery = true)
    List<PaymentsAggregatedMetricDto> getPaymentsStatusMetrics();

    @Query(name = "getPaymentsCountMetricsByInterval", nativeQuery = true)
    List<PaymentsTransactionCountMetricDto> getPaymentsCountMetricsByInterval(@Param("startPeriodDate") LocalDateTime startPeriodDate);

}
