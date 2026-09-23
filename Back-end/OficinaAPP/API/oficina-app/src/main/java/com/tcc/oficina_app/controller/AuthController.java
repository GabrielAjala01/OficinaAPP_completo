package com.tcc.oficina_app.controller;

import com.tcc.oficina_app.DTO.AutenticacaoDTO;
import com.tcc.oficina_app.DTO.TokenDTO;
import com.tcc.oficina_app.model.Funcionario;
import com.tcc.oficina_app.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> efetuarLogin(@RequestBody AutenticacaoDTO dados) {
        try {

            var authenticationToken = new UsernamePasswordAuthenticationToken(dados.getLogin(), dados.getSenha());
            var authentication = authenticationManager.authenticate(authenticationToken);
            var tokenJWT = tokenService.gerarToken((Funcionario) authentication.getPrincipal());
            return ResponseEntity.ok(new TokenDTO(tokenJWT));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}