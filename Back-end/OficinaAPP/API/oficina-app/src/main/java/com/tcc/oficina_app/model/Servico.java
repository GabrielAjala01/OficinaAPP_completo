package com.tcc.oficina_app.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "servicos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Servico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", length = 100)
    private String nome;

    @Column(nullable = false)
    private BigDecimal valor = BigDecimal.ZERO;

    @Column(name = "descricao", length = 500)
    private String descricao;

    @OneToMany(mappedBy = "servicoPrincipal", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ServicoSubServico> subServicos = new HashSet<>();



}