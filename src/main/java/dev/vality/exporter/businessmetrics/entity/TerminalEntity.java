package dev.vality.exporter.businessmetrics.entity;

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
@Table(name = "terminal")
public class TerminalEntity implements Serializable {

    @Id
    private Long id;

    @Column(name = "terminal_ref_id")
    private Integer terminalId;

    @Column(name = "name")
    private String name;

}
