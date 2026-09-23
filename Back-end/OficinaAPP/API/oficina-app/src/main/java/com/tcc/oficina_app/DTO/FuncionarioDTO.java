package com.tcc.oficina_app.DTO;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FuncionarioDTO {
    private Integer idFuncionario;
    private String nome;
    private String cargo;
    private String login;
}