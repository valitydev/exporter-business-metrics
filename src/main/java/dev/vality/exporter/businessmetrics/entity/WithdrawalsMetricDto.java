package dev.vality.exporter.businessmetrics.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@NamedNativeQuery(
        name = "getWithdrawalsMetricsByInterval",
        query = """
                with w4 as (with w3 as (with w2 as (with w1 as (select w.wallet_id,
                                                                       coalesce(w.provider_id, -1)                    as provider_id,
                                                                       cast(coalesce(w.terminal_id, '-1') as integer) as terminal_id,
                                                                       w.currency_code,
                                                                       w.withdrawal_status,
                                                                       w.amount
                                                                from dw.withdrawal as w
                                                                where w.event_created_at > :startPeriodDate
                                                                  and w.current)
                                                    select w1.*,
                                                           p.name as provider_name
                                                    from w1
                                                             inner join dw.provider as p
                                                                        on w1.provider_id = p.provider_ref_id and
                                                                           p.current)
                                        select w2.*,
                                               t.name as terminal_name
                                        from w2
                                                 inner join dw.terminal as t
                                                            on w2.terminal_id = t.terminal_ref_id and
                                                               t.current)
                            select w3.*,
                                   w.wallet_name as wallet_name
                            from w3
                                     inner join dw.wallet as w
                                                on w3.wallet_id = w.wallet_id and
                                                   w.current)
                select provider_id as providerId,
                       provider_name as providerName,
                       terminal_id as terminalId,
                       terminal_name as terminalName,
                       wallet_id as walletId,
                       wallet_name as walletName,
                       currency_code as currencyCode,
                       withdrawal_status as status,
                       count(withdrawal_status) as count,
                       sum(amount) as amount
                from w4
                group by provider_id,
                         provider_name,
                         terminal_id,
                         terminal_name,
                         wallet_id,
                         wallet_name,
                         currency_code,
                         withdrawal_status
                """,
        resultSetMapping = "WithdrawalsMetricDtoList")
@SqlResultSetMapping(
        name = "WithdrawalsMetricDtoList",
        classes = @ConstructorResult(
                targetClass = WithdrawalsMetricDto.class,
                columns = {
                        @ColumnResult(name = "providerId", type = String.class),
                        @ColumnResult(name = "providerName", type = String.class),
                        @ColumnResult(name = "terminalId", type = String.class),
                        @ColumnResult(name = "terminalName", type = String.class),
                        @ColumnResult(name = "walletId", type = String.class),
                        @ColumnResult(name = "walletName", type = String.class),
                        @ColumnResult(name = "currencyCode", type = String.class),
                        @ColumnResult(name = "status", type = String.class),
                        @ColumnResult(name = "count", type = String.class),
                        @ColumnResult(name = "amount", type = String.class)}))
@SuppressWarnings("LineLength")
public class WithdrawalsMetricDto {

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
    private String count;
    private String amount;

    public WithdrawalsMetricDto() {
    }

    public WithdrawalsMetricDto(String providerId, String providerName, String terminalId, String terminalName, String walletId, String walletName, String currencyCode, String status, String count, String amount) {
        this.providerId = providerId;
        this.providerName = providerName;
        this.terminalId = terminalId;
        this.terminalName = terminalName;
        this.walletId = walletId;
        this.walletName = walletName;
        this.currencyCode = currencyCode;
        this.status = status;
        this.count = count;
        this.amount = amount;
    }
}
