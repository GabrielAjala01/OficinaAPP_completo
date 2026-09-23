package com.tcc.oficina_app.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ItemServicoDTO {
    private Long idItemServico;
    private Long idServico;
    private BigDecimal qtdHoras;
    private BigDecimal desconto;
    private String nomeServico;
}
