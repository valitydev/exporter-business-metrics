package dev.vality.exporter.businessmetrics.entity.currency;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "currency")
public class CurrencyEntity implements Serializable {

    @Id
    private String id;

    @Column(name = "symbolic_code")
    private String symbolicCode;

    @Column(name = "numeric_code")
    private String numericCode;

    @Column(name = "exponent")
    private String exponent;

    @Column(name = "current")
    private Boolean current;

}
