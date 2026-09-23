package com.tcc.oficina_app.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.tcc.oficina_app.DTO.ItemServicoDTO;
import com.tcc.oficina_app.DTO.OrcamentoDTO;
import com.tcc.oficina_app.model.*;
import com.tcc.oficina_app.services.OrcamentoService;
import com.tcc.oficina_app.services.OrdemServicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orcamento")
@RequiredArgsConstructor
public class OrcamentoController {
    private final OrcamentoService orcamentoService;
    private final OrdemServicoService ordemServicoService;

    private OrcamentoDTO toDTO(Orcamento orcamento) {
        OrcamentoDTO dto = new OrcamentoDTO();
        dto.setIdOrcamento(orcamento.getIdOrcamento());
        dto.setSituacao(orcamento.getSituacao());
        dto.setValorTotal(orcamento.getValorTotal());
        dto.setIdCliente(orcamento.getCliente().getIdCliente());
        dto.setPlacaVeiculo(orcamento.getVeiculo().getPlaca());
        if (orcamento.getFuncionario() != null) {
            dto.setIdFuncionario(orcamento.getFuncionario().getIdFuncionario());
        }

        dto.setItens(orcamento.getItensServico().stream().map(item -> {
            ItemServicoDTO itemdto = new ItemServicoDTO();
            itemdto.setIdItemServico(item.getId());
            itemdto.setIdServico(item.getServico().getId());
            itemdto.setNomeServico(item.getServico().getNome());
            itemdto.setQtdHoras(item.getQtdHoras());
            itemdto.setDesconto(item.getDesconto());
            return itemdto;
        }).collect(Collectors.toList()));
        return dto;
    }

    private Orcamento toEntity(OrcamentoDTO dto) {
        Orcamento orcamento = new Orcamento();
        orcamento.setIdOrcamento(dto.getIdOrcamento());

        if (dto.getSituacao() != null) {
            orcamento.setSituacao(dto.getSituacao());
        }
        if (dto.getIdFuncionario() != null) {
            Funcionario funcionario = new Funcionario();
            funcionario.setIdFuncionario(dto.getIdFuncionario());
            orcamento.setFuncionario(funcionario);
        }
        if (dto.getIdCliente() != null) {
            Cliente cliente = new Cliente();
            cliente.setIdCliente(dto.getIdCliente());
            orcamento.setCliente(cliente);
        }
        if (dto.getPlacaVeiculo() != null) {
            Veiculo veiculo = new Veiculo();
            veiculo.setPlaca(dto.getPlacaVeiculo());
            orcamento.setVeiculo(veiculo);
        }
        if (dto.getItens() != null) {
            List<ItemServico> itens = dto.getItens().stream()
                    .map(this::toItemEntity)
                    .collect(Collectors.toList());
            itens.forEach(item -> item.setOrcamento(orcamento));
            orcamento.setItensServico(itens);
        }

        return orcamento;
    }

    private ItemServico toItemEntity(ItemServicoDTO itemDto) {
        ItemServico item = new ItemServico();
        item.setId(itemDto.getIdItemServico());
        item.setQtdHoras(itemDto.getQtdHoras());
        item.setDesconto(itemDto.getDesconto());
        if (itemDto.getIdServico() != null) {
            Servico servico = new Servico();
            servico.setId(itemDto.getIdServico());
            item.setServico(servico);
        }

        return item;
    }
    @GetMapping
    public ResponseEntity<List<OrcamentoDTO>> listarTodos() {
        List<OrcamentoDTO> lista = orcamentoService.listarTodos()
                .stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }
    @GetMapping("/{id}")
    public ResponseEntity<OrcamentoDTO> buscarPorId(@PathVariable Integer id) {
        return orcamentoService.buscarPorId(id)
                .map(this::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<OrcamentoDTO> criar(@RequestParam Integer idCliente, @RequestParam String placa) {
        try {
            Orcamento orcamento = orcamentoService.criarNovoOrcamento(idCliente, placa);
            return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(orcamento));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/itens")
    public ResponseEntity<OrcamentoDTO> adicionarItem(@PathVariable Integer id,
                                                      @RequestBody ItemServicoDTO itemDto) {
        try {
            Orcamento orcamento = orcamentoService.adicionarServico(
                    id,
                    itemDto.getIdServico(),
                    itemDto.getQtdHoras(),
                    itemDto.getDesconto()
            );
            return ResponseEntity.ok(toDTO(orcamento));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PutMapping("/atualizarSituacao/{id}")
    public ResponseEntity<OrcamentoDTO> atualizarSituacao(@PathVariable Integer id,
                                                          @RequestBody OrcamentoDTO dto){
        try{
            StatusOrcamento novaSituacao = dto.getSituacao();
            Orcamento orcamentoAtualizado = orcamentoService.atualizarOrcamento(
                    id,
                    novaSituacao,
                    null,
                    null
            );
            return ResponseEntity.ok(toDTO(orcamentoAtualizado));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}/itens/{idItem}")
    public ResponseEntity<OrcamentoDTO> removerItem(@PathVariable Integer id, @PathVariable Long idItem) {
        try {
            Orcamento orcamento = orcamentoService.removerItemServico(id, idItem);
            return ResponseEntity.ok(toDTO(orcamento));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        try {
            orcamentoService.deletarOrcamento(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/{id}/gerar-os")
    public ResponseEntity<OrdemServico> gerarOS(@PathVariable Integer id) {
        try {
            OrdemServico novaOS = ordemServicoService.converterOrcamentoParaOS(id);

            OrcamentoDTO dto = toDTO(novaOS.getOrcamento());
            return ResponseEntity.status(HttpStatus.CREATED).body(novaOS);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> gerarPdfOrcamento(@PathVariable Integer id) {
        try {
            byte[] pdf = orcamentoService.gerarPdfOrcamento(id);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=Orcamento_" + id + ".pdf");
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
