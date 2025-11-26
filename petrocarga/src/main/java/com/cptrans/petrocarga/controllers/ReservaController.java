package com.cptrans.petrocarga.controllers;

import java.time.LocalDate;
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

import com.cptrans.petrocarga.dto.ReservaDTO;
import com.cptrans.petrocarga.dto.ReservaDetailedResponseDTO;
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
    public ResponseEntity<List<ReservaResponseDTO>> getReservas(@RequestParam(required = false) StatusReservaEnum status, @RequestParam(required = false) UUID vagaId) {
        List<ReservaResponseDTO> reservas = reservaService.findAll(status, vagaId).stream()
                .map(ReservaResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reservas);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR', 'AGENTE')")
    @GetMapping("/all/{vagaId}")
    public ResponseEntity<List<ReservaDTO>> getAllReservasWithFilters(@PathVariable UUID vagaId,@RequestParam(required = false) LocalDate data, @RequestParam(required = false) String placa,@RequestParam(required = false) StatusReservaEnum status) {
        Vaga vaga = vagaService.findById(vagaId);
        if(placa != null) {
            List<ReservaDTO> reservas = reservaService.getReservasAtivasByDataAndPlaca(vaga, data, placa, status);
            return ResponseEntity.ok(reservas);
        }
        List<ReservaDTO> reservas = reservaService.getReservasByData(vaga, data, status);
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
    public ResponseEntity<List<ReservaResponseDTO>> getReservasByUsuarioId(@PathVariable UUID usuarioId, @RequestParam(required = false) StatusReservaEnum status) {
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
