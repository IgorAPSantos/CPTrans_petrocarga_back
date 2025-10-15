package com.cptrans.petrocarga.controllers;

import java.util.List;
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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/vagas")
@CrossOrigin(origins = {"http://localhost:3000"})
public class VagaController {
    
    @Autowired
    private VagaService vagaService;

    @GetMapping()
    @Operation(
        summary = "Listar todas as vagas",
        description = "Retorna uma lista de todas as vagas cadastradas no sistema.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Lista de vagas retornada com sucesso",
                        content = @Content(mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = VagaResponseDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
        }
    )
    public ResponseEntity<List<VagaResponseDTO>> listarVagas() {
        return ResponseEntity.ok(vagaService.listarVagas().stream().map(vaga -> vaga.toResponseDTO()).toList());
    }
    
    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar uma vaga pelo ID",
        description = "Retorna os detalhes de uma vaga específica identificada pelo seu UUID.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Vaga encontrada com sucesso",
                        content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = VagaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Vaga não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
        }
    )
    public ResponseEntity<VagaResponseDTO> listarVagas(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(vagaService.buscarVagaPorId(id).toResponseDTO());
    }

    @PostMapping()
    @Operation(
        summary = "Cadastrar uma nova vaga",
        description = "Cria uma nova vaga com base nos dados fornecidos no corpo da requisição.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados necessários para criação de uma vaga",
            required = true,
            content = @Content(schema = @Schema(implementation = VagaRequestDTO.class))
        ),
        responses = {
            @ApiResponse(responseCode = "201", description = "Vaga criada com sucesso",
                        content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = VagaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos enviados"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
        }
    )

    public ResponseEntity<VagaResponseDTO> cadastrarVaga(@Valid @RequestBody VagaRequestDTO vagaRequest) {
        Vaga vaga = vagaService.cadastrarVaga(vagaRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(vaga.toResponseDTO()); 
    }
    
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Deletar uma vaga pelo ID",
        description = "Remove uma vaga específica identificada pelo seu UUID.",
        parameters = {
            @Parameter(name = "id", description = "Identificador único da vaga", required = true,
                    example = "2cb9a7f0-4499-4531-9f67-3e1b6eaf1234")
        },
        responses = {
            @ApiResponse(responseCode = "204", description = "Vaga deletada com sucesso (sem conteúdo)"),
            @ApiResponse(responseCode = "404", description = "Vaga não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
        }
    )
    public ResponseEntity<?> deletarVaga(@Valid @PathVariable UUID id) {
        vagaService.deletarVaga(id);
        return ResponseEntity.noContent().build(); 
    }
    
    @PatchMapping("/{id}")
    @Operation(
        summary = "Atualizar parcialmente uma vaga",
        description = "Atualiza apenas os campos enviados no corpo da requisição para a vaga especificada pelo ID.",
        parameters = {
            @Parameter(name = "id", description = "Identificador único da vaga", required = true,
                    example = "2cb9a7f0-4499-4531-9f67-3e1b6eaf1234")
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Campos a serem atualizados na vaga",
            required = true,
            content = @Content(schema = @Schema(implementation = VagaRequestDTO.class))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Vaga atualizada com sucesso",
                        content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = VagaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos enviados"),
            @ApiResponse(responseCode = "404", description = "Vaga não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
        }
    )
    public ResponseEntity<VagaResponseDTO> atualizarParcialmenteVaga(@Valid @PathVariable UUID id,@Valid @RequestBody VagaRequestDTO vagaRequest) {
        Vaga vagaAtualizada = vagaService.atualizarParcialmenteVaga(id, vagaRequest);
        return ResponseEntity.ok(vagaAtualizada.toResponseDTO());
    }
}
