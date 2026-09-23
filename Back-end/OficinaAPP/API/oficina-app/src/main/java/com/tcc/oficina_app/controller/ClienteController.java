package com.tcc.oficina_app.controller;

import com.tcc.oficina_app.DTO.ClienteDTO;
import com.tcc.oficina_app.DTO.VeiculoDTO;
import com.tcc.oficina_app.model.Cliente;
import com.tcc.oficina_app.model.Veiculo;
import com.tcc.oficina_app.services.ClienteService;
import lombok.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {
    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<ClienteDTO> criarCliente(@RequestBody ClienteDTO dto) {
        try {
            Cliente clienteEntity = toEntity(dto);
            Cliente clienteSalvo = clienteService.salvar(clienteEntity);
            return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(clienteSalvo));
        } catch (IllegalArgumentException e) {
            System.err.println("Erro de validação (400): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (DataIntegrityViolationException e) {
            System.err.println("Erro de Integridade de Dados (400): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<ClienteDTO>> listarTodos() {
        List<ClienteDTO> clientes = clienteService.listarTodos().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> buscarPorId(@PathVariable Integer id) {
        try {
            Cliente cliente = clienteService.buscarPorId(id);
            return ResponseEntity.ok(toDTO(cliente));
        } catch (IllegalArgumentException e) {
            // Cliente não encontrado
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @GetMapping("/pesquisa")
    public ResponseEntity<List<ClienteDTO>> buscarPorNome(@RequestParam String nome) {
        List<ClienteDTO> resultado = clienteService.buscarPorNome(nome).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resultado);
    }
    @GetMapping("/documento/{cpfCnpj}")
    public ResponseEntity<ClienteDTO> buscarPorCpfCnpj(@PathVariable String cpfCnpj) {
        try {
            Cliente cliente = clienteService.buscarPorCpfCnpj(cpfCnpj);
            return ResponseEntity.ok(toDTO(cliente));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> atualizarCliente(@PathVariable Integer id, @RequestBody ClienteDTO dto) {
        dto.setIdCliente(id);
        try {
            Cliente clienteEntity = toEntity(dto);
            Cliente clienteAtualizado = clienteService.salvar(clienteEntity);
            return ResponseEntity.ok(toDTO(clienteAtualizado));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Integer id) {
        try {
            clienteService.deletar(id);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private  VeiculoDTO veiculoToDTO(Veiculo veiculo) {
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


    private ClienteDTO toDTO(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setIdCliente(cliente.getIdCliente());
        dto.setNome(cliente.getNome());
        dto.setCpfCnpj(cliente.getCpfCnpj());
        dto.setTelefone(cliente.getTelefone());
        dto.setEmail(cliente.getEmail());
        dto.setEndereco(cliente.getEndereco());
        dto.setInscricaoEstadual(cliente.getInscricaoEstadual());

        if (cliente.getVeiculos() != null && !cliente.getVeiculos().isEmpty()) {
            List<VeiculoDTO> veiculosDTO = cliente.getVeiculos().stream()
                    .map(this::veiculoToDTO)
                    .collect(Collectors.toList());
            dto.setVeiculos(veiculosDTO);
        }

        return dto;
    }
    private Cliente toEntity(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(dto.getIdCliente());
        cliente.setNome(dto.getNome());
        cliente.setCpfCnpj(dto.getCpfCnpj());
        cliente.setTelefone(dto.getTelefone());
        cliente.setEmail(dto.getEmail());
        cliente.setEndereco(dto.getEndereco());
        cliente.setInscricaoEstadual(dto.getInscricaoEstadual());
        return cliente;
    }
}
