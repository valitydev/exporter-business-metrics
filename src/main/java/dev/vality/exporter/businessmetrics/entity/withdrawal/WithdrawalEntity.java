package dev.vality.exporter.businessmetrics.entity.withdrawal;

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
@Table(name = "withdrawal")
public class WithdrawalEntity implements Serializable {

    @EmbeddedId
    private WithdrawalPk pk;

    @Column(name = "wallet_id")
    private String walletId;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "terminal_id")
    private String terminalId;

}
