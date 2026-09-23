package com.tcc.oficina_app.repository;

import com.tcc.oficina_app.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    Cliente save(Cliente cliente);

    @Override
    Optional<Cliente> findById(Integer integer);

    Optional<Cliente> findByCpfCnpj(String cpfCnpj);
    List<Cliente> findByNomeContainingIgnoreCase(String nome);
}
