package dev.vality.exporter.businessmetrics.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentMetricDto {
    private String providerId;
    private String providerName;
    private String terminalId;
    private String terminalName;
    private String shopId;
    private String shopName;
    private String currencyCode;
    private String status;
    private String duration;
    private String count;
    private String amount;
}
