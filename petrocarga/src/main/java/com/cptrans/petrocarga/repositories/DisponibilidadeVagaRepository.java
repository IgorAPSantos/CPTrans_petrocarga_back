package com.cptrans.petrocarga.repositories;

import com.cptrans.petrocarga.models.DisponibilidadeVaga;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DisponibilidadeVagaRepository extends JpaRepository<DisponibilidadeVaga, UUID> {
}
