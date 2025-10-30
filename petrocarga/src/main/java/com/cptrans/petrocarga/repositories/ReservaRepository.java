package com.cptrans.petrocarga.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cptrans.petrocarga.enums.StatusReservaEnum;
import com.cptrans.petrocarga.models.Reserva;
import com.cptrans.petrocarga.models.Usuario;
import com.cptrans.petrocarga.models.Vaga;

public interface ReservaRepository extends JpaRepository<Reserva, UUID> {
    public List<Reserva> findByVaga(Vaga vagaId);
    public List<Reserva> findByVagaAndStatus(Vaga vagaId, StatusReservaEnum status);
    public List<Reserva> findByCriadoPor(Usuario criadoPor);
    public List<Reserva> findByCriadoPorAndStatus(Usuario criadoPor, StatusReservaEnum status);
    public List<Reserva> findByStatus(StatusReservaEnum status);
}
