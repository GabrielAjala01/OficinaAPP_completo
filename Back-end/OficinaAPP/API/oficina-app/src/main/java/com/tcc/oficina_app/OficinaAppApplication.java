package com.tcc.oficina_app;

import com.tcc.oficina_app.model.Funcionario;
import com.tcc.oficina_app.repository.FuncionarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class OficinaAppApplication {
    public static void main(String[] args) {

        SpringApplication.run(OficinaAppApplication.class, args);
    }

}
