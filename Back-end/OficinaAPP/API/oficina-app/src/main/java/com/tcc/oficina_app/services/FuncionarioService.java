package com.tcc.oficina_app.services;

import com.tcc.oficina_app.model.Funcionario;
import com.tcc.oficina_app.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;

    @Transactional
    public Funcionario salvar(Funcionario funcionario) {
        if (funcionario.getNome() == null || funcionario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é um campo obrigatório");
        }
        if (funcionario.getLogin() == null || funcionario.getLogin().trim().isEmpty()) {
            throw new IllegalArgumentException("Login é um campo obrigatório");
        }
        if (funcionario.getSenha() == null || funcionario.getSenha().trim().isEmpty()) {
            throw new IllegalArgumentException("Senha é um campo obrigatório");
        }
        String senhaCriptografada = new BCryptPasswordEncoder().encode(funcionario.getSenha());
        funcionario.setSenha(senhaCriptografada);

        return funcionarioRepository.save(funcionario);
    }

    public List<Funcionario> listarTodos() {
        return funcionarioRepository.findAll();
    }

    public Funcionario buscarPorId(Integer id) {
        return funcionarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado com ID: " + id));
    }
}