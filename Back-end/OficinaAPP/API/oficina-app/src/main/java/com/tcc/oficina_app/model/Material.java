package com.tcc.oficina_app.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "materiais")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "ordensServico")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length = 20, nullable = false)
    @EqualsAndHashCode.Include
    private Integer idMaterial;

    @Column(nullable = false, length = 150)
    private String descricao;

    @Column(nullable = false)
    private Integer quantidade = 0;

    @Column(length = 20, nullable = false)
    private String unidade = "UN"; // UN, LT, KG, MT...

    @Column(length = 50)
    private String categoria;

    @Column(precision = 12, scale = 2)
    private BigDecimal valor = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer minimo = 5;

}