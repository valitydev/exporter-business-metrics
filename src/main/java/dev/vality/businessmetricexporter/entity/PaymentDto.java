package dev.vality.businessmetricexporter.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@NamedNativeQuery(
        name = "get_payment_dto_list",
        query = """
                with p4 as (with p3 as (with p2 as (with p1 as (select p.invoice_id,
                                                                       p.payment_id,
                                                                       p.currency_code,
                                                                       p.event_created_at,
                                                                       s.status
                                                                from dw.payment as p
                                                                         inner join dw.payment_status_info as s
                                                                                    on p.invoice_id = s.invoice_id and
                                                                                       p.payment_id = s.payment_id and s.current
                                                                where p.event_created_at > now() - interval '?1 second'
                                                                order by p.event_created_at desc)
                                                    select p1.*,
                                                           c.issuer_country as country
                                                    from p1
                                                             left join dw.payment_payer_info as c
                                                                       on p1.invoice_id = c.invoice_id and p1.payment_id = c.payment_id)
                                        select p2.*,
                                               pt.route_provider_id as provider_id,
                                               pt.route_terminal_id as terminal_id
                                        from p2
                                                 inner join dw.payment_route as pt
                                                            on p2.invoice_id = pt.invoice_id and p2.payment_id = pt.payment_id and
                                                               pt.current)
                            select p3.*,
                                   p.name as provider_name
                            from p3
                                     inner join dw.provider as p
                                                on p3.provider_id = p.provider_ref_id and p.current)
                select p4.invoice_id       as invoiceId,
                       p4.payment_id       as paymentId,
                       p4.currency_code    as currencyCode,
                       p4.event_created_at as createdAt,
                       p4.status           as status,
                       p4.country          as country,
                       p4.provider_id      as providerId,
                       p4.terminal_id      as terminalId,
                       p4.provider_name    as providerName,
                       t.name              as terminalName
                from p4
                         inner join dw.terminal as t
                                    on p4.terminal_id = t.terminal_ref_id and t.current
                """,
        resultSetMapping = "payment_dto_list")
@SqlResultSetMapping(
        name = "payment_dto_list",
        classes = @ConstructorResult(
                targetClass = PaymentDto.class,
                columns = {
                        @ColumnResult(name = "invoiceId", type = String.class),
                        @ColumnResult(name = "paymentId", type = String.class),
                        @ColumnResult(name = "status", type = String.class),
                        @ColumnResult(name = "createdAt", type = String.class),
                        @ColumnResult(name = "country", type = String.class),
                        @ColumnResult(name = "currencyCode", type = String.class),
                        @ColumnResult(name = "providerId", type = String.class),
                        @ColumnResult(name = "terminalId", type = String.class),
                        @ColumnResult(name = "providerName", type = String.class),
                        @ColumnResult(name = "terminalName", type = String.class)}))
@SuppressWarnings("LineLength")
public class PaymentDto {

    @Id
    private Long id;
    private String invoiceId;
    private String paymentId;
    private String status;
    private String createdAt;
    private String country;
    private String currencyCode;
    private String providerId;
    private String terminalId;
    private String providerName;
    private String terminalName;

    public PaymentDto() {
    }

    public PaymentDto(String invoiceId, String paymentId, String status, String createdAt, String country, String currencyCode, String providerId, String terminalId, String providerName, String terminalName) {
        this.invoiceId = invoiceId;
        this.paymentId = paymentId;
        this.status = status;
        this.createdAt = createdAt;
        this.country = country;
        this.currencyCode = currencyCode;
        this.providerId = providerId;
        this.terminalId = terminalId;
        this.providerName = providerName;
        this.terminalName = terminalName;
    }
}
