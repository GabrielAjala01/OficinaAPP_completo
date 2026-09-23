package com.tcc.oficina_app.model;

import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ServicoSubServicoID implements Serializable {

    private Long idServico;
    private Long idSubServico;
}
