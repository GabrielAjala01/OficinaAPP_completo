package com.tcc.oficina_app.DTO;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaterialDTO {
    private Integer idMaterial;
    private String descricao;
    private Integer quantidade;
    private String unidade;
    private String categoria;
    private BigDecimal valor;
    private Integer minimo;

}
