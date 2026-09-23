package com.tcc.oficina_app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "ordens_servico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"orcamento", "materiaisUsados"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_os")
    @EqualsAndHashCode.Include
    private Integer idOS;

    @Column(name = "data_abertura", nullable = false)
    private LocalDate dataAbertura = LocalDate.now();

    @Column(name = "data_entrega")
    private LocalDate dataEntrega;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Status status;

    @Column(name = "valor_final", precision = 12, scale = 2)
    private BigDecimal valorFinal = BigDecimal.ZERO;


    @OneToOne(optional = false)
    @JoinColumn(name = "id_orcamento", nullable = false, unique = true)
    @JsonIgnore
    private Orcamento orcamento;

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdemServicoMaterial> materiaisUsados = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "placa_veiculo")
    private Veiculo veiculo;

    // repository

    @Repository
    public interface Repo extends JpaRepository<OrdemServico, Integer> {

        List<OrdemServico> findByStatus(String status);

        List<OrdemServico> findByDataAberturaBetween(LocalDate inicio, LocalDate fim);

        List<OrdemServico> findByOrcamentoClienteIdCliente(Integer idCliente);

    }
    public enum Status {
        EM_ANDAMENTO,
        FINALIZADO,
        CANCELADO
    }

}