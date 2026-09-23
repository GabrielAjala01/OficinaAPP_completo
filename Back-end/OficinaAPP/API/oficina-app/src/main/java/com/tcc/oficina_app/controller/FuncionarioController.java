package com.tcc.oficina_app.controller;

import com.tcc.oficina_app.DTO.FuncionarioDTO;
import com.tcc.oficina_app.model.Funcionario;
import com.tcc.oficina_app.services.FuncionarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/funcionarios")
@RequiredArgsConstructor
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    private FuncionarioDTO toDTO(Funcionario funcionario) {
        FuncionarioDTO dto = new FuncionarioDTO();
        dto.setIdFuncionario(funcionario.getIdFuncionario());
        dto.setNome(funcionario.getNome());
        dto.setCargo(funcionario.getCargo());
        dto.setLogin(funcionario.getLogin());
        return dto;
    }

    private Funcionario toEntity(FuncionarioDTO dto) {
        Funcionario funcionario = new Funcionario();
        funcionario.setIdFuncionario(dto.getIdFuncionario());
        funcionario.setNome(dto.getNome());
        funcionario.setCargo(dto.getCargo());
        funcionario.setLogin(dto.getLogin());
        return funcionario;
    }

    @PostMapping
    public ResponseEntity<FuncionarioDTO> criarFuncionario(@RequestBody FuncionarioDTO dto) {
        try {
            Funcionario funcionarioEntity = toEntity(dto);
            Funcionario funcionarioSalvo = funcionarioService.salvar(funcionarioEntity);
            return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(funcionarioSalvo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<FuncionarioDTO>> listarTodos() {
        List<FuncionarioDTO> funcionarios = funcionarioService.listarTodos().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(funcionarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioDTO> buscarPorId(@PathVariable Integer id) {
        try {
            Funcionario funcionario = funcionarioService.buscarPorId(id);
            return ResponseEntity.ok(toDTO(funcionario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
