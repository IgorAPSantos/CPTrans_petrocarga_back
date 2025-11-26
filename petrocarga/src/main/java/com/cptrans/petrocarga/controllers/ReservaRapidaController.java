package com.cptrans.petrocarga.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cptrans.petrocarga.dto.ReservaRapidaRequestDTO;
import com.cptrans.petrocarga.dto.ReservaRapidaResponseDTO;
import com.cptrans.petrocarga.models.Agente;
import com.cptrans.petrocarga.models.ReservaRapida;
import com.cptrans.petrocarga.models.Vaga;
import com.cptrans.petrocarga.services.AgenteService;
import com.cptrans.petrocarga.services.ReservaRapidaService;
import com.cptrans.petrocarga.services.VagaService;




@RestController
@RequestMapping("/reserva-rapida")
public class ReservaRapidaController {
    
    @Autowired
    private ReservaRapidaService reservaRapidaService;
    @Autowired
    private VagaService vagaService;
    @Autowired
    private AgenteService agenteService;

    @PreAuthorize("hasAnyRole('ADMIN', 'AGENTE')")
    @PostMapping()
    public ResponseEntity<ReservaRapidaResponseDTO> createReservaRapida(@RequestBody ReservaRapidaRequestDTO reservaRapidaRequest) {
        Vaga vaga = vagaService.findById(reservaRapidaRequest.getVagaId());
        ReservaRapida novaReservaRapida = reservaRapidaService.create(reservaRapidaRequest.toEntity(vaga));
        
        return ResponseEntity.status(HttpStatus.CREATED).body(novaReservaRapida.toResponse());
    }
    
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR') or #agenteId == authentication.principal.id")
    @GetMapping("/{agenteId}")
    public ResponseEntity<List<ReservaRapidaResponseDTO>> getReservasRapidas(@PathVariable UUID agenteId) {
        Agente agente = agenteService.findById(agenteId);
        List<ReservaRapidaResponseDTO> reservasRapidas = reservaRapidaService.findByAgente(agente).stream()
                .map(ReservaRapida::toResponse)
                .toList();
        return ResponseEntity.ok(reservasRapidas);

    }
    
    // @PreAuthorize("hasAnyRole('ADMIN','GESTOR', 'AGENTE')")
    // @GetMapping()
    // public ResponseEntity<List<ReservaRapidaResponseDTO>> getReservasRapidas(@RequestParam (required = false) UUID vagaId, @RequestParam (required = false) String placaVeiculo) {
    //     return new String();
    // }
    
}
