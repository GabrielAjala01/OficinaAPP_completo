package com.tcc.oficina_app.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orcamentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"cliente", "itensServico", "ordemServico"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Orcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orcamento")
    @EqualsAndHashCode.Include
    private Integer idOrcamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_funcionario")
    private Funcionario funcionario;

    @Column(nullable = false)
    private LocalDate data = LocalDate.now();

    @Column(name = "valor_total", precision = 12, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusOrcamento situacao = StatusOrcamento.AGUARDANDO;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;


    @OneToMany(mappedBy = "orcamento", cascade = CascadeType.ALL)
    private List<ItemServico> itensServico = new ArrayList<>();

    @OneToOne(mappedBy = "orcamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private OrdemServico ordemServico;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "placa_veiculo", nullable = false)
    private Veiculo veiculo;


}