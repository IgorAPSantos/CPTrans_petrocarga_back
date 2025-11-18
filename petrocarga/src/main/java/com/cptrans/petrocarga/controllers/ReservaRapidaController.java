package com.cptrans.petrocarga.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cptrans.petrocarga.dto.ReservaRapidaRequestDTO;
import com.cptrans.petrocarga.dto.ReservaRapidaResponseDTO;
import com.cptrans.petrocarga.models.ReservaRapida;
import com.cptrans.petrocarga.models.Vaga;
import com.cptrans.petrocarga.services.ReservaRapidaService;
import com.cptrans.petrocarga.services.VagaService;


@RestController
@RequestMapping("/reserva-rapida")
public class ReservaRapidaController {
    
    @Autowired
    private ReservaRapidaService reservaRapidaService;
    @Autowired
    private VagaService vagaService;

    @PreAuthorize("hasAnyRole('ADMIN', 'AGENTE')")
    @PostMapping()
    public ResponseEntity<ReservaRapidaResponseDTO> createReservaRapida(@RequestBody ReservaRapidaRequestDTO reservaRapidaRequest) {
        Vaga vaga = vagaService.findById(reservaRapidaRequest.getVagaId());
        ReservaRapida novaReservaRapida = reservaRapidaService.create(reservaRapidaRequest.toEntity(vaga));
        
        return ResponseEntity.status(HttpStatus.CREATED).body(novaReservaRapida.toResponse());
    }
    
}
