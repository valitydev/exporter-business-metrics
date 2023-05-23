package dev.vality.businessmetricexporter.entity;

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
@Table(name = "provider")
public class ProviderEntity implements Serializable {

    @Id
    private Long id;

    @Column(name = "provider_ref_id")
    private Integer providerId;

    @Column(name = "name")
    private String name;

}
