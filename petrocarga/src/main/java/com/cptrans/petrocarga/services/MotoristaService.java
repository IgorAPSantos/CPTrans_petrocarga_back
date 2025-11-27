package com.cptrans.petrocarga.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cptrans.petrocarga.enums.PermissaoEnum;
import com.cptrans.petrocarga.models.Motorista;
import com.cptrans.petrocarga.models.Usuario;
import com.cptrans.petrocarga.repositories.MotoristaRepository;

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
        if(motoristaRepository.existsByNumeroCnh(novoMotorista.getNumeroCNH())) {
            throw new IllegalArgumentException("Número da CNH já cadastrado");
        }
        // if(novoMotorista.getEmpresa() != null) {
        //     Empresa empresa = empresaService.findById(novoMotorista.getEmpresa().getId());
        //     novoMotorista.setEmpresa(empresa);
        // }
        if(novoMotorista.getDataValidadeCNH().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("CNH vencida");
        }
        return  motoristaRepository.save(novoMotorista);
    }

    @Transactional
    public Motorista updateMotorista(UUID usuarioId, Motorista motoristaRequest) {
        Usuario usuarioCadastrado  = usuarioService.findById(usuarioId);
        if(usuarioCadastrado.getAtivo() == false) {
            throw new IllegalArgumentException("Usuário desativado.");
        }

        Motorista motoristaCadastrado = findByUsuarioId(usuarioId);
        
        if(motoristaRequest.getDataValidadeCNH().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("CNH vencida");
        }

        Optional<Motorista> motoristaByCnh = motoristaRepository.findByNumeroCnh(motoristaRequest.getNumeroCNH());

        if(motoristaByCnh.isPresent() && !motoristaByCnh.get().getId().equals(motoristaCadastrado.getId())){
            throw new IllegalArgumentException("Número da CNH já cadastrado");
        }

        // if (motoristaRequest.getEmpresa() != null) {
        //     Empresa empresa = empresaService.findById(motoristaRequest.getEmpresa().getId());
        //     //TODO: Criar lógica de update no EmpresaService
        //     motoristaCadastrado.setEmpresa(empresa);
        // }

        Usuario usuarioAtualizado = usuarioService.updateUsuario(usuarioId, motoristaRequest.getUsuario(), PermissaoEnum.MOTORISTA);
        motoristaCadastrado.setUsuario(usuarioAtualizado);
        motoristaCadastrado.setTipoCNH(motoristaRequest.getTipoCNH());
        motoristaCadastrado.setNumeroCNH(motoristaRequest.getNumeroCNH());
        motoristaCadastrado.setDataValidadeCNH(motoristaRequest.getDataValidadeCNH());

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
