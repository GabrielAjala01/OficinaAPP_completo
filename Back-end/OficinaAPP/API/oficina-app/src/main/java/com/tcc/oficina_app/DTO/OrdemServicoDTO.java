package com.tcc.oficina_app.DTO;

import com.tcc.oficina_app.model.OrdemServico.Status;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class OrdemServicoDTO {
    private Integer idOS;
    private LocalDate dataAbertura;
    private LocalDate dataEntrega;
    private Status status;
    private Integer idOrcamento;
    private Integer idCliente;
    private String placaVeiculo;
    private List<OrdemServicoMaterialDTO> materiaisUsados;
    private BigDecimal valorFinal;
}