package com.tcc.oficina_app.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"veiculos", "orcamentos"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class Cliente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    @EqualsAndHashCode.Include
    private Integer idCliente;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(length = 20)
    private String telefone;

    @Column(name = "cpf_cnpj", nullable = false, unique = true, length = 18)
    private String cpfCnpj;

    @Column(length = 100)
    private String email;

    @Column(length = 200)
    private String endereco;

    @Column(name = "inscricao_estadual", length = 30)
    private String inscricaoEstadual;

    // relacionamentos

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Veiculo> veiculos = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Orcamento> orcamentos = new ArrayList<>();


}

