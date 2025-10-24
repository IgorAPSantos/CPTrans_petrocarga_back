package com.cptrans.petrocarga.controllers;

import com.cptrans.petrocarga.dto.VeiculoRequestDTO;
import com.cptrans.petrocarga.dto.VeiculoResponseDTO;
import com.cptrans.petrocarga.models.Usuario;
import com.cptrans.petrocarga.models.Veiculo;
import com.cptrans.petrocarga.services.UsuarioService;
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
@RequestMapping("/veiculos")
public class VeiculoController {

    @Autowired
    private VeiculoService veiculoService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<VeiculoResponseDTO>> getAllVeiculos() {
        List<VeiculoResponseDTO> veiculos = veiculoService.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(veiculos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VeiculoResponseDTO> getVeiculoById(@PathVariable UUID id) {
        return veiculoService.findById(id)
                .map(this::convertToResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<VeiculoResponseDTO> createVeiculo(@RequestBody @Valid VeiculoRequestDTO veiculoRequestDTO) {
        return usuarioService.findById(veiculoRequestDTO.getUsuarioId())
                .map(usuario -> {
                    Veiculo veiculo = convertToEntity(veiculoRequestDTO, usuario);
                    Veiculo savedVeiculo = veiculoService.save(veiculo);
                    return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponseDTO(savedVeiculo));
                })
                .orElse(ResponseEntity.badRequest().build()); // Or a more specific error
    }

    @PutMapping("/{id}")
    public ResponseEntity<VeiculoResponseDTO> updateVeiculo(@PathVariable UUID id, @RequestBody @Valid VeiculoRequestDTO veiculoRequestDTO) {
        return veiculoService.findById(id)
                .map(existingVeiculo -> {
                    return usuarioService.findById(veiculoRequestDTO.getUsuarioId())
                            .map(usuario -> {
                                updateEntityFromDto(existingVeiculo, veiculoRequestDTO, usuario);
                                Veiculo updatedVeiculo = veiculoService.save(existingVeiculo);
                                return ResponseEntity.ok(convertToResponseDTO(updatedVeiculo));
                            })
                            .orElse(ResponseEntity.badRequest().build());
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVeiculo(@PathVariable UUID id) {
        if (veiculoService.findById(id).isPresent()) {
            veiculoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private VeiculoResponseDTO convertToResponseDTO(Veiculo veiculo) {
        VeiculoResponseDTO dto = new VeiculoResponseDTO();
        dto.setId(veiculo.getId());
        dto.setPlaca(veiculo.getPlaca());
        dto.setMarca(veiculo.getMarca());
        dto.setModelo(veiculo.getModelo());
        dto.setTipo(veiculo.getTipo());
        dto.setComprimento(veiculo.getComprimento());
        dto.setUsuarioId(veiculo.getUsuario().getId());
        dto.setCpfProprietario(veiculo.getCpfProprietario());
        dto.setCnpjProprietario(veiculo.getCnpjProprietario());
        return dto;
    }

    private Veiculo convertToEntity(VeiculoRequestDTO dto, Usuario usuario) {
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(dto.getPlaca());
        veiculo.setMarca(dto.getMarca());
        veiculo.setModelo(dto.getModelo());
        veiculo.setTipo(dto.getTipo());
        veiculo.setComprimento(dto.getComprimento());
        veiculo.setUsuario(usuario);
        veiculo.setCpfProprietario(dto.getCpfProprietario());
        veiculo.setCnpjProprietario(dto.getCnpjProprietario());
        return veiculo;
    }

    private void updateEntityFromDto(Veiculo veiculo, VeiculoRequestDTO dto, Usuario usuario) {
        veiculo.setPlaca(dto.getPlaca());
        veiculo.setMarca(dto.getMarca());
        veiculo.setModelo(dto.getModelo());
        veiculo.setTipo(dto.getTipo());
        veiculo.setComprimento(dto.getComprimento());
        veiculo.setUsuario(usuario);
        veiculo.setCpfProprietario(dto.getCpfProprietario());
        veiculo.setCnpjProprietario(dto.getCnpjProprietario());
    }
}