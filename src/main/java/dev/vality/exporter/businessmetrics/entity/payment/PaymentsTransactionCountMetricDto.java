package dev.vality.exporter.businessmetrics.entity.payment;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@NamedNativeQuery(
        name = "getPaymentsCountMetricsByInterval",
        query = """
                with p5 as (
                 with p4 as (
                   with p3 as (
                     with p2 as (
                       with p1 as (
                         select
                           p.invoice_id,
                           p.payment_id,
                           p.party_id,
                           p.shop_id,
                           p.currency_code
                         from
                           dw.payment as p
                         where
                           p.event_created_at > :startPeriodDate
                       )
                       select
                         p1.*,
                         coalesce(pr.route_provider_id, -1) as provider_id,
                         coalesce(pr.route_terminal_id, -1) as terminal_id
                       from
                         p1
                         inner join dw.payment_route as pr on p1.invoice_id = pr.invoice_id
                         and p1.payment_id = pr.payment_id
                         and pr.route_provider_id not in (1)
                         and pr.current
                     )
                     select
                       p2.*,
                       p.name as provider_name
                     from
                       p2
                       inner join dw.provider as p on p2.provider_id = p.provider_ref_id
                       and p.current
                   )
                   select
                     p3.*,
                     t.name as terminal_name
                   from
                     p3
                     inner join dw.terminal as t on p3.terminal_id = t.terminal_ref_id
                     and t.current
                 )
                 select
                   p4.*,
                   s.details_name as shop_name
                 from
                   p4
                   inner join dw.shop as s on p4.shop_id = s.shop_id
                   and s.current
               )
               select
                 provider_id as providerId,
                 provider_name as providerName,
                 terminal_id as terminalId,
                 terminal_name as terminalName,
                 shop_id as shopId,
                 shop_name as shopName,
                 currency_code as currencyCode,
                 count(payment_id) as count
               from
                 p5
               group by
                 provider_id,
                 provider_name,
                 terminal_id,
                 terminal_name,
                 shop_id,
                 shop_name,
                 currency_code
                """,
        resultSetMapping = "PaymentsTransactionCountMetricDtoList")
@SqlResultSetMapping(
        name = "PaymentsTransactionCountMetricDtoList",
        classes = @ConstructorResult(
                targetClass = PaymentsTransactionCountMetricDto.class,
                columns = {
                        @ColumnResult(name = "providerId", type = String.class),
                        @ColumnResult(name = "providerName", type = String.class),
                        @ColumnResult(name = "terminalId", type = String.class),
                        @ColumnResult(name = "terminalName", type = String.class),
                        @ColumnResult(name = "shopId", type = String.class),
                        @ColumnResult(name = "shopName", type = String.class),
                        @ColumnResult(name = "currencyCode", type = String.class),
                        @ColumnResult(name = "count", type = String.class)}))
@SuppressWarnings("LineLength")
public class PaymentsTransactionCountMetricDto {

    @Id
    private Long id;
    private String providerId;
    private String providerName;
    private String terminalId;
    private String terminalName;
    private String shopId;
    private String shopName;
    private String currencyCode;
    private String count;

    public PaymentsTransactionCountMetricDto() {
    }

    public PaymentsTransactionCountMetricDto(String providerId, String providerName, String terminalId, String terminalName, String shopId, String shopName, String currencyCode, String count) {
        this.providerId = providerId;
        this.providerName = providerName;
        this.terminalId = terminalId;
        this.terminalName = terminalName;
        this.shopId = shopId;
        this.shopName = shopName;
        this.currencyCode = currencyCode;
        this.count = count;
    }
}
