package com.cptrans.petrocarga.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cptrans.petrocarga.dto.ReservaDTO;
import com.cptrans.petrocarga.dto.ReservaDetailedResponseDTO;
import com.cptrans.petrocarga.dto.ReservaPATCHRequestDTO;
import com.cptrans.petrocarga.dto.ReservaRequestDTO;
import com.cptrans.petrocarga.dto.ReservaResponseDTO;
import com.cptrans.petrocarga.enums.StatusReservaEnum;
import com.cptrans.petrocarga.enums.TipoVeiculoEnum;
import com.cptrans.petrocarga.models.Motorista;
import com.cptrans.petrocarga.models.Reserva;
import com.cptrans.petrocarga.models.Vaga;
import com.cptrans.petrocarga.models.Veiculo;
import com.cptrans.petrocarga.services.MotoristaService;
import com.cptrans.petrocarga.services.ReservaService;
import com.cptrans.petrocarga.services.VagaService;
import com.cptrans.petrocarga.services.VeiculoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;
    @Autowired
    private VagaService vagaService;
    @Autowired
    private MotoristaService motoristaService;
    @Autowired
    private VeiculoService veiculoService;

    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR', 'AGENTE')")
    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> getReservas(@RequestParam(required = false) List<StatusReservaEnum> status, @RequestParam(required = false) UUID vagaId) {
        List<ReservaResponseDTO> reservas = reservaService.findAll(status, vagaId).stream()
                .map(ReservaResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reservas);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR', 'AGENTE')")
    @GetMapping("/all/{vagaId}")
    public ResponseEntity<List<ReservaDTO>> getAllReservasWithFiltersByVaga(@PathVariable UUID vagaId,@RequestParam(required = false) LocalDate data, @RequestParam(required = false) String placa,@RequestParam(required = false) List<StatusReservaEnum> status) {
        Vaga vaga = vagaService.findById(vagaId);
        if(placa != null) {
            List<ReservaDTO> reservas = reservaService.getReservasByVagaDataAndPlaca(vaga, data, placa, status);
            return ResponseEntity.ok(reservas);
        }
        List<ReservaDTO> reservas = reservaService.getReservasByVagaAndData(vaga, data, status);
        return ResponseEntity.ok(reservas);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR', 'AGENTE')")
    @GetMapping("/all")
    public ResponseEntity<List<ReservaDTO>> getAllReservasWithFilters(@RequestParam(required = false) LocalDate data, @RequestParam(required = false) String placa,@RequestParam(required = false) List<StatusReservaEnum> status) {
        if(placa != null) {
            List<ReservaDTO> reservas = reservaService.getAllReservasByDataAndPlaca( data, placa, status);
            return ResponseEntity.ok(reservas);
        }
        List<ReservaDTO> reservas = reservaService.getAllReservasByData( data, status);
        return ResponseEntity.ok(reservas);
    }
    
    @PreAuthorize("hasAnyRole('ADMIN','AGENTE')")
    @GetMapping("/placa")
    public ResponseEntity<List<ReservaDTO>> getAllReservasByPlaca(@RequestParam(required = true) String placa) {
        List<ReservaDTO> reservas = reservaService.getReservasAtivasByPlaca(placa);
        return ResponseEntity.ok(reservas);
    }

    @PreAuthorize("hasAnyRole('ADMIN','AGENTE','MOTORISTA', 'EMPRESA')")
    @GetMapping("/bloqueios/{vagaId}")
    public ResponseEntity<List<ReservaService.Intervalo>> getIntervalosBloqueados(@PathVariable UUID vagaId, @RequestParam LocalDate data, @RequestParam TipoVeiculoEnum tipoVeiculo) {
        Vaga vaga = vagaService.findById(vagaId);
        List<ReservaService.Intervalo> intervalosBloqueados = reservaService.getIntervalosBloqueados(vaga, data, tipoVeiculo);
        return ResponseEntity.ok(intervalosBloqueados);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR', 'AGENTE', 'MOTORISTA', 'EMPRESA')")
    @GetMapping("/{id}")
    public ResponseEntity<ReservaDetailedResponseDTO> getReservaById(@PathVariable UUID id) {
        // Busca a reserva e mantém as verificações de permissão no service
        Reserva reserva = reservaService.findById(id);
        // Converte para DTO detalhado que expõe nomes/placa para exibição amigável
        ReservaDetailedResponseDTO dto = new ReservaDetailedResponseDTO(reserva);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("#usuarioId == authentication.principal.id or hasAnyRole('ADMIN', 'GESTOR')")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ReservaResponseDTO>> getReservasByUsuarioId(@PathVariable UUID usuarioId, @RequestParam(required = false) List<StatusReservaEnum> status) {
        List<ReservaResponseDTO> reservas = reservaService.findByUsuarioId(usuarioId, status).stream()
                .map(ReservaResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reservas);
    }
    
    @PreAuthorize("hasAnyRole('ADMIN','MOTORISTA', 'EMPRESA')")
    @PostMapping()
    public ResponseEntity<ReservaResponseDTO> createReserva(@RequestBody @Valid ReservaRequestDTO reservaRequestDTO) {
        Vaga vaga = vagaService.findById(reservaRequestDTO.getVagaId());
        Motorista motorista = motoristaService.findById(reservaRequestDTO.getMotoristaId());
        Veiculo veiculo = veiculoService.findById(reservaRequestDTO.getVeiculoId());
        Reserva novaReserva = reservaService.createReserva(reservaRequestDTO.toEntity(vaga, motorista, veiculo));
                                                                
        return ResponseEntity.status(HttpStatus.CREATED).body(novaReserva.toResponseDTO());

    }

    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','AGENTE')")
    @PostMapping("/{id}/finalizar-forcado")
    public ResponseEntity<ReservaResponseDTO> finalizarReservaForcado(@PathVariable UUID id) {
        Reserva reservaFinalizada = reservaService.finalizarForcado(id);
        return ResponseEntity.ok(reservaFinalizada.toResponseDTO());
    }

    @PreAuthorize("hasAnyRole('ADMIN','AGENTE','MOTORISTA','EMPRESA')")
    @PostMapping("/{id}/checkin")
    public ResponseEntity<ReservaResponseDTO> realizarCheckIn(@PathVariable UUID id) {
        Reserva reserva = reservaService.realizarCheckIn(id);
        return ResponseEntity.ok(reserva.toResponseDTO());
    }

    @PreAuthorize("#usuarioId == authentication.principal.id or hasAnyRole('ADMIN', 'GESTOR')")
    @PatchMapping("/{id}/{usuarioId}")
    public ResponseEntity<ReservaResponseDTO> updateReserva(@PathVariable UUID id, @PathVariable UUID usuarioId, @RequestBody @Valid ReservaPATCHRequestDTO reservaRequestDTO) {
        Reserva reserva = reservaService.findById(id);
        Reserva reservaAtualizada = reservaService.atualizarReserva(reserva, usuarioId, reservaRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaAtualizada.toResponseDTO());
    }

    @PreAuthorize("#usuarioId == authentication.principal.id or hasAnyRole('ADMIN', 'GESTOR')")
    @DeleteMapping("/{id}/{usuarioId}")
    public ResponseEntity<Void> cancelarReserva(@PathVariable UUID id, @PathVariable UUID usuarioId) {
        
        reservaService.cancelarReserva(id, usuarioId);
        return ResponseEntity.noContent().build();
        
    }
}