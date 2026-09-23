package com.tcc.oficina_app.services;

import com.tcc.oficina_app.model.*;
import com.tcc.oficina_app.repository.ItemServicoRepository;
import com.tcc.oficina_app.repository.OrcamentoRepository;
import com.tcc.oficina_app.repository.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.ByteArrayOutputStream;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
@RequiredArgsConstructor
public class OrcamentoService {
    @Autowired
    private final OrcamentoRepository orcamentoRepository;
    private final ItemServicoRepository itemServicoRepository;
    private final ServicoService servicoService;
    private final ClienteService clienteService;
    private final VeiculoService veiculoService;
    @Autowired
    private TemplateEngine templateEngine;

    public Optional<Orcamento> buscarPorId(Integer id) {
        return orcamentoRepository.findById(id);
    }
    public List<Orcamento> listarTodos() {
        return orcamentoRepository.findAll();
    }

    @Transactional
    public Orcamento criarNovoOrcamento(Integer idCliente, String placaVeiculo) {
        Cliente cliente = clienteService.buscarPorId(idCliente);
        Veiculo veiculo = veiculoService.buscarPorPlaca(placaVeiculo);

        Orcamento orcamento = new Orcamento();
        orcamento.setCliente(cliente);
        orcamento.setVeiculo(veiculo);
        orcamento.setSituacao(StatusOrcamento.AGUARDANDO);
        orcamento.setValorTotal(BigDecimal.ZERO);

        return orcamentoRepository.save(orcamento);
    }
    @Transactional
    public Orcamento atualizarOrcamento(Integer idOrcamento,
                                        StatusOrcamento novaSituacao,
                                        Integer novoIdCliente,
                                        String novaPlaca) {
        Orcamento orcamento = buscarPorId(idOrcamento)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado com ID: " + idOrcamento));

        if (novaSituacao != null) {
            orcamento.setSituacao(novaSituacao);
        }

        if (novoIdCliente != null && !orcamento.getCliente().getIdCliente().equals(novoIdCliente)) {
            Cliente novoCliente = clienteService.buscarPorId(novoIdCliente);
            orcamento.setCliente(novoCliente);
        }

        if (novaPlaca != null && !orcamento.getVeiculo().getPlaca().equalsIgnoreCase(novaPlaca)) {
            Veiculo novoVeiculo = veiculoService.buscarPorPlaca(novaPlaca);
            orcamento.setVeiculo(novoVeiculo);
        }
        return orcamentoRepository.save(orcamento);
    }

    @Transactional
    public void deletarOrcamento(Integer idOrcamento) {
        Orcamento orcamento = buscarPorId(idOrcamento)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado com ID: " + idOrcamento));

        if (orcamento.getSituacao() == StatusOrcamento.APROVADO) {
            throw new IllegalStateException("Não é possível excluir um orçamento APROVADO.");
        }
        orcamentoRepository.delete(orcamento);
    }



    @Transactional
    public Orcamento adicionarServico(Integer idOrcamento,
                                      Long idServico,
                                      BigDecimal qtdHoras,
                                      BigDecimal desconto) {
        Orcamento orcamento = buscarPorId(idOrcamento)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado."));

        Servico servicoBase = servicoService.buscarServicoPorId(idServico)
                .orElseThrow(() -> new IllegalArgumentException("Serviço base não encontrado."));

        ItemServico item = new ItemServico();
        item.setOrcamento(orcamento);
        item.setServico(servicoBase);
        item.setQtdHoras(qtdHoras.setScale(2, RoundingMode.HALF_UP));
        item.setDesconto(desconto != null ? desconto.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

        orcamento.getItensServico().add(item);
        orcamento = orcamentoRepository.save(orcamento);

        return recalcularValorTotal(orcamento);
    }
    @Transactional
    public Orcamento removerItemServico(Integer idOrcamento, Long idItemServico) {
        Orcamento orcamento = buscarPorId(idOrcamento)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado."));

        Optional<ItemServico> itemParaRemoverOpt = orcamento.getItensServico().stream()
                .filter(item -> item.getId().equals(idItemServico))
                .findFirst();

        if (itemParaRemoverOpt.isEmpty()) {
            throw new IllegalArgumentException("Item de Serviço não encontrado neste orçamento.");
        }
        ItemServico itemParaRemover = itemParaRemoverOpt.get();
        orcamento.getItensServico().remove(itemParaRemover);
        Orcamento orcamentoAtualizado = orcamentoRepository.save(orcamento);
        return recalcularValorTotal(orcamentoAtualizado);
    }

    private Orcamento recalcularValorTotal(Orcamento orcamento) {
        BigDecimal novoTotal = BigDecimal.ZERO;
        for (ItemServico item : orcamento.getItensServico()) {
            BigDecimal valorServicoBase = item.getServico().getValor();
            BigDecimal subTotal = valorServicoBase.multiply(item.getQtdHoras());
            BigDecimal valorComDesconto = subTotal.subtract(item.getDesconto());

            if (valorComDesconto.compareTo(BigDecimal.ZERO) < 0) {
                valorComDesconto = BigDecimal.ZERO;
            }
            novoTotal = novoTotal.add(valorComDesconto);
        }

        orcamento.setValorTotal(novoTotal.setScale(2, RoundingMode.HALF_UP));
        return orcamentoRepository.save(orcamento);
    }
    public byte[] gerarPdfOrcamento(Integer idOrcamento) throws Exception {
        Orcamento orcamento = orcamentoRepository.findById(idOrcamento)
                .orElseThrow(() -> new RuntimeException("Orçamento não encontrado: " + idOrcamento));

        Context context = new Context();
        context.setVariable("orcamento", orcamento);
        context.setVariable("cliente", orcamento.getCliente());
        context.setVariable("veiculo", orcamento.getVeiculo());

        context.setVariable("itens", orcamento.getItensServico());

        String html = templateEngine.process("pdf/orcamento", context);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, "/");
            builder.toStream(os);
            builder.run();

            return os.toByteArray();
        }
    }

}
