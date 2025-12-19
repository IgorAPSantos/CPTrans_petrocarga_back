package com.cptrans.petrocarga.repositories;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cptrans.petrocarga.enums.StatusReservaEnum;
import com.cptrans.petrocarga.models.Reserva;
import com.cptrans.petrocarga.models.Usuario;
import com.cptrans.petrocarga.models.Vaga;

public interface ReservaRepository extends JpaRepository<Reserva, UUID> {
    public List<Reserva> findByVaga(Vaga vaga);
    public List<Reserva> findByVagaAndStatus(Vaga vaga, StatusReservaEnum status);
    public List<Reserva> findByVagaAndStatusIn(Vaga vaga, List<StatusReservaEnum> status);
    public List<Reserva> findByCriadoPor(Usuario criadoPor);
    public List<Reserva> findByCriadoPorAndStatusIn(Usuario criadoPor, List<StatusReservaEnum> status);
    public List<Reserva> findByStatusIn(List<StatusReservaEnum> status);
    public List<Reserva> findByVagaAndStatusAndInicio(Vaga vaga, StatusReservaEnum status, OffsetDateTime data);
    public List<Reserva> findByVeiculoPlacaIgnoringCaseAndStatusIn(String placa, List<StatusReservaEnum> status);
    public Integer countByVeiculoPlacaIgnoringCase(String placa);
    public List<Reserva> findByFimGreaterThanAndInicioLessThan(OffsetDateTime novoInicio, OffsetDateTime novoFim);

    /**
     * Retorna reservas que estão ATIVAS e cujo fim já passou (<= agora).
     * Usado pelo job de auto-conclusão.
     */
    public List<Reserva> findByStatusAndFimBefore(StatusReservaEnum status, OffsetDateTime agora);

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
           "AND r.inicio <= :agora " +
           "AND FUNCTION('TIMESTAMPADD', MINUTE, :graceMinutes, r.inicio) <= :agora " +
           "AND r.fim > :agora")
    List<Reserva> findNoShowCandidates(
        @Param("status") StatusReservaEnum status,
        @Param("graceMinutes") int graceMinutes,
        @Param("agora") OffsetDateTime agora
    );

    @Query("SELECT CASE WHEN (COUNT(r) > 0) THEN true ELSE false END FROM Reserva r " +
           "WHERE r.vaga.id = :vagaId " +
           "AND (r.status = com.cptrans.petrocarga.enums.StatusReservaEnum.ATIVA OR r.status = com.cptrans.petrocarga.enums.StatusReservaEnum.RESERVADA) " +
           "AND r.inicio <= :dataHora " +
           "AND r.fim >= :dataHora")
    boolean existsByVagaIdAndHorarioOcupado(@Param("vagaId") UUID vagaId, @Param("dataHora") java.time.OffsetDateTime dataHora);
}
