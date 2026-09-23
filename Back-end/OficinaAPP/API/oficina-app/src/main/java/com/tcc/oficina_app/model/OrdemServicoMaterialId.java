package com.tcc.oficina_app.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrdemServicoMaterialId implements Serializable {
    private Integer ordemServicoId;
    private Integer materialId;

}
