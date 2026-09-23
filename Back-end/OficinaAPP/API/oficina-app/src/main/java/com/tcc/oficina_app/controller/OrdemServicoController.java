package com.tcc.oficina_app.controller;


import com.tcc.oficina_app.DTO.MovimentacaoMaterialDTO;
import com.tcc.oficina_app.DTO.OrdemServicoDTO;
import com.tcc.oficina_app.DTO.OrdemServicoMaterialDTO;
import com.tcc.oficina_app.model.OrdemServico;
import com.tcc.oficina_app.services.OrdemServicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/ordens-servico")
@RequiredArgsConstructor
public class OrdemServicoController {
    private final OrdemServicoService ordemServicoService;

    private OrdemServicoDTO toDTO(OrdemServico os) {
        OrdemServicoDTO dto = new OrdemServicoDTO();
        dto.setIdOS(os.getIdOS());
        dto.setDataAbertura(os.getDataAbertura());
        dto.setDataEntrega(os.getDataEntrega());
        dto.setStatus(os.getStatus());
        dto.setIdOrcamento(os.getOrcamento().getIdOrcamento());

        if (os.getCliente() != null) {
            dto.setIdCliente(os.getCliente().getIdCliente());
        }
        if (os.getVeiculo() != null) {
            dto.setPlacaVeiculo(os.getVeiculo().getPlaca());
        }
        dto.setMateriaisUsados(os.getMateriaisUsados().stream().map(m -> {
            return new OrdemServicoMaterialDTO(
                    m.getMaterial().getIdMaterial(),
                    m.getMaterial().getDescricao(),
                    m.getQtdUsada()
            );
        }).collect(Collectors.toList()));
        dto.setValorFinal(os.getValorFinal());
        return dto;
    }
    @GetMapping
    public ResponseEntity<List<OrdemServicoDTO>> listarTodas() {
        List<OrdemServicoDTO> listaDto = ordemServicoService.listarTodos()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaDto);
    }
    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoDTO> buscarPorId(@PathVariable Integer id) {
        return ordemServicoService.buscarPorId(id)
                .map(this::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{idOS}/materiais")
    public ResponseEntity<OrdemServicoDTO> declararMaterial(@PathVariable Integer idOS,
                                                            @RequestBody MovimentacaoMaterialDTO dto) {
        try {
            OrdemServico osAtualizada = ordemServicoService.declararMaterial(idOS, dto.getDescricao(), dto.getQuantidade());
            return ResponseEntity.ok(toDTO(osAtualizada));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PutMapping("/{id}/status")
    public ResponseEntity<OrdemServicoDTO> atualizarStatus(@PathVariable Integer id,
                                                           @RequestParam OrdemServico.Status novoStatus) {
        try {
            OrdemServico osAtualizada = ordemServicoService.atualizarStatus(id, novoStatus);
            return ResponseEntity.ok(toDTO(osAtualizada));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        try {
            ordemServicoService.deletarOS(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
