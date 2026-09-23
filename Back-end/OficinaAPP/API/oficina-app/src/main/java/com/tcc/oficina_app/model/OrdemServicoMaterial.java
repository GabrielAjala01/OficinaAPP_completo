package com.tcc.oficina_app.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "os_materiais")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OrdemServicoMaterial {

    @EmbeddedId
    private OrdemServicoMaterialId id = new OrdemServicoMaterialId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("ordemServicoId")
    @JoinColumn(name = "id_os", nullable = false)
    private OrdemServico ordemServico;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("materialId")
    @JoinColumn(name = "codigo_material", nullable = false)
    private Material material;

    @Column(name = "qtd_usada", nullable = false)
    private Integer qtdUsada = 1;
}
