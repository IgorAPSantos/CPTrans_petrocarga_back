package com.cptrans.petrocarga.repositories;

import com.cptrans.petrocarga.models.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface EmpresaRepository extends JpaRepository<Empresa, UUID> {
}
