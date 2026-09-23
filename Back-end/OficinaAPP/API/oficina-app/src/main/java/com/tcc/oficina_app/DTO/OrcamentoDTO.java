package com.tcc.oficina_app.DTO;

import com.tcc.oficina_app.model.StatusOrcamento;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Data
public class OrcamentoDTO {
    private Integer idOrcamento;
    private Integer idFuncionario;
    private StatusOrcamento situacao;
    private BigDecimal valorTotal;
    private Integer idCliente;
    private String placaVeiculo;
    private List<ItemServicoDTO> itens;
}