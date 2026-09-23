package com.tcc.oficina_app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import javax.naming.Name;
import java.util.ArrayList;
import java.util.Optional;

@Entity
@AllArgsConstructor
@IdClass(ServicoSubServicoID.class)
@Table(name = "servico_SubServico")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ServicoSubServico{

    @EqualsAndHashCode.Include
    @Id
    @Column(name= "id_servico")
    private Long idServico;

    @EqualsAndHashCode.Include
    @Id
    @Column(name = "id_subServico")
    private Long idSubServico;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_servico", insertable = false, updatable = false)
    private Servico servicoPrincipal;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_subServico", insertable = false, updatable = false)
    private Servico subServico;
}
