package com.tcc.oficina_app.repository;

import com.tcc.oficina_app.model.Orcamento;
import jakarta.persistence.metamodel.SingularAttribute;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Optional;

@Repository
public interface OrcamentoRepository extends JpaRepository<Orcamento, Integer> {
    Orcamento save(Orcamento orcamento);

    Orcamento findById(int id);
}
