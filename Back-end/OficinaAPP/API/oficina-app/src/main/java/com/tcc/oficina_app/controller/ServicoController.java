package com.tcc.oficina_app.controller;


import com.tcc.oficina_app.DTO.ServicoDTO;
import com.tcc.oficina_app.model.Servico;
import com.tcc.oficina_app.model.ServicoSubServico;
import com.tcc.oficina_app.services.ServicoService;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/servicos")
@RequiredArgsConstructor
public class ServicoController {
    private final ServicoService servicoService;

    private ServicoDTO toDTO(Servico servico) {
        ServicoDTO dto = new ServicoDTO();
        dto.setIdServico(servico.getId());
        dto.setNome(servico.getNome());
        dto.setDescricao(servico.getDescricao());
        dto.setValor(servico.getValor());

        List<Long> subServicoIds = servico.getSubServicos().stream()
                .map(sss -> sss.getSubServico().getId())
                .collect(Collectors.toList());
        dto.setIdsSubServicos(subServicoIds);

        List<ServicoDTO.SubServicoDetalheDTO> detalhes = servico.getSubServicos().stream()
                .map(sss -> servicoService.mapearDetalhes(sss.getSubServico()))
                .collect(Collectors.toList());
        dto.setSubServicosDetalhes(detalhes);
        return dto;
    }


    @GetMapping
    public ResponseEntity<List<ServicoDTO>> listarTodos() {
        List<ServicoDTO> servicos = servicoService.listarTodosServicos().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(servicos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicoDTO> buscarPorId(@PathVariable Long id) {
        Servico servico = servicoService.buscarServicoPorId(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));
        return ResponseEntity.ok(toDTO(servico));
    }

    @PostMapping
    public ResponseEntity<ServicoDTO> criarServico(@RequestBody ServicoDTO dto) {
        try {
            Servico novoServico = paraEntidade(dto);
            Servico servicoSalvo = servicoService.cadastrarServico(novoServico);
            return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(servicoSalvo));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<ServicoDTO> atualizarServicos(@PathVariable Long id,
                                                        @RequestBody ServicoDTO dto) {
        try {
            Servico servico = paraEntidade(dto);
            Servico atualizado = servicoService.atualizarServico(id, servico);
            return ResponseEntity.ok(toDTO(atualizado));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/sub-servicos")
    public ResponseEntity<ServicoDTO> atualizarSubServicos(@PathVariable Long id,
                                                           @RequestBody List<Long> novosIdsSubServicos) {
        try {
            Servico servico = servicoService.buscarServicoPorId(id)
                    .orElseThrow(() -> new RuntimeException("Serviço principal não encontrado"));
            List<Long> idsAtuais = new ArrayList<>();
            for (ServicoSubServico sss : servico.getSubServicos()) {
                Long idSubServico = sss.getIdSubServico();
                idsAtuais.add(idSubServico);
            }

            for (Long idAtual : idsAtuais) {
                if (!novosIdsSubServicos.contains(idAtual)) {
                    servicoService.removerSubServico(id, idAtual);
                }
            }
            for (Long idNovo : novosIdsSubServicos) {
                if (!idsAtuais.contains(idNovo)) {
                    servicoService.adicionarSubServico(id, idNovo);
                }
            }
            Servico servicoAtualizado = servicoService.buscarServicoPorId(id).get();
            return ResponseEntity.ok(toDTO(servicoAtualizado));

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirServico(@PathVariable Long id) {
        try {
            servicoService.excluirServico(id);
            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Servico paraEntidade(ServicoDTO dto) {
        Servico servico = new Servico();
        servico.setNome(dto.getNome());
        servico.setDescricao(dto.getDescricao());
        servico.setValor(dto.getValor());

        if (dto.getIdsSubServicos() != null && !dto.getIdsSubServicos().isEmpty()) {
            for (Long subServicoId : dto.getIdsSubServicos()) {
                ServicoSubServico sss = new ServicoSubServico();
                Servico subServicoReferencia = new Servico();
                subServicoReferencia.setId(subServicoId);
                sss.setSubServico(subServicoReferencia);
                servico.getSubServicos().add(sss);
            }
        }
        return servico;
    }
}
