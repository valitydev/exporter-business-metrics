package dev.vality.exporter.businessmetrics.entity.withdrawal;

import lombok.Data;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Data
@Entity
@NamedNativeQuery(
        name = "getWithdrawalsMetrics",
        query = """
                   
                 with w4 as (
                  with w3 as (
                    with w2 as (
                      with w1 as (
                        select
                          w.wallet_id,
                          coalesce(w.provider_id, -1) as provider_id,
                          cast(coalesce(w.terminal_id, '-1') as integer) as terminal_id,
                          w.currency_code,
                          w.withdrawal_status,
                          w.amount,
                          w.event_occured_at
                        from
                          dw.withdrawal as w
                        where
                          w.event_occured_at > now() - interval '24 hour'
                          and w.current
                      )
                      select
                        w1.*,
                        p.name as provider_name
                      from
                        w1
                        inner join dw.provider as p on w1.provider_id = p.provider_ref_id
                        and p.current
                    )
                    select
                      w2.*,
                      t.name as terminal_name
                    from
                      w2
                      inner join dw.terminal as t on w2.terminal_id = t.terminal_ref_id
                      and t.current
                  )
                  select
                    w3.*,
                    w.wallet_name as wallet_name
                  from
                    w3
                    inner join dw.wallet as w on w3.wallet_id = w.wallet_id
                    and w.current
                )
                select
                  provider_id as providerId,
                  provider_name as providerName,
                  terminal_id as terminalId,
                  terminal_name as terminalName,
                  wallet_id as walletId,
                  wallet_name as walletName,
                  currency_code as currencyCode,
                  withdrawal_status as status,
                  count(case when event_occured_at > now() - interval '5 minute' then 1 end) as count_5m,
                  coalesce(sum(case when event_occured_at > now() - interval '5 minute' then amount end), 0) as amount_5m,
                  count(case when event_occured_at > now() - interval '15 minute' then 1 end) as count_15m,
                  coalesce(sum(case when event_occured_at > now() - interval '15 minute' then amount end), 0) as amount_15m,
                  count(case when event_occured_at > now() - interval '30 minute' then 1 end) as count_30m,
                  coalesce(sum(case when event_occured_at > now() - interval '30 minute' then amount end), 0) as amount_30m,
                  count(case when event_occured_at > now() - interval '1 hour' then 1 end) as count_1h,
                  coalesce(sum(case when event_occured_at > now() - interval '1 hour' then amount end), 0) as amount_1h,
                  count(case when event_occured_at > now() - interval '3 hour' then 1 end) as count_3h,
                  coalesce(sum(case when event_occured_at > now() - interval '3 hour' then amount end), 0) as amount_3h,
                  count(case when event_occured_at > now() - interval '6 hour' then 1 end) as count_6h,
                  coalesce(sum(case when event_occured_at > now() - interval '6 hour' then amount end), 0) as amount_6h,
                  count(case when event_occured_at > now() - interval '12 hour' then 1 end) as count_12h,
                  coalesce(sum(case when event_occured_at > now() - interval '12 hour' then amount end), 0) as amount_12h,
                  count(withdrawal_status) as count_24h,
                  sum(amount) as amount_24h,
                  count(case when event_occured_at > current_date at time zone 'Europe/Moscow' then 1 end) as count_today_msk,
                  coalesce(sum(case when event_occured_at > current_date at time zone 'Europe/Moscow' then amount end), 0) as amount_today_msk
                from
                  w4
                group by
                  provider_id,
                  provider_name,
                  terminal_id,
                  terminal_name,
                  wallet_id,
                  wallet_name,
                  currency_code,
                  withdrawal_status
                """,
        resultSetMapping = "WithdrawalsAggregatedMetricDtoList")
