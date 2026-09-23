package com.tcc.oficina_app.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "itemServico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"orcamento", "servicos"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ItemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "qtd_horas", nullable = false)
    private BigDecimal qtdHoras = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal desconto = BigDecimal.ZERO;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_orcamento", nullable = false)
    private Orcamento orcamento;

    @ManyToOne
    @JoinColumn(name = "id_servico", nullable = false)
    private Servico servico;


}