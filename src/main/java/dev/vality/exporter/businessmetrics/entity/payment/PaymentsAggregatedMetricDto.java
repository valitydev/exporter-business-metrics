package dev.vality.exporter.businessmetrics.entity.payment;

import lombok.Data;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Data
@NamedNativeQuery(
        name = "getPaymentsStatusMetrics",
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
                            p.currency_code,
                            p.amount,
                            p.created_at,
                            psi.status
                          from
                            dw.payment as p
                            inner join dw.payment_status_info as psi on p.invoice_id = psi.invoice_id
                            and p.payment_id = psi.payment_id
                            and psi.current
                          where
                            p.created_at > now() - interval '24 hour'
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
                  status,
                  count(case when created_at > now() - interval '5 minute' then 1 end) as count_5m,
                  coalesce(sum(case when created_at > now() - interval '5 minute' then amount end), 0) as amount_5m,
                  count(case when created_at > now() - interval '15 minute' then 1 end) as count_15m,
                  coalesce(sum(case when created_at > now() - interval '15 minute' then amount end), 0) as amount_15m,
                  count(case when created_at > now() - interval '30 minute' then 1 end) as count_30m,
                  coalesce(sum(case when created_at > now() - interval '30 minute' then amount end), 0) as amount_30m,
                  count(case when created_at > now() - interval '1 hour' then 1 end) as count_1h,
                  coalesce(sum(case when created_at > now() - interval '1 hour' then amount end), 0) as amount_1h,
                  count(case when created_at > now() - interval '3 hour' then 1 end) as count_3h,
                  coalesce(sum(case when created_at > now() - interval '3 hour' then amount end), 0) as amount_3h,
                  count(case when created_at > now() - interval '6 hour' then 1 end) as count_6h,
                  coalesce(sum(case when created_at > now() - interval '6 hour' then amount end), 0) as amount_6h,
                  count(case when created_at > now() - interval '12 hour' then 1 end) as count_12h,
                  coalesce(sum(case when created_at > now() - interval '12 hour' then amount end), 0) as amount_12h,
                  count(status) as count_24h,
                  sum(amount) as amount_24h
                from
                  p5
                group by
                  provider_id,
                  provider_name,
                  terminal_id,
                  terminal_name,
                  shop_id,
                  shop_name,
                  currency_code,
                  status
                """,
        resultSetMapping = "PaymentsAggregatedMetricDtoList")
@SqlResultSetMapping(
        name = "PaymentsAggregatedMetricDtoList",
        classes = @ConstructorResult(
                targetClass = PaymentsAggregatedMetricDto.class,
                columns = {
                        @ColumnResult(name = "providerId", type = String.class),
                        @ColumnResult(name = "providerName", type = String.class),
                        @ColumnResult(name = "terminalId", type = String.class),
                        @ColumnResult(name = "terminalName", type = String.class),
                        @ColumnResult(name = "shopId", type = String.class),
                        @ColumnResult(name = "shopName", type = String.class),
                        @ColumnResult(name = "currencyCode", type = String.class),
                        @ColumnResult(name = "status", type = String.class),
                        @ColumnResult(name = "count5m", type = String.class),
                        @ColumnResult(name = "amount5m", type = String.class),
                        @ColumnResult(name = "count15m", type = String.class),
                        @ColumnResult(name = "amount15m", type = String.class),
                        @ColumnResult(name = "count30m", type = String.class),
                        @ColumnResult(name = "amount30m", type = String.class),
                        @ColumnResult(name = "count1h", type = String.class),
                        @ColumnResult(name = "amount1h", type = String.class),
                        @ColumnResult(name = "count3h", type = String.class),
                        @ColumnResult(name = "amount3h", type = String.class),
                        @ColumnResult(name = "count6h", type = String.class),
                        @ColumnResult(name = "amount6h", type = String.class),
                        @ColumnResult(name = "count12h", type = String.class),
                        @ColumnResult(name = "amount12h", type = String.class),
                        @ColumnResult(name = "count24h", type = String.class),
                        @ColumnResult(name = "amount24h", type = String.class),}))
@SuppressWarnings("LineLength")
public class PaymentsAggregatedMetricDto {

    @Id
    private Long id;
    private String providerId;
    private String providerName;
    private String terminalId;
    private String terminalName;
    private String shopId;
    private String shopName;
    private String currencyCode;
    private String status;
    private Map<String, String> statusCounters;
    private Map<String, String> amountCounters;

    public PaymentsAggregatedMetricDto(String providerId, String providerName, String terminalId, String terminalName, String shopId, String shopName, String currencyCode, String status,
                                       String count5m, String amount5m,
                                       String count15m, String amount15m,
                                       String count30m, String amount30m,
                                       String count1h, String amount1h,
                                       String count3h, String amount3h,
                                       String count6h, String amount6h,
                                       String count12h, String amount12h,
                                       String count24h, String amount24h) {
        this.providerId = providerId;
        this.providerName = providerName;
        this.terminalId = terminalId;
        this.terminalName = terminalName;
        this.shopId = shopId;
        this.shopName = shopName;
        this.currencyCode = currencyCode;
        this.status = status;
        this.statusCounters = new HashMap<>();
        statusCounters.put("5m", count5m);
        statusCounters.put("15m", count15m);
        statusCounters.put("30m", count30m);
        statusCounters.put("1h", count1h);
        statusCounters.put("3h", count3h);
        statusCounters.put("6h", count6h);
        statusCounters.put("12h", count12h);
        statusCounters.put("24h", count24h);
        this.amountCounters = new HashMap<>();
        amountCounters.put("5m", amount5m);
        amountCounters.put("15m", amount15m);
        amountCounters.put("30m", amount30m);
        amountCounters.put("1h", amount1h);
        amountCounters.put("3h", amount3h);
        amountCounters.put("6h", amount6h);
        amountCounters.put("12h", amount12h);
        amountCounters.put("24h", amount24h);
    }
}
