package com.cptrans.petrocarga.repositories;

import com.cptrans.petrocarga.models.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface VeiculoRepository extends JpaRepository<Veiculo, UUID> {
}
