package com.cptrans.petrocarga.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cptrans.petrocarga.dto.MotoristaFiltrosDTO;
import com.cptrans.petrocarga.dto.UsuarioPATCHRequestDTO;
import com.cptrans.petrocarga.enums.PermissaoEnum;
import com.cptrans.petrocarga.models.Motorista;
import com.cptrans.petrocarga.models.Usuario;
import com.cptrans.petrocarga.repositories.MotoristaRepository;
import com.cptrans.petrocarga.specification.MotoristaSpecification;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class MotoristaService {

    @Autowired
    private MotoristaRepository motoristaRepository;

    @Autowired
    private UsuarioService usuarioService;

    // @Autowired
    // private EmpresaService empresaService;

    public List<Motorista> findAll() {
        return motoristaRepository.findAll();
    }

    public List<Motorista> findAllWithFiltros(MotoristaFiltrosDTO filtros) {
        return motoristaRepository.findAll(MotoristaSpecification.filtrar(filtros));
    }

    public Motorista findByUsuarioId(UUID usuarioId) {
        Usuario usuario = usuarioService.findById(usuarioId);
        Motorista motorista = motoristaRepository.findByUsuario(usuario)
                .orElseThrow(() -> new IllegalArgumentException("Motorista não encontrado"));
        return motorista;
    }

    @Transactional
    public Motorista createMotorista(Motorista novoMotorista) {
        Usuario usuario = usuarioService.createUsuario(novoMotorista.getUsuario(), PermissaoEnum.MOTORISTA);
        novoMotorista.setUsuario(usuario);
        if(motoristaRepository.existsByNumeroCnh(novoMotorista.getNumeroCnh())) {
            throw new IllegalArgumentException("Número da CNH já cadastrado");
        }
        // if(novoMotorista.getEmpresa() != null) {
        //     Empresa empresa = empresaService.findById(novoMotorista.getEmpresa().getId());
        //     novoMotorista.setEmpresa(empresa);
        // }
        if(novoMotorista.getDataValidadeCnh().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("CNH vencida");
        }
        return  motoristaRepository.save(novoMotorista);
    }

    @Transactional
    public Motorista updateMotorista(UUID usuarioId, UsuarioPATCHRequestDTO motoristaRequest) {
        Motorista motoristaCadastrado = findByUsuarioId(usuarioId);
        
        if(motoristaRequest.getDataValidadeCnh() != null) {
            if(motoristaRequest.getDataValidadeCnh().isBefore(LocalDate.now())) throw new IllegalArgumentException("Cnh vencida");
            motoristaCadastrado.setDataValidadeCnh(motoristaRequest.getDataValidadeCnh());
        }
        if (motoristaRequest.getNumeroCnh() != null) {
            Optional<Motorista> motoristaByCnh = motoristaRepository.findByNumeroCnh(motoristaRequest.getNumeroCnh());
            if(motoristaByCnh.isPresent() && !motoristaByCnh.get().getId().equals(motoristaCadastrado.getId())){
                throw new IllegalArgumentException("Número da Cnh já cadastrado");
            }
            motoristaCadastrado.setNumeroCnh(motoristaRequest.getNumeroCnh());
        }
        if (motoristaRequest.getTipoCnh() != null) {
            motoristaCadastrado.setTipoCnh(motoristaRequest.getTipoCnh());
        }

        // if (motoristaRequest.getEmpresa() != null) {
        //     Empresa empresa = empresaService.findById(motoristaRequest.getEmpresa().getId());
        //     //TODO: Criar lógica de update no EmpresaService
        //     motoristaCadastrado.setEmpresa(empresa);
        // }

        Usuario usuarioAtualizado = usuarioService.patchUpdate(usuarioId, PermissaoEnum.MOTORISTA, motoristaRequest);
        motoristaCadastrado.setUsuario(usuarioAtualizado);

        return motoristaRepository.save(motoristaCadastrado);
    }

    public Motorista findById(UUID id) {
        return motoristaRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Motorista nao encontrado."));
    }

    public void deleteByUsuarioId(UUID usuarioId) {
        Motorista motorista = findByUsuarioId(usuarioId);
        motoristaRepository.deleteById(motorista.getId());
    }
}
