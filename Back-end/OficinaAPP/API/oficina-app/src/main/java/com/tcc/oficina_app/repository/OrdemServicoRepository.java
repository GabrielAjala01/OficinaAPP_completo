package com.tcc.oficina_app.repository;

import com.tcc.oficina_app.model.Orcamento;
import com.tcc.oficina_app.model.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Integer> {
    OrdemServico save(OrdemServico ordemServico);
    Optional<OrdemServico> findByOrcamento(Orcamento orcamento);
}
