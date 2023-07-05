package dev.vality.exporter.businessmetrics.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@NamedNativeQuery(
        name = "getPaymentsMetricsByInterval",
        query = """
                with p6 as (with p5 as (with p4 as (with p3 as (with p2 as (with p1 as (select p.invoice_id,
                                                                                               p.payment_id,
                                                                                               p.party_id,
                                                                                               p.shop_id,
                                                                                               p.currency_code,
                                                                                               p.amount,
                                                                                               psi.status
                                                                                        from dw.payment as p
                                                                                                 inner join dw.payment_status_info as psi
                                                                                                            on p.invoice_id =
                                                                                                               psi.invoice_id and
                                                                                                               p.payment_id =
                                                                                                               psi.payment_id and
                                                                                                               psi.current
                                                                                        where p.event_created_at > :startPeriodDate)
                                                                            select p1.*,
                                                                                   coalesce(ppi.issuer_country, 'undefined')           as issuer_country,
                                                                                   coalesce(ppi.bank_name, 'undefined')                as issuer_bank,
                                                                                   coalesce(ppi.bank_card_payment_system, 'undefined') as issuer_bank_card_payment_system
                                                                            from p1
                                                                                     left join dw.payment_payer_info as ppi
                                                                                               on p1.invoice_id = ppi.invoice_id and
                                                                                                  p1.payment_id = ppi.payment_id)
                                                                select p2.*,
                                                                       coalesce(pr.route_provider_id, -1) as provider_id,
                                                                       coalesce(pr.route_terminal_id, -1) as terminal_id
                                                                from p2
                                                                         inner join dw.payment_route as pr
                                                                                    on p2.invoice_id = pr.invoice_id and
                                                                                       p2.payment_id = pr.payment_id and
                                                                                       pr.route_provider_id not in (1) and
                                                                                       pr.current)
                                                    select p3.*,
                                                           p.name as provider_name
                                                    from p3
                                                             inner join dw.provider as p
                                                                        on p3.provider_id = p.provider_ref_id and
                                                                           p.current)
                                        select p4.*,
                                               t.name as terminal_name
                                        from p4
                                                 inner join dw.terminal as t
                                                            on p4.terminal_id = t.terminal_ref_id and
                                                               t.current)
                            select p5.*,
                                   s.details_name as shop_name
                            from p5
                                     inner join dw.shop as s
                                                on p5.shop_id = s.shop_id and
                                                   s.current)
                select provider_id as providerId,
                       provider_name as providerName,
                       terminal_id as terminalId,
                       terminal_name as terminalName,
                       shop_id as shopId,
                       shop_name as shopName,
                       currency_code as currencyCode,
                       issuer_country as issuerCountry,
                       issuer_bank as issuerBank,
                       issuer_bank_card_payment_system as issuerBankCardPaymentSystem,
                       status,
                       count(status) as count,
                       sum(amount) as amount
                from p6
                group by provider_id,
                         provider_name,
                         terminal_id,
                         terminal_name,
                         shop_id,
                         shop_name,
                         currency_code,
                         issuer_country,
                         issuer_bank,
                         issuer_bank_card_payment_system,
                         status
                """,
        resultSetMapping = "PaymentsMetricDtoList")
@SqlResultSetMapping(
        name = "PaymentsMetricDtoList",
        classes = @ConstructorResult(
                targetClass = PaymentsMetricDto.class,
                columns = {
                        @ColumnResult(name = "providerId", type = String.class),
                        @ColumnResult(name = "providerName", type = String.class),
                        @ColumnResult(name = "terminalId", type = String.class),
                        @ColumnResult(name = "terminalName", type = String.class),
                        @ColumnResult(name = "shopId", type = String.class),
                        @ColumnResult(name = "shopName", type = String.class),
                        @ColumnResult(name = "currencyCode", type = String.class),
                        @ColumnResult(name = "issuerCountry", type = String.class),
                        @ColumnResult(name = "issuerBank", type = String.class),
                        @ColumnResult(name = "issuerBankCardPaymentSystem", type = String.class),
                        @ColumnResult(name = "status", type = String.class),
                        @ColumnResult(name = "count", type = String.class),
                        @ColumnResult(name = "amount", type = String.class)}))
@SuppressWarnings("LineLength")
public class PaymentsMetricDto {

    @Id
    private Long id;
    @JsonProperty("provider_id")
    private String providerId;
    @JsonIgnore
    private String providerName;
    @JsonProperty("terminal_id")
    private String terminalId;
    @JsonIgnore
    private String terminalName;
    @JsonProperty("shop_id")
    private String shopId;
    @JsonIgnore
    private String shopName;
    @JsonIgnore
    private String currencyCode;
    @JsonIgnore
    private String issuerCountry;
    @JsonIgnore
    private String issuerBank;
    @JsonIgnore
    private String issuerBankCardPaymentSystem;
    @JsonProperty("status")
    private String status;
    @JsonProperty("count")
    private String count;
    @JsonProperty("amount")
    private String amount;

    public PaymentsMetricDto() {
    }

    public PaymentsMetricDto(String providerId, String providerName, String terminalId, String terminalName, String shopId, String shopName, String currencyCode, String issuerCountry, String issuerBank, String issuerBankCardPaymentSystem, String status, String count, String amount) {
        this.providerId = providerId;
        this.providerName = providerName;
        this.terminalId = terminalId;
        this.terminalName = terminalName;
        this.shopId = shopId;
        this.shopName = shopName;
        this.currencyCode = currencyCode;
        this.issuerCountry = issuerCountry;
        this.issuerBank = issuerBank;
        this.issuerBankCardPaymentSystem = issuerBankCardPaymentSystem;
        this.status = status;
        this.count = count;
        this.amount = amount;
    }
}
