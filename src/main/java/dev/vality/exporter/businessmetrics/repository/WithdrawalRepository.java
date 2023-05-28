package dev.vality.exporter.businessmetrics.repository;

import dev.vality.exporter.businessmetrics.entity.WithdrawalEntity;
import dev.vality.exporter.businessmetrics.entity.WithdrawalPk;
import dev.vality.exporter.businessmetrics.entity.WithdrawalsMetricDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WithdrawalRepository extends JpaRepository<WithdrawalEntity, WithdrawalPk> {

    @Query(name = "getWithdrawalsMetricsByInterval", nativeQuery = true)
    List<WithdrawalsMetricDto> getWithdrawalsMetricsByInterval(@Param("startPeriodDate") LocalDateTime startPeriodDate);

}
