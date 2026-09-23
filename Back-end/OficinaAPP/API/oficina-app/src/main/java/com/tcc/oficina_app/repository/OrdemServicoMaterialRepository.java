package com.tcc.oficina_app.repository;

import com.tcc.oficina_app.model.OrdemServicoMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdemServicoMaterialRepository extends JpaRepository<OrdemServicoMaterial, Integer> {
    OrdemServicoMaterial save(OrdemServicoMaterial ordemServicoMaterial);
}
