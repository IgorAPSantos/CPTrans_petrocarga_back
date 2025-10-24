package com.cptrans.petrocarga.repositories;

import com.cptrans.petrocarga.models.ReservaRapida;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ReservaRapidaRepository extends JpaRepository<ReservaRapida, UUID> {
}
