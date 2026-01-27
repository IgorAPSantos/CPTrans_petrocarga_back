package com.cptrans.petrocarga;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PetrocargaApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(PetrocargaApplication.class, args);
    }
}
