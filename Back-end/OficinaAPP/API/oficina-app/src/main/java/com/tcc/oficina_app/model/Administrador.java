package com.tcc.oficina_app.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "administradores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@PrimaryKeyJoinColumn(name = "id_funcionario")
public class Administrador extends Funcionario {

    @Column(name = "nivel_acesso", length = 50)
    private String nivelAcesso = "COMPLETO";

}