@SqlResultSetMapping(
        name = "WithdrawalsAggregatedMetricDtoList",
        classes = @ConstructorResult(
                targetClass = WithdrawalsAggregatedMetricDto.class,
                columns = {
                        @ColumnResult(name = "providerId", type = String.class),
                        @ColumnResult(name = "providerName", type = String.class),
                        @ColumnResult(name = "terminalId", type = String.class),
                        @ColumnResult(name = "terminalName", type = String.class),
                        @ColumnResult(name = "walletId", type = String.class),
                        @ColumnResult(name = "walletName", type = String.class),
                        @ColumnResult(name = "currencyCode", type = String.class),
                        @ColumnResult(name = "status", type = String.class),
                        @ColumnResult(name = "count_5m", type = String.class),
                        @ColumnResult(name = "amount_5m", type = String.class),
                        @ColumnResult(name = "count_15m", type = String.class),
                        @ColumnResult(name = "amount_15m", type = String.class),
                        @ColumnResult(name = "count_30m", type = String.class),
                        @ColumnResult(name = "amount_30m", type = String.class),
                        @ColumnResult(name = "count_1h", type = String.class),
                        @ColumnResult(name = "amount_1h", type = String.class),
                        @ColumnResult(name = "count_3h", type = String.class),
                        @ColumnResult(name = "amount_3h", type = String.class),
                        @ColumnResult(name = "count_6h", type = String.class),
                        @ColumnResult(name = "amount_6h", type = String.class),
                        @ColumnResult(name = "count_12h", type = String.class),
                        @ColumnResult(name = "amount_12h", type = String.class),
                        @ColumnResult(name = "count_24h", type = String.class),
                        @ColumnResult(name = "amount_24h", type = String.class),
                        @ColumnResult(name = "count_today_msk", type = String.class),
                        @ColumnResult(name = "amount_today_msk", type = String.class)}))
@SuppressWarnings("LineLength")
public class WithdrawalsAggregatedMetricDto {

    @Id
    private Long id;
    private String providerId;
    private String providerName;
    private String terminalId;
    private String terminalName;
    private String walletId;
    private String walletName;
    private String currencyCode;
    private String status;
    private String count5m;
    private String amount5m;
    private String count15m;
    private String amount15m;
    private String count30m;
    private String amount30m;
    private String count1h;
    private String amount1h;
    private String count3h;
    private String amount3h;
    private String count6h;
    private String amount6h;
    private String count12h;
    private String amount12h;
    private String count24h;
    private String amount24h;
    private String countTodayMsk;
    private String amountTodayMsk;

    public WithdrawalsAggregatedMetricDto(String providerId, String providerName, String terminalId,
                                          String terminalName, String walletId, String walletName,
                                          String currencyCode, String status,
                                          String count5m, String amount5m,
                                          String count15m, String amount15m,
                                          String count30m, String amount30m,
                                          String count1h, String amount1h,
                                          String count3h, String amount3h,
                                          String count6h, String amount6h,
                                          String count12h, String amount12h,
                                          String count24h, String amount24h,
                                          String countTodayMsk, String amountTodayMsk) {
        this.providerId = providerId;
        this.providerName = providerName;
        this.terminalId = terminalId;
        this.terminalName = terminalName;
        this.walletId = walletId;
        this.walletName = walletName;
        this.currencyCode = currencyCode;
        this.status = status;
        this.count5m = count5m;
        this.amount5m = amount5m;
        this.count15m = count15m;
        this.amount15m = amount15m;
        this.count30m = count30m;
        this.amount30m = amount30m;
        this.count1h = count1h;
        this.amount1h = amount1h;
        this.count3h = count3h;
        this.amount3h = amount3h;
        this.count6h = count6h;
        this.amount6h = amount6h;
        this.count12h = count12h;
        this.amount12h = amount12h;
        this.count24h = count24h;
        this.amount24h = amount24h;
        this.countTodayMsk = countTodayMsk;
        this.amountTodayMsk = amountTodayMsk;
    }

    public WithdrawalsAggregatedMetricDto() {

    }
}
