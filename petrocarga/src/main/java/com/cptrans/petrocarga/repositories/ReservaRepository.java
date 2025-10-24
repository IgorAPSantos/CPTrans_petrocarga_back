package com.cptrans.petrocarga.repositories;

import com.cptrans.petrocarga.models.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ReservaRepository extends JpaRepository<Reserva, UUID> {
}
