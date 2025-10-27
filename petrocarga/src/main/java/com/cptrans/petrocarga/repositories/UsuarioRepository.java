package com.cptrans.petrocarga.repositories;

import com.cptrans.petrocarga.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
}
