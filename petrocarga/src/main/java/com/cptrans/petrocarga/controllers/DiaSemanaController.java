package com.cptrans.petrocarga.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cptrans.petrocarga.services.DiaSemanaService;


@RestController
@RequestMapping(value = "/diasSemana")
public class DiaSemanaController {

    @Autowired
    private DiaSemanaService diaSemanaService;
    
    @PostMapping()
    public ResponseEntity<?> createDiasSemana() {
        try {
            diaSemanaService.createDiaSemana();
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            System.out.println("Erro ao criar dias da semana: " + e.getMessage());
            if(e.getMessage().contains("duplicar valor da chave")){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Dias da semana já foram criados");
            }
            return ResponseEntity.internalServerError().body("Erro ao criar dias da semana");
        }
    }
    
}
