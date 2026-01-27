package com.example.bibliotecaapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BibliotecaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BibliotecaApiApplication.class, args);
    }

}
