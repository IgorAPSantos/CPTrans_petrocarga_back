package com.cptrans.petrocarga.repositories;

import com.cptrans.petrocarga.models.Motorista;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface MotoristaRepository extends JpaRepository<Motorista, UUID> {
}
