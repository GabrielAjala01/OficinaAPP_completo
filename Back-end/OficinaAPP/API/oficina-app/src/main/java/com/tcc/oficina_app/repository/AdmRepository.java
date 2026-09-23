package com.tcc.oficina_app.repository;

import com.tcc.oficina_app.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdmRepository extends JpaRepository<Administrador, Integer> {
    Administrador save(Administrador administrador);
}
