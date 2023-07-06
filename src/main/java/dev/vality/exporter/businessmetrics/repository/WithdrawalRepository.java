package dev.vality.exporter.businessmetrics.repository;

import dev.vality.exporter.businessmetrics.entity.withdrawal.WithdrawalEntity;
import dev.vality.exporter.businessmetrics.entity.withdrawal.WithdrawalPk;
import dev.vality.exporter.businessmetrics.entity.withdrawal.WithdrawalsMetricDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@SuppressWarnings("LineLength")
public interface WithdrawalRepository extends JpaRepository<WithdrawalEntity, WithdrawalPk> {

    @Query(name = "getWithdrawalsMetricsByInterval", nativeQuery = true)
    List<WithdrawalsMetricDto> getWithdrawalsFinalStatusMetricsByInterval(@Param("startPeriodDate") LocalDateTime startPeriodDate);
}
