package com.tcc.oficina_app.services;

import com.tcc.oficina_app.DTO.ServicoDTO;
import com.tcc.oficina_app.model.Servico;
import com.tcc.oficina_app.model.ServicoSubServico;
import com.tcc.oficina_app.repository.ServicoRepository;
import com.tcc.oficina_app.repository.ServicoSubServicoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ServicoService {
    @Autowired
    private ServicoRepository servicoRepository;
    @Autowired
    private ServicoSubServicoRepository servicoSubServicoRepository;

    public Optional<Servico> buscarServicoPorId(Long id) {
        return servicoRepository.findById(id);
    }

    @Transactional
    public Servico cadastrarServico(Servico servico){
        if (servico.getNome() == null || servico.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do serviço é obrigatório.");
        }
        if (servico.getValor() == null || servico.getValor().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O valor do serviço deve ser fornecido e não pode ser negativo.");
        }
        Set<ServicoSubServico> subServicos = servico.getSubServicos();
        servico.setSubServicos(new HashSet<>());

        Servico servicoSalvo = servicoRepository.save(servico);
        servicoSalvo.setSubServicos(new HashSet<>());

        if (subServicos != null && !subServicos.isEmpty()) {

            for (ServicoSubServico sss : subServicos) {
                if (sss.getSubServico() == null || sss.getSubServico().getId() == null) {
                    throw new IllegalArgumentException(
                            "O Sub-Serviço associado deve ser um Servico existente."
                    );
                }
                Servico subServicoExistente = servicoRepository.findById(sss.getSubServico().getId())
                        .orElseThrow(() -> new RuntimeException("Sub-Serviço com ID " + sss.getSubServico().getId() + " não encontrado."));
                sss.setServicoPrincipal(servicoSalvo);
                sss.setSubServico(subServicoExistente);
                sss.setIdServico(servicoSalvo.getId());
                sss.setIdSubServico(subServicoExistente.getId());
                servicoSubServicoRepository.save(sss);
                servicoSalvo.getSubServicos().add(sss);
            }
        }

        return servicoSalvo;
    }

    public List<Servico> listarTodosServicos() {
        return servicoRepository.findAll();
    }
    @Transactional
    public void excluirServico(Long id) {
        servicoRepository.desvincularServicoDeTodos(id);
        servicoRepository.deleteById(id);
    }


    @Transactional
    public Servico adicionarSubServico(Long idServicoPrincipal, Long idSubServico) {
        Servico principal = buscarServicoPorId(idServicoPrincipal)
                .orElseThrow(() -> new RuntimeException("Serviço Principal não encontrado."));

        Servico subServico = buscarServicoPorId(idSubServico)
                .orElseThrow(() -> new RuntimeException("Sub-Serviço não encontrado."));

        ServicoSubServico novaAssociacao = new ServicoSubServico();
        novaAssociacao.setIdServico(principal.getId());
        novaAssociacao.setIdSubServico(subServico.getId());
        novaAssociacao.setServicoPrincipal(principal);
        novaAssociacao.setSubServico(subServico);

        principal.getSubServicos().add(novaAssociacao);

        return servicoRepository.save(principal);
    }

    @Transactional
    public Servico removerSubServico(Long idServicoPrincipal, Long idSubServico) {
        Servico principal = buscarServicoPorId(idServicoPrincipal)
                .orElseThrow(() -> new RuntimeException("Serviço Principal não encontrado."));

        Set<ServicoSubServico> associacaoParaRemover = principal.getSubServicos().stream()
                .filter(sss -> sss.getIdSubServico().equals(idSubServico))
                .collect(Collectors.toSet());

        if (associacaoParaRemover.isEmpty()) {
            throw new RuntimeException("Associação de Sub-Serviço não encontrada.");
        }

        principal.getSubServicos().removeAll(associacaoParaRemover);

        return servicoRepository.save(principal);
    }
    @Transactional
    public Servico atualizarServico(Long id, Servico servicoAtualizado) {
        // 1. Busca o serviço existente no banco
        Servico servicoExistente = servicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço com ID " + id + " não encontrado."));
        if (servicoAtualizado.getNome() == null || servicoAtualizado.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do serviço é obrigatório.");
        }
        if (servicoAtualizado.getValor() == null || servicoAtualizado.getValor().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O valor do serviço deve ser fornecido e não pode ser negativo.");
        }
        servicoExistente.setNome(servicoAtualizado.getNome());
        servicoExistente.setDescricao(servicoAtualizado.getDescricao());
        servicoExistente.setValor(servicoAtualizado.getValor());

        return servicoRepository.save(servicoExistente);
    }

    //mapeando recurção com DTO

    public ServicoDTO.SubServicoDetalheDTO mapearDetalhes(Servico servico) {
        ServicoDTO.SubServicoDetalheDTO dto = new ServicoDTO.SubServicoDetalheDTO();
        dto.setId(servico.getId());
        dto.setNome(servico.getNome());

        if (servico.getSubServicos() != null && !servico.getSubServicos().isEmpty()) {
            List<ServicoDTO.SubServicoDetalheDTO> filhos = servico.getSubServicos().stream()
                    .map(sss -> mapearDetalhes(sss.getSubServico()))
                    .collect(Collectors.toList());
            dto.setSubServicosDetalhes(filhos);
        }
        return dto;
    }

}
