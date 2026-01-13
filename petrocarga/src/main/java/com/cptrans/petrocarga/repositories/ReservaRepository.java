package com.cptrans.petrocarga.repositories;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cptrans.petrocarga.enums.StatusReservaEnum;
import com.cptrans.petrocarga.models.Reserva;
import com.cptrans.petrocarga.models.Usuario;
import com.cptrans.petrocarga.models.Vaga;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, UUID> {
    public List<Reserva> findByVaga(Vaga vaga);
    public List<Reserva> findByVagaAndStatus(Vaga vaga, StatusReservaEnum status);
    public List<Reserva> findByVagaAndStatusIn(Vaga vaga, List<StatusReservaEnum> status);
    public List<Reserva> findByCriadoPor(Usuario criadoPor);
    public List<Reserva> findByCriadoPorAndStatusIn(Usuario criadoPor, List<StatusReservaEnum> status);
    public List<Reserva> findByStatusIn(List<StatusReservaEnum> status);
    public List<Reserva> findByVagaAndStatusAndInicio(Vaga vaga, StatusReservaEnum status, OffsetDateTime data);
    public List<Reserva> findByVeiculoPlacaIgnoringCaseAndStatusIn(String placa, List<StatusReservaEnum> status);
    public Integer countByVeiculoPlacaIgnoringCaseAndStatusIn(String placa,List<StatusReservaEnum> status);
    public List<Reserva> findByFimGreaterThanAndInicioLessThan(OffsetDateTime novoInicio, OffsetDateTime novoFim);

    @Query("SELECT r FROM Reserva r " +
           "JOIN FETCH r.vaga " +
           "JOIN FETCH r.motorista " +
           "JOIN FETCH r.veiculo " +
           "JOIN FETCH r.criadoPor " +
           "WHERE r.id = :id")
    Reserva findByIdWithJoins(@Param("id") UUID id);

    @Query("SELECT r FROM Reserva r " +
           "WHERE r.status = :status " +
           "AND r.checkedIn = false " +
           "AND r.inicio < :agora " +
           "AND FUNCTION('TIMESTAMPADD', MINUTE, :graceMinutes, r.inicio) <= :agora " +
           "AND r.fim > :agora")
    List<Reserva> findNoShowCandidates(
        @Param("status") StatusReservaEnum status,
        @Param("graceMinutes") int graceMinutes,
        @Param("agora") OffsetDateTime agora
    );
}
