package dev.vality.businessmetricexporter.entity;

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
@Table(name = "payment")
public class PaymentEntity implements Serializable {

    @EmbeddedId
    private PaymentPk pk;

    @Column(name = "party_id")
    private String partyId;

    @Column(name = "shop_id")
    private String shopId;

}
