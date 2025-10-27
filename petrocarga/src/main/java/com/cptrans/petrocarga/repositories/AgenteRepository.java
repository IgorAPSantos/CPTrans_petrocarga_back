package com.cptrans.petrocarga.repositories;

import com.cptrans.petrocarga.models.Agente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AgenteRepository extends JpaRepository<Agente, UUID> {
}
