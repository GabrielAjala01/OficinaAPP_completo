package com.tcc.oficina_app.services;


import com.tcc.oficina_app.model.Cliente;
import com.tcc.oficina_app.model.Orcamento;
import com.tcc.oficina_app.model.Veiculo;
import com.tcc.oficina_app.repository.ClienteRepository;
import com.tcc.oficina_app.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VeiculoService {
    private final VeiculoRepository veiculoRepository;
    private final ClienteRepository clienteRepository;

    @Transactional
    public Veiculo cadastrar(Veiculo veiculo, Integer idCliente) {

        if (veiculo.getPlaca() == null || veiculo.getPlaca().trim().isEmpty()) {
            throw new IllegalArgumentException("Placa é obrigatória para cadastro de veículo.");
        }
        if (veiculo.getMarca() == null || veiculo.getMarca().trim().isEmpty()) {
            throw new IllegalArgumentException("Marca é obrigatória para cadastro de veículo.");
        }
        if (veiculo.getModelo() == null || veiculo.getModelo().trim().isEmpty()) {
            throw new IllegalArgumentException("Modelo é obrigatório para cadastro de veículo.");
        }
        if (veiculo.getAno() == null || veiculo.getAno() < 1900 || veiculo.getAno() > java.time.Year.now().getValue() + 1) {
            throw new IllegalArgumentException("Ano do veículo inválido (deve ser entre 1900 e ano atual +1).");
        }
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado para vincular veículo."));
        veiculo.setCliente(cliente);
        veiculo.setPlaca(veiculo.getPlaca().toUpperCase().replaceAll("\\s+", ""));
        return veiculoRepository.save(veiculo);
    }
    @Transactional
    public Veiculo atualizar(String placa, Veiculo dadosAtualizados) {
        Veiculo veiculo = veiculoRepository.findByPlaca(placa)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado com placa: " + placa));


        if (dadosAtualizados.getMarca() != null) veiculo.setMarca(dadosAtualizados.getMarca());
        if (dadosAtualizados.getModelo() != null) veiculo.setModelo(dadosAtualizados.getModelo());
        if (dadosAtualizados.getAno() != null) veiculo.setAno(dadosAtualizados.getAno());
        if (dadosAtualizados.getCor() != null) veiculo.setCor(dadosAtualizados.getCor());

        return veiculoRepository.save(veiculo);
    }


    public List<Orcamento> getHistoricoAtendimentos(String placa) {
        Veiculo veiculo = veiculoRepository.findByPlaca(placa)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado."));
        return veiculo.getOrcamentos();
    }

    public Veiculo buscarPorPlaca(String placa) {
        return veiculoRepository.findByPlaca(placa.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado com placa: " + placa));
    }
    public List<Veiculo> listarTodos() {
        return veiculoRepository.findAll();
    }

    @Transactional
    public void deletar(String placa) {
        Veiculo veiculo = buscarPorPlaca(placa);
        if (!veiculo.getOrcamentos().isEmpty()) {
            throw new IllegalArgumentException("Não é possível excluir veículo com histórico de atendimentos (orçamentos ou OS).");
        }
        veiculoRepository.delete(veiculo);
    }
}
