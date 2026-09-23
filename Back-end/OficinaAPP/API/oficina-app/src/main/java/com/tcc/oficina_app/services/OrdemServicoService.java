package com.tcc.oficina_app.services;

import com.tcc.oficina_app.model.*;
import com.tcc.oficina_app.repository.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrdemServicoService {
    private final OrdemServicoRepository osRepository;
    private final OrcamentoService orcamentoService;
    private final MaterialService materialService;

    public Optional<OrdemServico> buscarPorId(Integer id) {
        return osRepository.findById(id);
    }

    public List<OrdemServico> listarTodos() {
        return osRepository.findAll();
    }

    @Transactional
    public OrdemServico atualizarStatus(Integer idOS, OrdemServico.Status novoStatus) {
        OrdemServico os = buscarPorId(idOS)
                .orElseThrow(() -> new IllegalArgumentException("OS não encontrada."));

        if (novoStatus == null) {
            throw new IllegalArgumentException("O novo status não pode ser nulo.");
        }
        if (novoStatus == OrdemServico.Status.FINALIZADO) {
            os.setDataEntrega(LocalDate.now());
            BigDecimal valorCalculado = calcularValorTotalFinal(os);
            os.setValorFinal(valorCalculado);
        }

        os.setStatus(novoStatus);
        return osRepository.save(os);
    }

    @Transactional
    public void deletarOS(Integer idOS) {
        OrdemServico os = buscarPorId(idOS)
                .orElseThrow(() -> new IllegalArgumentException("OS não encontrada."));
        if (os.getStatus() == OrdemServico.Status.FINALIZADO || os.getStatus() == OrdemServico.Status.EM_ANDAMENTO) {
            throw new IllegalStateException("Não é possível excluir uma OS que já foi iniciada ou finalizada.");
        }
        osRepository.delete(os);
    }
    public BigDecimal calcularValorTotalFinal(OrdemServico os) {
        BigDecimal totalMaoDeObra = os.getOrcamento().getValorTotal();
        if (totalMaoDeObra == null) {
            totalMaoDeObra = BigDecimal.ZERO;
        }

        BigDecimal totalMateriais = os.getMateriaisUsados().stream()
                .map(osMaterial -> {
                    BigDecimal valorUnitario = osMaterial.getMaterial().getValor();
                    BigDecimal quantidade = BigDecimal.valueOf(osMaterial.getQtdUsada());
                    return valorUnitario.multiply(quantidade);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalMaoDeObra.add(totalMateriais);
    }

    @Transactional
    public OrdemServico converterOrcamentoParaOS(Integer idOrcamento) {
        Orcamento orcamento = orcamentoService.buscarPorId(idOrcamento)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado."));

        if (orcamento.getSituacao() != StatusOrcamento.APROVADO) {
            throw new IllegalStateException("O orçamento deve estar APROVADO para ser convertido em OS.");
        }

        if (orcamento.getOrdemServico() != null) {
            throw new IllegalStateException("Este orçamento já foi convertido para a OS:" + orcamento.getOrdemServico().getIdOS());
        }

        OrdemServico os = new OrdemServico();
        os.setOrcamento(orcamento);
        os.setCliente(orcamento.getCliente());
        os.setVeiculo(orcamento.getVeiculo());
        os.setDataAbertura(LocalDate.now());
        os.setStatus(OrdemServico.Status.EM_ANDAMENTO);

        OrdemServico osSalva = osRepository.save(os);

        orcamento.setSituacao(StatusOrcamento.CONVERTIDO_OS);
        orcamentoService.atualizarOrcamento(orcamento.getIdOrcamento(), StatusOrcamento.CONVERTIDO_OS, null, null);

        return osSalva;
    }

    @Transactional
    public OrdemServico declararMaterial(Integer idOS, String descricao, Integer qtdUsada) {

        OrdemServico os = buscarPorId(idOS)
                .orElseThrow(() -> new IllegalArgumentException("OS não encontrada."));

        if (os.getStatus() != OrdemServico.Status.EM_ANDAMENTO) {
            throw new IllegalStateException("Só é possível adicionar material a OS em EM_ANDAMENTO.");
        }
        Material material = materialService.registrarSaida(descricao, qtdUsada);
        if (material == null) {
            throw new IllegalStateException("Material não encontrado no estoque");
        }else{

        }

        OrdemServicoMaterial osMaterial = new OrdemServicoMaterial();
        OrdemServicoMaterialId idComp = new OrdemServicoMaterialId();
        idComp.setOrdemServicoId(os.getIdOS());
        idComp.setMaterialId(material.getIdMaterial());

        osMaterial.setId(idComp);
        osMaterial.setOrdemServico(os);
        osMaterial.setMaterial(material);
        osMaterial.setQtdUsada(qtdUsada);

        os.getMateriaisUsados().add(osMaterial);

        return osRepository.save(os);
    }
}
