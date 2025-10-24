POspackage com.cptrans.petrocarga.controllers;

import com.cptrans.petrocarga.dto.EmpresaRequestDTO;
import com.cptrans.petrocarga.dto.EmpresaResponseDTO;
import com.cptrans.petrocarga.models.Empresa;
import com.cptrans.petrocarga.models.Usuario;
import com.cptrans.petrocarga.services.EmpresaService;
import com.cptrans.petrocarga.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<EmpresaResponseDTO>> getAllEmpresas() {
        List<EmpresaResponseDTO> empresas = empresaService.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(empresas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaResponseDTO> getEmpresaById(@PathVariable UUID id) {
        return empresaService.findById(id)
                .map(this::convertToResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EmpresaResponseDTO> createEmpresa(@RequestBody @Valid EmpresaRequestDTO empresaRequestDTO) {
        return usuarioService.findById(empresaRequestDTO.getUsuarioId())
                .map(usuario -> {
                    Empresa empresa = convertToEntity(empresaRequestDTO, usuario);
                    Empresa savedEmpresa = empresaService.save(empresa);
                    return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponseDTO(savedEmpresa));
                })
                .orElse(ResponseEntity.badRequest().build()); // Or a more specific error
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpresaResponseDTO> updateEmpresa(@PathVariable UUID id, @RequestBody @Valid EmpresaRequestDTO empresaRequestDTO) {
        return empresaService.findById(id)
                .map(existingEmpresa -> {
                    return usuarioService.findById(empresaRequestDTO.getUsuarioId())
                            .map(usuario -> {
                                updateEntityFromDto(existingEmpresa, empresaRequestDTO, usuario);
                                Empresa updatedEmpresa = empresaService.save(existingEmpresa);
                                return ResponseEntity.ok(convertToResponseDTO(updatedEmpresa));
                            })
                            .orElse(ResponseEntity.badRequest().build());
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmpresa(@PathVariable UUID id) {
        if (empresaService.findById(id).isPresent()) {
            empresaService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private EmpresaResponseDTO convertToResponseDTO(Empresa empresa) {
        EmpresaResponseDTO dto = new EmpresaResponseDTO();
        dto.setId(empresa.getId());
        dto.setUsuarioId(empresa.getUsuario().getId());
        dto.setCnpj(empresa.getCnpj());
        dto.setRazaoSocial(empresa.getRazaoSocial());
        return dto;
    }

    private Empresa convertToEntity(EmpresaRequestDTO dto, Usuario usuario) {
        Empresa empresa = new Empresa();
        empresa.setUsuario(usuario);
        empresa.setCnpj(dto.getCnpj());
        empresa.setRazaoSocial(dto.getRazaoSocial());
        return empresa;
    }

    private void updateEntityFromDto(Empresa empresa, EmpresaRequestDTO dto, Usuario usuario) {
        empresa.setUsuario(usuario);
        empresa.setCnpj(dto.getCnpj());
        empresa.setRazaoSocial(dto.getRazaoSocial());
    }
}