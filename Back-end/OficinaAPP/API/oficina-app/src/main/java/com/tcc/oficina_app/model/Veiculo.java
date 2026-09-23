package com.tcc.oficina_app.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "veiculos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "cliente")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Veiculo {

    @Id
    @Column(length = 8, nullable = false)
    @EqualsAndHashCode.Include
    private String placa;

    @Column(length = 100, nullable = false)
    private String modelo;

    @Column(nullable = false)
    private Integer ano;

    @Column(length = 30)
    private String cor;

    @Column(length = 50, nullable = false)
    private String marca;

    // Relacionamento

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @OneToMany(mappedBy = "veiculo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Orcamento> orcamentos = new ArrayList<>();

}