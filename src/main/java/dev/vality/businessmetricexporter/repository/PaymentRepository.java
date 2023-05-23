package dev.vality.businessmetricexporter.repository;

import dev.vality.businessmetricexporter.entity.PaymentDto;
import dev.vality.businessmetricexporter.entity.PaymentEntity;
import dev.vality.businessmetricexporter.entity.PaymentPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, PaymentPk> {

    @Query(name = "get_payment_dto_list", nativeQuery = true)
    List<PaymentDto> getPaymentDtoList(String intervalTime);

}
