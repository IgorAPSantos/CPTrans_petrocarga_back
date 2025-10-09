package com.cptrans.petrocarga.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cptrans.petrocarga.dto.VagaRequestDTO;
import com.cptrans.petrocarga.dto.VagaResponseDTO;
import com.cptrans.petrocarga.models.Vaga;
import com.cptrans.petrocarga.services.VagaService;

import io.swagger.v3.oas.annotations.Operation;



@RestController
@CrossOrigin(origins = "http://localhost:3000/")
@RequestMapping(value = "/vagas")
public class VagaController {
    
    @Autowired
    private VagaService vagaService;

    @GetMapping()
    @Operation(summary = "Listar todas as vagas")
    public ResponseEntity<?> listarVagas() {
        return ResponseEntity.ok(vagaService.listarVagas().stream().map(vaga -> vaga.toResponseDTO()));
    }

    @PostMapping()
    @Operation(summary = "Cadastrar uma nova vaga")
    public ResponseEntity<VagaResponseDTO> cadastrarVaga(@RequestBody VagaRequestDTO vagaRequest) {
        Vaga vaga = vagaService.cadastrarVaga(vagaRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(vaga.toResponseDTO()); 
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar uma vaga pelo ID")
    public ResponseEntity<?> deletarVaga(@PathVariable UUID id) {
        vagaService.deletarVaga(id);
        return ResponseEntity.noContent().build(); 
      
    }
    
    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar atributos de uma vaga pelo ID")
    public ResponseEntity<VagaResponseDTO> atualizarParcialmenteVaga(@PathVariable UUID id, @RequestBody VagaRequestDTO vagaRequest) {
        Vaga vagaAtualizada = vagaService.atualizarParcialmenteVaga(id, vagaRequest);
        return ResponseEntity.ok(vagaAtualizada.toResponseDTO());
    }
}
