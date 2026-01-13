package com.cptrans.petrocarga.services;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cptrans.petrocarga.dto.UsuarioPATCHRequestDTO;
import com.cptrans.petrocarga.enums.PermissaoEnum;
import com.cptrans.petrocarga.models.Usuario;
import com.cptrans.petrocarga.repositories.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;
    
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Usuario findById(UUID id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Usuario não encontrado."));
    }
    
    public Usuario findByIdAndAtivo(UUID id, Boolean ativo) {
        return usuarioRepository.findByIdAndAtivo(id, ativo).orElseThrow(() -> new EntityNotFoundException("Usuario não encontrado."));
    }

    public List<Usuario> findByPermissao(PermissaoEnum permissao) {
        return usuarioRepository.findByPermissao(permissao);
    }

    public List<Usuario> findByPermissaoAndAtivo(PermissaoEnum permissao, Boolean ativo) {
        return usuarioRepository.findByPermissaoAndAtivo(permissao, ativo);
    }

    public Usuario createUsuario(Usuario novoUsuario, PermissaoEnum permissao) {
        if(usuarioRepository.findByEmail(novoUsuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado");
        }
        if(usuarioRepository.findByCpf(novoUsuario.getCpf()).isPresent()) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }
        novoUsuario.setPermissao(permissao);
        novoUsuario.setSenha(passwordEncoder.encode(novoUsuario.getSenha()));
        // Ensure user is created as inactive and generate activation code
        novoUsuario.setAtivo(false);

        SecureRandom random = new SecureRandom();
        int code = random.nextInt(1_000_000);
        String codeStr = String.format("%06d", code);
        novoUsuario.setVerificationCode(codeStr);
        novoUsuario.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10));

        Usuario saved = usuarioRepository.save(novoUsuario);

        // Send activation code via email (best-effort)
        try {
            emailService.sendActivationCode(saved.getEmail(), codeStr);
        } catch (Exception e) {
            // log or rethrow depending on desired behavior; keep it simple here
        }

        return saved;
    }

    @Transactional
    public Usuario activateAccount(String email, String code) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        if (usuario.getVerificationCode() == null || !usuario.getVerificationCode().equals(code)) {
            throw new IllegalArgumentException("Código inválido.");
        }

        if (usuario.getVerificationCodeExpiresAt() == null || usuario.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Código expirado.");
        }

        usuario.setAtivo(true);
        usuario.setVerificationCode(null);
        usuario.setVerificationCodeExpiresAt(null);

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void resendActivationCode(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        if (usuario.getAtivo() != null && usuario.getAtivo()) {
            throw new IllegalArgumentException("Usuário já ativado.");
        }

        SecureRandom random = new SecureRandom();
        int code = random.nextInt(1_000_000);
        String codeStr = String.format("%06d", code);
        usuario.setVerificationCode(codeStr);
        usuario.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10));

        usuarioRepository.save(usuario);

        try {
            emailService.sendActivationCode(usuario.getEmail(), codeStr);
        } catch (Exception e) {
            // best-effort: ignore or log
        }
    }

    public Usuario updateUsuario(UUID id, Usuario novoUsuario, PermissaoEnum permissao) {
        Usuario usuarioExistente = findByIdAndAtivo(id, true);

        if(!usuarioExistente.getEmail().equals(novoUsuario.getEmail())) {
            Optional<Usuario> usuarioByEmail = usuarioRepository.findByEmail(novoUsuario.getEmail());
            if (usuarioByEmail.isPresent() && !usuarioByEmail.get().getId().equals(id)) {
                throw new IllegalArgumentException("Email já cadastrado");
            }
            usuarioExistente.setEmail(novoUsuario.getEmail());
        }

        if (!usuarioExistente.getCpf().equals(novoUsuario.getCpf())) {
            Optional<Usuario> usuarioByCpf = usuarioRepository.findByCpf(novoUsuario.getCpf());
            if (usuarioByCpf.isPresent() && !usuarioByCpf.get().getId().equals(id)) {
                throw new IllegalArgumentException("CPF já cadastrado");
            }
            usuarioExistente.setCpf(novoUsuario.getCpf());
        }

        usuarioExistente.setNome(novoUsuario.getNome());
        usuarioExistente.setTelefone(novoUsuario.getTelefone());

        if (novoUsuario.getSenha() != null && !novoUsuario.getSenha().isEmpty()) {
            usuarioExistente.setSenha(passwordEncoder.encode(novoUsuario.getSenha()));
        }
        usuarioExistente.setPermissao(permissao);
        return usuarioRepository.save(usuarioExistente);
    }

    public Usuario patchUpdate(UUID id, PermissaoEnum permissao, UsuarioPATCHRequestDTO patchRequestDTO) {
        Usuario usuarioExistente = findByIdAndAtivo(id, true);

        if (!usuarioExistente.getPermissao().equals(permissao)) throw new IllegalArgumentException("Permissão inválida para o usuário.");

        if (patchRequestDTO.getNome() != null) usuarioExistente.setNome(patchRequestDTO.getNome());
        if (patchRequestDTO.getTelefone() != null) usuarioExistente.setTelefone(patchRequestDTO.getTelefone());
        if (patchRequestDTO.getEmail() != null) {
            Optional<Usuario> usuarioByEmail = usuarioRepository.findByEmail(patchRequestDTO.getEmail());
            if (usuarioByEmail.isPresent() && !usuarioByEmail.get().getId().equals(id))  throw new IllegalArgumentException("Email já cadastrado");
            usuarioExistente.setEmail(patchRequestDTO.getEmail());
        }
        if (patchRequestDTO.getCpf() != null) {
            Optional<Usuario> usuarioByCpf = usuarioRepository.findByCpf(patchRequestDTO.getCpf());
            if (usuarioByCpf.isPresent() && !usuarioByCpf.get().getId().equals(id)) throw new IllegalArgumentException("CPF já cadastrado");
            usuarioExistente.setCpf(patchRequestDTO.getCpf());
        }
        if (patchRequestDTO.getSenha() != null) {
            usuarioExistente.setSenha(passwordEncoder.encode(patchRequestDTO.getSenha()));
        }
        // if(permissao.equals(PermissaoEnum.MOTORISTA)) {
        //     Motorista motorista = motoristaRepository.findByUsuario(usuarioExistente).orElseThrow(() -> new IllegalArgumentException("Motorista não encontrado."));
        //     if(patchRequestDTO.getNumeroCnh().isPresent()) {
        //        Motorista motoristaByCnh = motoristaRepository.findByNumeroCnh(patchRequestDTO.getNumeroCnh().get()).get();
        //        if(!motoristaByCnh.getId().equals(motorista.getId())) throw new IllegalArgumentException("CNH já cadastrada");
        //         motorista.setNumeroCNH(patchRequestDTO.getNumeroCnh().get());
        //     }
        //     if(patchRequestDTO.getTipoCnh().isPresent()) {
        //         motorista.setTipoCNH(patchRequestDTO.getTipoCnh().get());
        //     }
        //     if(patchRequestDTO.getDataValidadeCnh().isPresent()) {
        //         if(patchRequestDTO.getDataValidadeCnh().get().isBefore(LocalDate.now())) {
        //             throw new IllegalArgumentException("Data de validade da CNH nao pode ser menor que a data atual.");
        //         }
        //         motorista.setDataValidadeCNH(patchRequestDTO.getDataValidadeCnh().get());
        //     }
        //     return motoristaRepository.save(motorista).getUsuario();
        // }
        return usuarioRepository.save(usuarioExistente);
    }
    public void deleteById(UUID id) {
        usuarioRepository.deleteById(id);
    }
}
