package com.tcc.oficina_app.DTO;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServicoMaterialDTO {
    private Integer idMaterial;
    private String descricaoMaterial;
    private Integer qtdUsada;
}
