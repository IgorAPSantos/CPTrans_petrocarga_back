package com.cptrans.petrocarga.controllers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cptrans.petrocarga.dto.ReservaRequestDTO;
import com.cptrans.petrocarga.dto.ReservaResponseDTO;
import com.cptrans.petrocarga.enums.StatusReservaEnum;
import com.cptrans.petrocarga.models.Reserva;
import com.cptrans.petrocarga.services.ReservaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR', 'AGENTE')")
    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> getAllReservas(@RequestParam(required = false) StatusReservaEnum status) {
        List<ReservaResponseDTO> reservas = reservaService.findAll(status).stream()
                .map(ReservaResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reservas);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR', 'AGENTE', 'MOTORISTA', 'EMPRESA')")
    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> getReservaById(@PathVariable UUID id) {
        Reserva reserva = reservaService.findById(id);
        return ResponseEntity.ok(reserva.toResponseDTO());
    }

    @PreAuthorize("#usuarioId == authentication.principal.id or hasAnyRole('ADMIN', 'GESTOR')")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ReservaResponseDTO>> getReservasByUsuarioId(@PathVariable UUID usuarioId, @RequestParam(required = false) StatusReservaEnum status) {
        List<ReservaResponseDTO> reservas = reservaService.findByUsuarioId(usuarioId, status).stream()
                .map(ReservaResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reservas);
    }
    
    @PreAuthorize("hasAnyRole('ADMIN','MOTORISTA', 'EMPRESA')")
    @PostMapping()
    public ResponseEntity<ReservaResponseDTO> createReserva(@RequestBody @Valid ReservaRequestDTO reservaRequestDTO) {
        Reserva novaReserva = reservaService.createReserva( reservaRequestDTO.getVagaId(),
                                                            reservaRequestDTO.getMotoristaId(),
                                                            reservaRequestDTO.getVeiculoId(),
                                                            reservaRequestDTO.toEntity());
                                                                
        return ResponseEntity.status(HttpStatus.CREATED).body(novaReserva.toResponseDTO());

    }

    // TODO: Refatorar Rota PUT
    // @PutMapping("/{id}")
    // public ResponseEntity<ReservaResponseDTO> updateReserva(@PathVariable UUID id, @RequestBody @Valid ReservaRequestDTO reservaRequestDTO) {
        
    //     // 1. Verificar se a entidade a ser atualizada (Reserva) existe
    //     Optional<Reserva> existingOpt = reservaService.findById(id);
    //     if (existingOpt.isEmpty()) {
    //         return ResponseEntity.notFound().build(); // 404 Not Found
    //     }

    //     // 2. Verificar todas as dependências
    //     Optional<Vaga> vagaOpt = vagaService.findById(reservaRequestDTO.getVagaId());
    //     Optional<Motorista> motoristaOpt = motoristaService.findById(reservaRequestDTO.getMotoristaId());
    //     Optional<Veiculo> veiculoOpt = veiculoService.findById(reservaRequestDTO.getVeiculoId());
    //     Optional<Usuario> criadoPorOpt = usuarioService.findById(reservaRequestDTO.getCriadoPorId());

    //     // 3. Validar se TODAS as dependências foram encontradas
    //     if (vagaOpt.isEmpty() || motoristaOpt.isEmpty() || veiculoOpt.isEmpty() || criadoPorOpt.isEmpty()) {
    //         return ResponseEntity.badRequest().build(); // 400 Bad Request (dependências ausentes)
    //     }

    //     // 4. Todas as verificações passaram, realizar a atualização
    //     Reserva existingReserva = existingOpt.get();
    //     existingReserva.setVaga(vagaOpt.get());
    //     existingReserva.setMotorista(motoristaOpt.get());
    //     existingReserva.setVeiculo(veiculoOpt.get());
    //     existingReserva.setCriadoPor(criadoPorOpt.get());
    //     existingReserva.setCidadeOrigem(reservaRequestDTO.getCidadeOrigem());
    //     existingReserva.setInicio(reservaRequestDTO.getInicio());
    //     existingReserva.setFim(reservaRequestDTO.getFim());
    //     existingReserva.setStatus(reservaRequestDTO.getStatus());
        
    //     Reserva updatedReserva = reservaService.save(existingReserva);
        
    //     // 200 OK
    //     return ResponseEntity.ok(new ReservaResponseDTO(updatedReserva));
    // }

    // TODO: Refatorar Rota DELETE
    // @DeleteMapping("/{id}")
    // public ResponseEntity<Void> deleteReserva(@PathVariable UUID id) {
    //     if (reservaService.findById(id).isPresent()) {
    //         reservaService.deleteById(id);
    //         return ResponseEntity.noContent().build();
    //     }
    //     return ResponseEntity.notFound().build();
    // }
}
