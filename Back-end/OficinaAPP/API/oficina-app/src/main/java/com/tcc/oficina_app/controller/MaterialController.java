package com.tcc.oficina_app.controller;

import com.tcc.oficina_app.DTO.MaterialDTO;
import com.tcc.oficina_app.DTO.MovimentacaoMaterialDTO;
import com.tcc.oficina_app.model.Material;
import com.tcc.oficina_app.services.MaterialService;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/materiais")
@RequiredArgsConstructor
public class MaterialController {
    private final MaterialService materialService;

    private MaterialDTO toDTO(Material material) {
        return new MaterialDTO(
                material.getIdMaterial(),
                material.getDescricao(),
                material.getQuantidade(),
                material.getUnidade(),
                material.getCategoria(),
                material.getValor(),
                material.getMinimo()
        );
    }

    private Material toEntity(MaterialDTO dto) {
        Material material = new Material();
        material.setIdMaterial(dto.getIdMaterial());
        material.setDescricao(dto.getDescricao());
        material.setQuantidade(dto.getQuantidade() != null ? dto.getQuantidade() : 0);
        material.setUnidade(dto.getUnidade() != null ? dto.getUnidade() : "UN");
        material.setCategoria(dto.getCategoria());
        material.setValor(dto.getValor());
        material.setMinimo(dto.getMinimo() != null ? dto.getMinimo() : 5);
        return material;
    }

    @GetMapping
    public ResponseEntity<List<MaterialDTO>> listarTodos() {
        List<MaterialDTO> lista = materialService.listarTodos()
                .stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }
    @GetMapping("/{id}")
    public ResponseEntity<MaterialDTO> buscarPorId(@PathVariable Integer id) {
        try {
            Material material = materialService.buscarPorId(id);
            return ResponseEntity.ok(toDTO(material));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping
    public ResponseEntity<MaterialDTO> cadastrar(@RequestBody MaterialDTO dto) {
        try {
            Material novoMaterial = materialService.salvar(toEntity(dto));
            return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(novoMaterial));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping("/entrada")
    public ResponseEntity<MaterialDTO> darEntrada(@RequestBody MovimentacaoMaterialDTO movimentacao) {
        try {
            Material material = materialService.adicionarEntrada(movimentacao.getDescricao(), movimentacao.getQuantidade());
            return ResponseEntity.ok(toDTO(material));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/saida")
    public ResponseEntity<MaterialDTO> registrarSaida(@RequestBody MovimentacaoMaterialDTO movimentacao) {
        try {
            Material material = materialService.registrarSaida(movimentacao.getDescricao(), movimentacao.getQuantidade());
            return ResponseEntity.ok(toDTO(material));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/abaixo-minimo")
    public ResponseEntity<List<MaterialDTO>> listarAbaixoDoMinimo() {
        List<MaterialDTO> lista = materialService.listarMateriaisAbaixoDoMinimo()
                .stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        try {
            materialService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
