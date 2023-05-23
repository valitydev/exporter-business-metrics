package dev.vality.businessmetricexporter.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PaymentDto {

    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "invoice_id")
    private String invoiceId;
    @Column(name = "payment_id")
    private String paymentId;
    @Column(name = "status")
    private String status;
    @Column(name = "event_created_at")
    private String createdAt;
    @Column(name = "country")
    private String country;
    @Column(name = "currency_code")
    private String currencyCode;
    @Column(name = "provider_id")
    private String providerId;
    @Column(name = "terminal_id")
    private String terminalId;
    @Column(name = "provider_name")
    private String providerName;
    @Column(name = "terminal_name")
    private String terminalName;

}
