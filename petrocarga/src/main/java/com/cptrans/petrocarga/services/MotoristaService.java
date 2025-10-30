package com.cptrans.petrocarga.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cptrans.petrocarga.dto.MotoristaRequestDTO;
import com.cptrans.petrocarga.dto.MotoristaResponseDTO;
import com.cptrans.petrocarga.enums.PermissaoEnum;
import com.cptrans.petrocarga.models.Empresa;
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

    @Autowired
    private EmpresaService empresaService;

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
    public MotoristaResponseDTO createMotorista(MotoristaRequestDTO motorista) {
        Usuario usuario = usuarioService.createUsuario(motorista.getUsuario().toEntity(), PermissaoEnum.MOTORISTA);
        if(motoristaRepository.existsByNumeroCnh(motorista.getNumeroCNH())) {
            throw new IllegalArgumentException("Número da CNH já cadastrado");
        }
        Motorista novoMotorista = new Motorista();

        if(motorista.getEmpresaId() != null) {
            Empresa empresa = empresaService.findById(motorista.getEmpresaId());
            novoMotorista.setEmpresa(empresa);
        }
        if(motorista.getDataValidadeCNH().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("CNH vencida");
        }
        novoMotorista.setUsuario(usuario);
        novoMotorista.setTipoCNH(motorista.getTipoCNH());
        novoMotorista.setNumeroCNH(motorista.getNumeroCNH());
        novoMotorista.setDataValidadeCNH(motorista.getDataValidadeCNH());
        Motorista motoristaSalvo = motoristaRepository.save(novoMotorista);
        return new MotoristaResponseDTO(motoristaSalvo);
    }

    @Transactional
    public Motorista updateMotorista(UUID usuarioId, MotoristaRequestDTO motoristaRequest) {
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

        if (motoristaRequest.getEmpresaId() != null) {
            Empresa empresa = empresaService.findById(motoristaRequest.getEmpresaId());
            //TODO: Criar lógica de update no EmpresaService
            motoristaCadastrado.setEmpresa(empresa);
        }

        Usuario usuarioAtualizado = usuarioService.updateUsuario(usuarioId, motoristaRequest.getUsuario().toEntity(), PermissaoEnum.MOTORISTA);

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
