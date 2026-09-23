package com.tcc.oficina_app.repository;

import com.tcc.oficina_app.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Integer> {
    Funcionario save(Funcionario funcionario);

    Optional<Funcionario> findByLogin(String login);
}
