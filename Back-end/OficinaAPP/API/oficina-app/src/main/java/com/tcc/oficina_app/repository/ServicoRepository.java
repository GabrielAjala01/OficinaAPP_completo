package com.tcc.oficina_app.repository;

import com.tcc.oficina_app.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {

    @Modifying
    @Query(value = "DELETE FROM servico_sub_servico WHERE id_servico = :idServico OR id_sub_servico = :idServico", nativeQuery = true)
    void desvincularServicoDeTodos(@Param("idServico") Long idServico);
}
