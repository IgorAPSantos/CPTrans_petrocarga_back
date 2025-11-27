package com.cptrans.petrocarga.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cptrans.petrocarga.enums.PermissaoEnum;
import com.cptrans.petrocarga.models.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    public Optional<Usuario> findByEmail(String email);
    public Optional<Usuario> findByCpf(String cpf);
    public Boolean existsByEmail(String email);
    public Boolean existsByCpf(String cpf);
    public List<Usuario> findByPermissao(PermissaoEnum permissao);
    public List<Usuario> findByPermissaoAndAtivo(PermissaoEnum permissao, Boolean ativo);
}
