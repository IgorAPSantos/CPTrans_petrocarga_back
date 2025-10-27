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
                .map(ReservaResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> getReservaById(@PathVariable UUID id) {
        return reservaService.findById(id)
                .map(ReservaResponseDTO::new)
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
            Reserva reserva = reservaRequestDTO.toEntity(vagaOpt.get(), motoristaOpt.get(), veiculoOpt.get(), criadoPorOpt.get());
            Reserva savedReserva = reservaService.save(reserva);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ReservaResponseDTO(savedReserva));
        }
        return ResponseEntity.badRequest().build(); // Or a more specific error
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> updateReserva(@PathVariable UUID id, @RequestBody @Valid ReservaRequestDTO reservaRequestDTO) {
        
        // 1. Verificar se a entidade a ser atualizada (Reserva) existe
        Optional<Reserva> existingOpt = reservaService.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }

        // 2. Verificar todas as dependências
        Optional<Vaga> vagaOpt = vagaService.findById(reservaRequestDTO.getVagaId());
        Optional<Motorista> motoristaOpt = motoristaService.findById(reservaRequestDTO.getMotoristaId());
        Optional<Veiculo> veiculoOpt = veiculoService.findById(reservaRequestDTO.getVeiculoId());
        Optional<Usuario> criadoPorOpt = usuarioService.findById(reservaRequestDTO.getCriadoPorId());

        // 3. Validar se TODAS as dependências foram encontradas
        if (vagaOpt.isEmpty() || motoristaOpt.isEmpty() || veiculoOpt.isEmpty() || criadoPorOpt.isEmpty()) {
            return ResponseEntity.badRequest().build(); // 400 Bad Request (dependências ausentes)
        }

        // 4. Todas as verificações passaram, realizar a atualização
        Reserva existingReserva = existingOpt.get();
        existingReserva.setVaga(vagaOpt.get());
        existingReserva.setMotorista(motoristaOpt.get());
        existingReserva.setVeiculo(veiculoOpt.get());
        existingReserva.setCriadoPor(criadoPorOpt.get());
        existingReserva.setCidadeOrigem(reservaRequestDTO.getCidadeOrigem());
        existingReserva.setInicio(reservaRequestDTO.getInicio());
        existingReserva.setFim(reservaRequestDTO.getFim());
        existingReserva.setStatus(reservaRequestDTO.getStatus());
        
        Reserva updatedReserva = reservaService.save(existingReserva);
        
        // 200 OK
        return ResponseEntity.ok(new ReservaResponseDTO(updatedReserva));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReserva(@PathVariable UUID id) {
        if (reservaService.findById(id).isPresent()) {
            reservaService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
