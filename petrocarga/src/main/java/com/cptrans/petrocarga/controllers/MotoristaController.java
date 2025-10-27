package com.cptrans.petrocarga.controllers;

import com.cptrans.petrocarga.dto.MotoristaRequestDTO;
import com.cptrans.petrocarga.dto.MotoristaResponseDTO;
import com.cptrans.petrocarga.models.Empresa;
import com.cptrans.petrocarga.models.Motorista;
import com.cptrans.petrocarga.models.Usuario;
import com.cptrans.petrocarga.services.EmpresaService;
import com.cptrans.petrocarga.services.MotoristaService;
import com.cptrans.petrocarga.services.UsuarioService;
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
@RequestMapping("/motoristas")
public class MotoristaController {

    @Autowired
    private MotoristaService motoristaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EmpresaService empresaService;

    @GetMapping
    public ResponseEntity<List<MotoristaResponseDTO>> getAllMotoristas() {
        List<MotoristaResponseDTO> motoristas = motoristaService.findAll().stream()
                .map(MotoristaResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(motoristas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MotoristaResponseDTO> getMotoristaById(@PathVariable UUID id) {
        return motoristaService.findById(id)
                .map(MotoristaResponseDTO::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MotoristaResponseDTO> createMotorista(@RequestBody @Valid MotoristaRequestDTO motoristaRequestDTO) {
        Optional<Usuario> usuarioOpt = usuarioService.findById(motoristaRequestDTO.getUsuarioId());
        Optional<Empresa> empresaOpt = Optional.empty();
        if (motoristaRequestDTO.getEmpresaId() != null) {
            empresaOpt = empresaService.findById(motoristaRequestDTO.getEmpresaId());
        }

        if (usuarioOpt.isPresent() && (motoristaRequestDTO.getEmpresaId() == null || empresaOpt.isPresent())) {
            Motorista motorista = motoristaRequestDTO.toEntity(usuarioOpt.get(), empresaOpt.orElse(null));
            Motorista savedMotorista = motoristaService.save(motorista);
            return ResponseEntity.status(HttpStatus.CREATED).body(new MotoristaResponseDTO(savedMotorista));
        }
        return ResponseEntity.badRequest().build(); // Or a more specific error
    }

    @PutMapping("/{id}")
    public ResponseEntity<MotoristaResponseDTO> updateMotorista(@PathVariable UUID id, @RequestBody @Valid MotoristaRequestDTO motoristaRequestDTO) {
        
        // 1. Verificar se a entidade a ser atualizada (Motorista) existe
        Optional<Motorista> existingOpt = motoristaService.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }

        // 2. Verificar as dependências (Usuario e Empresa)
        Optional<Usuario> usuarioOpt = usuarioService.findById(motoristaRequestDTO.getUsuarioId());
        Optional<Empresa> empresaOpt = Optional.empty();
        if (motoristaRequestDTO.getEmpresaId() != null) {
            empresaOpt = empresaService.findById(motoristaRequestDTO.getEmpresaId());
        }

        // 3. Validar se as dependências foram encontradas
        // (O usuário deve existir. A empresa é opcional, mas se o ID foi passado, ela deve existir)
        if (usuarioOpt.isEmpty() || (motoristaRequestDTO.getEmpresaId() != null && empresaOpt.isEmpty())) {
            return ResponseEntity.badRequest().build(); // 400 Bad Request (dependências ausentes)
        }

        // 4. Todas as verificações passaram, realizar a atualização
        Motorista existingMotorista = existingOpt.get();
        existingMotorista.setUsuario(usuarioOpt.get());
        existingMotorista.setTipoCNH(motoristaRequestDTO.getTipoCNH());
        existingMotorista.setNumeroCNH(motoristaRequestDTO.getNumeroCNH());
        existingMotorista.setDataValidadeCNH(motoristaRequestDTO.getDataValidadeCNH());
        existingMotorista.setEmpresa(empresaOpt.orElse(null)); // Define null se nenhuma empresa foi passada

        Motorista updatedMotorista = motoristaService.save(existingMotorista);
        
        // 200 OK
        return ResponseEntity.ok(new MotoristaResponseDTO(updatedMotorista));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMotorista(@PathVariable UUID id) {
        if (motoristaService.findById(id).isPresent()) {
            motoristaService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
