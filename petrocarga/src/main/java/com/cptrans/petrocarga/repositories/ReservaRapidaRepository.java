package com.cptrans.petrocarga.repositories;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cptrans.petrocarga.enums.StatusReservaEnum;
import com.cptrans.petrocarga.models.Agente;
import com.cptrans.petrocarga.models.ReservaRapida;
import com.cptrans.petrocarga.models.Vaga;

public interface ReservaRapidaRepository extends JpaRepository<ReservaRapida, UUID> {
    public List<ReservaRapida> findByStatusIn(List<StatusReservaEnum> status);
    public List<ReservaRapida> findByVaga(Vaga vaga);
    public List<ReservaRapida> findByVagaAndStatus(Vaga vaga, StatusReservaEnum status);
    public List<ReservaRapida> findByVagaAndStatusIn(Vaga vaga, List<StatusReservaEnum> status);
    public List<ReservaRapida> findByPlacaIgnoringCaseAndStatus(String placa, StatusReservaEnum status);
    public List<ReservaRapida> findByAgente(Agente agente);
    public List<ReservaRapida> findByFimGreaterThanAndInicioLessThan(OffsetDateTime novoInicio, OffsetDateTime novoFim);
    public Integer countByPlaca(String placa);

    @org.springframework.data.jpa.repository.Query("SELECT r FROM ReservaRapida r " +
           "WHERE r.status = :status " +
           "AND r.inicio <= :agora " +
           "AND FUNCTION('TIMESTAMPADD', MINUTE, :graceMinutes, r.inicio) <= :agora " +
           "AND r.fim > :agora")
    public List<ReservaRapida> findNoShowCandidates(
        @org.springframework.data.repository.query.Param("status") StatusReservaEnum status,
        @org.springframework.data.repository.query.Param("graceMinutes") int graceMinutes,
        @org.springframework.data.repository.query.Param("agora") OffsetDateTime agora
    );

    @org.springframework.data.jpa.repository.Query("SELECT CASE WHEN (COUNT(r) > 0) THEN true ELSE false END FROM ReservaRapida r " +
           "WHERE r.vaga.id = :vagaId " +
           "AND (r.status = com.cptrans.petrocarga.enums.StatusReservaEnum.ATIVA OR r.status = com.cptrans.petrocarga.enums.StatusReservaEnum.RESERVADA) " +
           "AND r.inicio <= :dataHora " +
           "AND r.fim >= :dataHora")
    public boolean existsByVagaIdAndHorarioOcupado(@org.springframework.data.repository.query.Param("vagaId") UUID vagaId, @org.springframework.data.repository.query.Param("dataHora") java.time.OffsetDateTime dataHora);

    public List<ReservaRapida> findByStatusAndFimBefore(StatusReservaEnum status, OffsetDateTime agora);
}
