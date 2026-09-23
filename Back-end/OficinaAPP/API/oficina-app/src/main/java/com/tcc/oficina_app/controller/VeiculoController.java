package com.tcc.oficina_app.controller;


import com.tcc.oficina_app.DTO.VeiculoDTO;
import com.tcc.oficina_app.model.Cliente;
import com.tcc.oficina_app.model.Veiculo;
import com.tcc.oficina_app.services.VeiculoService;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/veiculos")
@RequiredArgsConstructor
public class VeiculoController {
    private final VeiculoService veiculoService;

    private VeiculoDTO toDTO(Veiculo veiculo) {
        VeiculoDTO dto = new VeiculoDTO();
        dto.setPlaca(veiculo.getPlaca());
        dto.setModelo(veiculo.getModelo());
        dto.setAno(veiculo.getAno());
        dto.setCor(veiculo.getCor());
        dto.setMarca(veiculo.getMarca());
        if (veiculo.getCliente() != null) {
            dto.setIdCliente(veiculo.getCliente().getIdCliente());
        }
        return dto;
    }

    private Veiculo toEntity(VeiculoDTO dto) {
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(dto.getPlaca());
        veiculo.setModelo(dto.getModelo());
        veiculo.setAno(dto.getAno());
        veiculo.setCor(dto.getCor());
        veiculo.setMarca(dto.getMarca());
        if (dto.getIdCliente() != null) {
            Cliente clienteReferencia = new Cliente();
            clienteReferencia.setIdCliente(dto.getIdCliente());
            veiculo.setCliente(clienteReferencia);
        }
        return veiculo;
    }

    @PostMapping
    public ResponseEntity<VeiculoDTO> cadastrarVeiculo(@RequestBody VeiculoDTO dto) {
        if (dto.getIdCliente() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Cliente ID é obrigatório para cadastro
        }
        try {
            Veiculo veiculoEntity = toEntity(dto);
            Veiculo veiculoSalvo = veiculoService.cadastrar(veiculoEntity, dto.getIdCliente());
            return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(veiculoSalvo));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{placa}")
    public ResponseEntity<VeiculoDTO> buscarPorPlaca(@PathVariable String placa) {
        try {
            Veiculo veiculo = veiculoService.buscarPorPlaca(placa);
            return ResponseEntity.ok(toDTO(veiculo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @PutMapping("/{placa}")
    public ResponseEntity<VeiculoDTO> atualizarVeiculo(@PathVariable String placa, @RequestBody VeiculoDTO dto) {
        try {
            Veiculo veiculoEntity = toEntity(dto);
            Veiculo veiculoAtualizado = veiculoService.atualizar(placa, veiculoEntity);
            return ResponseEntity.ok(toDTO(veiculoAtualizado));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping
    public ResponseEntity<List<VeiculoDTO>> listarTodos() {
        List<VeiculoDTO> lista = veiculoService.listarTodos().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }
    @DeleteMapping("/{placa}")
    public ResponseEntity<Void> deletarVeiculo(@PathVariable String placa) {
        try {
            veiculoService.deletar(placa);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
