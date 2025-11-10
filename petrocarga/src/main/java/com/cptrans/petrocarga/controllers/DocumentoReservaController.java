package com.cptrans.petrocarga.controllers;

import com.cptrans.petrocarga.dto.ReservaDetailedResponseDTO;
import com.cptrans.petrocarga.models.Reserva;
import com.cptrans.petrocarga.services.DocumentoReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/documentos/reservas")
public class DocumentoReservaController {

    @Autowired
    private DocumentoReservaService documentoReservaService;

    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR', 'AGENTE', 'MOTORISTA', 'EMPRESA')")
    @GetMapping("/{id}")
    public ResponseEntity<ReservaDetailedResponseDTO> getDocumentoReservaById(@PathVariable UUID id) {
        Reserva reserva = documentoReservaService.findReservaWithDetails(id);
        ReservaDetailedResponseDTO dto = new ReservaDetailedResponseDTO(reserva);
        return ResponseEntity.ok(dto);
    }
}