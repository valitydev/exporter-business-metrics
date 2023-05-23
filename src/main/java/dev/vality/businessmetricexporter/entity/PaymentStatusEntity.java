package dev.vality.businessmetricexporter.entity;

import dev.vality.businessmetricexporter.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payment_status_info")
public class PaymentStatusEntity implements Serializable {

    @EmbeddedId
    private PaymentPk pk;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PaymentStatus status;

}
