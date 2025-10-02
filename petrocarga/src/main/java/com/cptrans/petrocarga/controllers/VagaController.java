package com.cptrans.petrocarga.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cptrans.petrocarga.dto.VagaRequestDTO;
import com.cptrans.petrocarga.models.Vaga;
import com.cptrans.petrocarga.services.VagaService;



@RestController
@RequestMapping(value = "/vagas")
public class VagaController {
    
    @Autowired
    private VagaService vagaService;

    @GetMapping()
    public ResponseEntity<?> listarVagas() {
        try {
            return ResponseEntity.ok(vagaService.listarVagas().stream().map(vaga -> vaga.toResponseDTO()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("erro: Erro interno de servidor.");
        }
    }

    @PostMapping()
    public ResponseEntity<?> cadastrarVaga(@RequestBody VagaRequestDTO vagaRequest) {
        try {
            Vaga vaga = vagaService.cadastrarVaga(vagaRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(vaga.toResponseDTO()); 
        }catch(IllegalArgumentException e ){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("erro: " + e.getMessage());
        }
        catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("erro: Erro interno de servidor.");
        }
    }
    
    
}
