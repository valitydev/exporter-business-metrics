package dev.vality.exporter.businessmetrics.repository;

import dev.vality.exporter.businessmetrics.entity.PaymentDto;
import dev.vality.exporter.businessmetrics.entity.PaymentEntity;
import dev.vality.exporter.businessmetrics.entity.PaymentPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, PaymentPk> {

    @Query(name = "get_payment_dto_list", nativeQuery = true)
    List<PaymentDto> getPaymentDtoList(String intervalTime);

}
