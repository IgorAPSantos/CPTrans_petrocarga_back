package com.cptrans.petrocarga.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cptrans.petrocarga.dto.ReservaResponseDTO;
import com.cptrans.petrocarga.services.ReservaSearchService;

@RestController
@RequestMapping("/reservas")
public class ReservaSearchController {

    @Autowired
    private ReservaSearchService reservaSearchService;

    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR', 'AGENTE', 'MOTORISTA', 'EMPRESA')")
    @GetMapping("/search")
    public ResponseEntity<List<ReservaResponseDTO>> searchReservas(@RequestParam("q") String q) {
        List<ReservaResponseDTO> results = reservaSearchService.searchByQuery(q);
        return ResponseEntity.ok(results);
    }
}
