package dev.vality.exporter.businessmetrics.repository;

import dev.vality.exporter.businessmetrics.entity.withdrawal.WithdrawalEntity;
import dev.vality.exporter.businessmetrics.entity.withdrawal.WithdrawalPk;
import dev.vality.exporter.businessmetrics.entity.withdrawal.WithdrawalsAggregatedMetricDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("LineLength")
public interface WithdrawalRepository extends JpaRepository<WithdrawalEntity, WithdrawalPk> {

    @Query(name = "getWithdrawalsMetrics", nativeQuery = true)
    List<WithdrawalsAggregatedMetricDto> getWithdrawalsMetrics();
}
