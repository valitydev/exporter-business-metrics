package dev.vality.exporter.businessmetrics.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawalPk implements Serializable {

    @Column(name = "withdrawal_id")
    private String withdrawalId;

    @Column(name = "sequence_id")
    private String sequenceId;

}
