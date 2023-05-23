package dev.vality.businessmetricexporter.repository;

import dev.vality.businessmetricexporter.entity.PaymentDto;
import dev.vality.businessmetricexporter.entity.PaymentPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentRepository, PaymentPk>, JpaSpecificationExecutor<PaymentRepository> {

    @Query(value = "with currppt as (with currp as ( " +
            "select p.invoice_id, p.payment_id, s.status, p.event_created_at, pi.issuer_country as country, p.currency_code" +
            "from dw.payment as p " +
            "inner join dw.payment_status_info as s" +
            "on p.invoice_id = s.invoice_id and p.payment_id = s.payment_id and s.current" +
            "left join dw.payment_payer_info as pi" +
            "on p.invoice_id = pi.invoice_id and p.payment_id = pi.payment_id" +
            "where p.event_created_at > now() - interval '?1 second'" +
            "order by p.event_created_at desc)" +
            "select currp.invoice_id, currp.payment_id, currp.status, currp.event_created_at, currp.country, currp.currency_code, pt.route_provider_id as provider_id, pt.route_terminal_id as terminal_id " +
            "from currp " +
            "inner join dw.payment_route as pt" +
            "on currp.invoice_id = pt.invoice_id and currp.payment_id = pt.payment_id and pt.current)" +
            "select currppt.invoice_id, currppt.payment_id, currppt.status, currppt.event_created_at, currppt.country, currppt.currency_code, currppt.provider_id, currppt.terminal_id, p.name as provider_name, t.name as terminal_name " +
            "from currppt" +
            "inner join dw.provider as p" +
            "on currppt.provider_id = t.provider_ref_id and p.current " +
            "inner join dw.terminal as t" +
            "on currppt.terminal_id = t.terminal_ref_id and t.current " +
            "", nativeQuery = true)
    List<PaymentDto> getPaymentDtoList(String intervalTime);

}
