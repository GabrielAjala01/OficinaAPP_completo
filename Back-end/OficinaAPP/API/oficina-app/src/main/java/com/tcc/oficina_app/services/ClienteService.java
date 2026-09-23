package com.tcc.oficina_app.services;


import com.tcc.oficina_app.model.Cliente;
import com.tcc.oficina_app.model.Orcamento;
import com.tcc.oficina_app.repository.ClienteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional
    public Cliente salvar(Cliente cliente) {
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é um campo obrigatorio");
        }
        if (cliente.getCpfCnpj() == null || cliente.getCpfCnpj().trim().isEmpty()) {
            throw new IllegalArgumentException("CPF/CNPJ é um campo obrigatório");
        }
        clienteRepository.findByCpfCnpj(cliente.getCpfCnpj())
                .ifPresent(c -> {
                    if (cliente.getIdCliente() == null || !c.getIdCliente().equals(cliente.getIdCliente())) {
                        throw new IllegalArgumentException("CPF/CNPJ já cadastrado");
                    }
                });


        return clienteRepository.save(cliente);
    }
    @Transactional(readOnly = true)
    public Cliente buscarPorId(Integer id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com ID: " + id));
    }
    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }
    public List<Cliente> buscarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return listarTodos();
        }
        return clienteRepository.findByNomeContainingIgnoreCase(nome.trim());
    }
    public Cliente buscarPorCpfCnpj(String cpfCnpj) {
        String cpfCnpjLimpo = cpfCnpj.replaceAll("[^0-9]", "");
        return clienteRepository.findByCpfCnpj(cpfCnpjLimpo)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com CPF/CNPJ: " + cpfCnpj));
    }
    @Transactional
    public void deletar(Integer id) {
        Cliente cliente = buscarPorId(id);

        if (!cliente.getVeiculos().isEmpty()) {
            throw new IllegalArgumentException("Não é possível excluir cliente que possui veículos cadastrados.");
        }
        if (!cliente.getOrcamentos().isEmpty()) {
            throw new IllegalArgumentException("Não é possível excluir cliente que possui orçamentos.");
        }
        clienteRepository.delete(cliente);
    }
    @Transactional(readOnly = true)
    public List<Orcamento> buscarHistorico(Integer idCliente) {
        Cliente cliente = buscarPorId(idCliente);
        List<Orcamento> orcamentos = cliente.getOrcamentos();
        orcamentos.sort((o1, o2) -> o2.getData().compareTo(o1.getData()));
        return orcamentos;
    }

}
