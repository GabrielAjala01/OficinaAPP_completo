package com.tcc.oficina_app.services;

import com.tcc.oficina_app.model.Material;
import com.tcc.oficina_app.repository.MaterialRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;

    public Material buscarPorId(Integer id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Material não encontrado com ID: " + id));
    }

    public Material buscarPorDescricao(String descricao) {
        return materialRepository.findByDescricaoIgnoreCase(descricao)
                .orElseThrow(() -> new IllegalArgumentException("Material não encontrado com a descrição: " + descricao));
    }

    public List<Material> listarTodos() {
        return materialRepository.findAll();
    }

    @Transactional
    public Material salvar(Material material){
        if (material.getDescricao() == null || material.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("A descrição do material é obrigatória.");
        }
        if (material.getUnidade() == null || material.getUnidade().trim().isEmpty()) {
            throw new IllegalArgumentException("A unidade de medida é obrigatória.");
        }
        return materialRepository.save(material);
    }

    @Transactional
    public void deletar(Integer id) {
        Material material = buscarPorId(id);
        if (material.getQuantidade() > 0) {
            throw new IllegalStateException("Não é possível excluir material com estoque restante");
        }
        materialRepository.delete(material);
    }

    @Transactional
    public Material adicionarEntrada(String descricao, Integer quantidadeEntrada) {
        Material material = buscarPorDescricao(descricao);
        if (quantidadeEntrada <= 0) {
            throw new IllegalArgumentException("A quantidade de entrada nula.");
        }
        material.setQuantidade(material.getQuantidade() + quantidadeEntrada);

        return materialRepository.save(material);
    }
    @Transactional
    public Material registrarSaida(String descricao, Integer quantidadeSaida) {
        if (quantidadeSaida <= 0) {
            throw new IllegalArgumentException("A quantidade de saída não pode ser nula.");
        }
        Material material = buscarPorDescricao(descricao);
        if (material.getQuantidade() < quantidadeSaida) {
            throw new IllegalStateException("Erro: "+material.getDescricao()+ "\nEstoque insuficiente: " + material.getQuantidade());
        }

        material.setQuantidade(material.getQuantidade() - quantidadeSaida);
        return materialRepository.save(material);
    }

    public List<Material> listarMateriaisAbaixoDoMinimo() {
        return materialRepository.findMateriaisAbaixoDoMinimo();
    }

}
