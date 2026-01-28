package com.cptrans.petrocarga.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.cptrans.petrocarga.enums.PermissaoEnum;
import com.cptrans.petrocarga.models.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID>, JpaSpecificationExecutor<Usuario> {
    public Optional<Usuario> findByEmail(String email);
    public Optional<Usuario> findByEmailOrCpf(String email, String cpf);
    public Optional<Usuario> findByCpf(String cpf);
    public Boolean existsByEmail(String email);
    public Boolean existsByCpf(String cpf);
    public List<Usuario> findByPermissao(PermissaoEnum permissao);
    public List<Usuario> findByPermissaoAndAtivo(PermissaoEnum permissao, Boolean ativo);
    public Optional<Usuario> findByIdAndAtivo(UUID id, Boolean ativo);
}
