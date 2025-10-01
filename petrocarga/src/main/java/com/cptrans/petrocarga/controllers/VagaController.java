package com.cptrans.petrocarga.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cptrans.petrocarga.services.VagaService;


@RestController
@RequestMapping(value = "/vagas")
public class VagaController {
    
    @Autowired
    private VagaService vagaService;

    @GetMapping()
    public ResponseEntity<String> getVagas() {
        try {
            return ResponseEntity.ok(vagaService.getVagas().toString());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro: " + e.getMessage());
        }
    }
    
}
