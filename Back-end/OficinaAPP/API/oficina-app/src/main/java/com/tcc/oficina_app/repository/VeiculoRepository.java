package com.tcc.oficina_app.repository;

import com.tcc.oficina_app.model.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo,String> {
    Veiculo save(Veiculo veiculo);
    Optional<Veiculo> findByPlaca(String placa);
    //List<Veiculo> findByIdCliente(Integer idCliente);
}
