package com.tcc.oficina_app.repository;

import com.tcc.oficina_app.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Integer> {
    Material save(Material material);

    @Query("SELECT m FROM Material m WHERE m.quantidade <= m.minimo")
    List<Material> findMateriaisAbaixoDoMinimo();


    Optional<Material> findByDescricaoIgnoreCase(String descricao);

}
