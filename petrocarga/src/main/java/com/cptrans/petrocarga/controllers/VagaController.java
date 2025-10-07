package com.cptrans.petrocarga.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cptrans.petrocarga.dto.VagaPatchDTO;
import com.cptrans.petrocarga.dto.VagaRequestDTO;
import com.cptrans.petrocarga.models.Vaga;
import com.cptrans.petrocarga.services.VagaService;

import jakarta.persistence.EntityNotFoundException;



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
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarVaga(@PathVariable UUID id) {
        try {
            vagaService.deletarVaga(id);
            return ResponseEntity.noContent().build(); 
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao deletar vaga.");
        }
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<?> atualizarParcialmenteVaga(@PathVariable UUID id, @RequestBody VagaPatchDTO vagaRequest) {
        try {
            Vaga vagaAtualizada = vagaService.atualizarParcialmenteVaga(id, vagaRequest);
            return ResponseEntity.ok(vagaAtualizada.toResponseDTO());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao atualizar vaga.");
        }
    }
}
