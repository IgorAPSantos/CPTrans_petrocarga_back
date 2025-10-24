package com.cptrans.petrocarga.controllers;

import com.cptrans.petrocarga.dto.ReservaRequestDTO;
import com.cptrans.petrocarga.dto.ReservaResponseDTO;
import com.cptrans.petrocarga.models.Motorista;
import com.cptrans.petrocarga.models.Reserva;
import com.cptrans.petrocarga.models.Usuario;
import com.cptrans.petrocarga.models.Vaga;
import com.cptrans.petrocarga.models.Veiculo;
import com.cptrans.petrocarga.services.MotoristaService;
import com.cptrans.petrocarga.services.ReservaService;
import com.cptrans.petrocarga.services.UsuarioService;
import com.cptrans.petrocarga.services.VagaService;
import com.cptrans.petrocarga.services.VeiculoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> getAllReservas() {
        List<ReservaResponseDTO> reservas = reservaService.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> getReservaById(@PathVariable UUID id) {
        return reservaService.findById(id)
                .map(this::convertToResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> createReserva(@RequestBody @Valid ReservaRequestDTO reservaRequestDTO) {
        Optional<Vaga> vagaOpt = vagaService.findById(reservaRequestDTO.getVagaId());
        Optional<Motorista> motoristaOpt = motoristaService.findById(reservaRequestDTO.getMotoristaId());
        Optional<Veiculo> veiculoOpt = veiculoService.findById(reservaRequestDTO.getVeiculoId());
        Optional<Usuario> criadoPorOpt = usuarioService.findById(reservaRequestDTO.getCriadoPorId());

        if (vagaOpt.isPresent() && motoristaOpt.isPresent() && veiculoOpt.isPresent() && criadoPorOpt.isPresent()) {
            Reserva reserva = convertToEntity(reservaRequestDTO, vagaOpt.get(), motoristaOpt.get(), veiculoOpt.get(), criadoPorOpt.get());
            Reserva savedReserva = reservaService.save(reserva);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponseDTO(savedReserva));
        }
        return ResponseEntity.badRequest().build(); // Or a more specific error
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> updateReserva(@PathVariable UUID id, @RequestBody @Valid ReservaRequestDTO reservaRequestDTO) {
        return reservaService.findById(id)
                .map(existingReserva -> {
                    Optional<Vaga> vagaOpt = vagaService.findById(reservaRequestDTO.getVagaId());
                    Optional<Motorista> motoristaOpt = motoristaService.findById(reservaRequestDTO.getMotoristaId());
                    Optional<Veiculo> veiculoOpt = veiculoService.findById(reservaRequestDTO.getVeiculoId());
                    Optional<Usuario> criadoPorOpt = usuarioService.findById(reservaRequestDTO.getCriadoPorId());

                    if (vagaOpt.isPresent() && motoristaOpt.isPresent() && veiculoOpt.isPresent() && criadoPorOpt.isPresent()) {
                        updateEntityFromDto(existingReserva, reservaRequestDTO, vagaOpt.get(), motoristaOpt.get(), veiculoOpt.get(), criadoPorOpt.get());
                        Reserva updatedReserva = reservaService.save(existingReserva);
                        return ResponseEntity.ok(convertToResponseDTO(updatedReserva));
                    }
                    return ResponseEntity.badRequest().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReserva(@PathVariable UUID id) {
        if (reservaService.findById(id).isPresent()) {
            reservaService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private ReservaResponseDTO convertToResponseDTO(Reserva reserva) {
        ReservaResponseDTO dto = new ReservaResponseDTO();
        dto.setId(reserva.getId());
        dto.setVagaId(reserva.getVaga().getId());
        dto.setMotoristaId(reserva.getMotorista().getId());
        dto.setVeiculoId(reserva.getVeiculo().getId());
        dto.setCriadoPorId(reserva.getCriadoPor().getId());
        dto.setCidadeOrigem(reserva.getCidadeOrigem());
        dto.setCriadoEm(reserva.getCriadoEm());
        dto.setInicio(reserva.getInicio());
        dto.setFim(reserva.getFim());
        dto.setStatus(reserva.getStatus());
        return dto;
    }

    private Reserva convertToEntity(ReservaRequestDTO dto, Vaga vaga, Motorista motorista, Veiculo veiculo, Usuario criadoPor) {
        Reserva reserva = new Reserva();
        reserva.setVaga(vaga);
        reserva.setMotorista(motorista);
        reserva.setVeiculo(veiculo);
        reserva.setCriadoPor(criadoPor);
        reserva.setCidadeOrigem(dto.getCidadeOrigem());
        reserva.setInicio(dto.getInicio());
        reserva.setFim(dto.getFim());
        reserva.setStatus(dto.getStatus());
        return reserva;
    }

    private void updateEntityFromDto(Reserva reserva, ReservaRequestDTO dto, Vaga vaga, Motorista motorista, Veiculo veiculo, Usuario criadoPor) {
        reserva.setVaga(vaga);
        reserva.setMotorista(motorista);
        reserva.setVeiculo(veiculo);
        reserva.setCriadoPor(criadoPor);
        reserva.setCidadeOrigem(dto.getCidadeOrigem());
        reserva.setInicio(dto.getInicio());
        reserva.setFim(dto.getFim());
        reserva.setStatus(dto.getStatus());
    }
}