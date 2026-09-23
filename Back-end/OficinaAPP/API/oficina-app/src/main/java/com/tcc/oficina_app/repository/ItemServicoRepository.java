package com.tcc.oficina_app.repository;

import com.tcc.oficina_app.model.ItemServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemServicoRepository extends JpaRepository<ItemServico, Long> {
    ItemServico save(ItemServicoRepository itemServico);
}
