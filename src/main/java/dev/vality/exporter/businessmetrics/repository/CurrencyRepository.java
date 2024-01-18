package dev.vality.exporter.businessmetrics.repository;

import dev.vality.exporter.businessmetrics.entity.currency.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrencyRepository extends JpaRepository<CurrencyEntity, String> {

    List<CurrencyEntity> findAllByCurrentIsTrue();

}
