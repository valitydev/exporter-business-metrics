package dev.vality.exporter.businessmetrics.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@NamedNativeQuery(
        name = "get_payment_dto_list",
        query = """
                with p5 as (with p4 as (with p3 as (with p2 as (with p1 as (select p.invoice_id,
                                                                                   p.payment_id,
                                                                                   p.party_id,
                                                                                   p.shop_id,
                                                                                   p.currency_code,
                                                                                   p.event_created_at,
                                                                                   psi.status
                                                                            from dw.payment as p
                                                                                     inner join dw.payment_status_info as psi
                                                                                                on p.invoice_id = psi.invoice_id and
                                                                                                   p.payment_id = psi.payment_id and
                                                                                                   psi.current
                                                                            where p.event_created_at > now() - interval '?1 second'
                                                                            order by p.event_created_at desc)
                                                                select p1.*,
                                                                       ppi.issuer_country as issuer_country,
                                                                       ppi.bank_name      as issuer_bank
                                                                from p1
                                                                         left join dw.payment_payer_info as ppi
                                                                                   on p1.invoice_id = ppi.invoice_id and p1.payment_id = ppi.payment_id)
                                                    select p2.*,
                                                           pr.route_provider_id as provider_id,
                                                           pr.route_terminal_id as terminal_id
                                                    from p2
                                                             inner join dw.payment_route as pr
                                                                        on p2.invoice_id = pr.invoice_id and
                                                                           p2.payment_id = pr.payment_id and
                                                                           pr.current)
                                        select p3.*,
                                               p.name as provider_name
                                        from p3
                                                 inner join dw.provider as p
                                                            on p3.provider_id = p.provider_ref_id and p.current)
                            select p4.*,
                                   t.name as terminal_name
                            from p4
                                     inner join dw.terminal as t
                                                on p4.terminal_id = t.terminal_ref_id and t.current)
                select p5.invoice_id       as invoiceId,
                       p5.payment_id       as paymentId,
                       p5.party_id         as partyId,
                       p5.shop_id          as shopId,
                       s.details_name      as shopName,
                       p5.currency_code    as currencyCode,
                       p5.event_created_at as eventCreatedAt,
                       p5.status           as status,
                       p5.issuer_country   as issuerCountry,
                       p5.issuer_bank      as issuerBank,
                       p5.provider_id      as providerId,
                       p5.provider_name    as providerName,
                       p5.terminal_id      as terminalId,
                       p5.terminal_name    as terminalName
                from p5
                         inner join dw.shop as s
                                    on p5.shop_id = s.shop_id and s.current
                """,
        resultSetMapping = "payment_dto_list")
@SqlResultSetMapping(
        name = "payment_dto_list",
        classes = @ConstructorResult(
                targetClass = PaymentDto.class,
                columns = {
                        @ColumnResult(name = "invoiceId", type = String.class),
                        @ColumnResult(name = "paymentId", type = String.class),
                        @ColumnResult(name = "partyId", type = String.class),
                        @ColumnResult(name = "shopId", type = String.class),
                        @ColumnResult(name = "shopName", type = String.class),
                        @ColumnResult(name = "currencyCode", type = String.class),
                        @ColumnResult(name = "eventCreatedAt", type = String.class),
                        @ColumnResult(name = "status", type = String.class),
                        @ColumnResult(name = "issuerCountry", type = String.class),
                        @ColumnResult(name = "issuerBank", type = String.class),
                        @ColumnResult(name = "providerId", type = String.class),
                        @ColumnResult(name = "providerName", type = String.class),
                        @ColumnResult(name = "terminalId", type = String.class),
                        @ColumnResult(name = "terminalName", type = String.class)}))
@SuppressWarnings("LineLength")
public class PaymentDto {

    @Id
    private Long id;
    private String invoiceId;
    private String paymentId;
    private String partyId;
    private String shopId;
    private String shopName;
    private String currencyCode;
    private String eventCreatedAt;
    private String status;
    private String issuerCountry;
    private String issuerBank;
    private String providerId;
    private String providerName;
    private String terminalId;
    private String terminalName;

    public PaymentDto() {
    }

    public PaymentDto(String invoiceId, String paymentId, String partyId, String shopId, String shopName, String currencyCode, String eventCreatedAt, String status, String issuerCountry, String issuerBank, String providerId, String providerName, String terminalId, String terminalName) {
        this.invoiceId = invoiceId;
        this.paymentId = paymentId;
        this.partyId = partyId;
        this.shopId = shopId;
        this.shopName = shopName;
        this.currencyCode = currencyCode;
        this.eventCreatedAt = eventCreatedAt;
        this.status = status;
        this.issuerCountry = issuerCountry;
        this.issuerBank = issuerBank;
        this.providerId = providerId;
        this.providerName = providerName;
        this.terminalId = terminalId;
        this.terminalName = terminalName;
    }
}
