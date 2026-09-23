package com.tcc.oficina_app.DTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
public class ClienteDTO {
    private Integer idCliente;
    private String nome;
    private String telefone;
    private String cpfCnpj;
    private String email;
    private String endereco;
    private String inscricaoEstadual;
    private List<VeiculoDTO> veiculos;
}
