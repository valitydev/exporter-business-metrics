package dev.vality.exporter.businessmetrics.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payment_route")
public class PaymentRouteEntity implements Serializable {

    @EmbeddedId
    private PaymentPk pk;

    @Column(name = "route_provider_id")
    private Integer providerId;

    @Column(name = "route_terminal_id")
    private Integer terminalId;

}
