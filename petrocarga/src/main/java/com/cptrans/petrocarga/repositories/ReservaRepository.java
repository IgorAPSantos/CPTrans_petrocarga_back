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
    public Boolean existsByVeiculoIdAndStatusIn(UUID veiculoId, List<StatusReservaEnum> status);

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

    @Query("SELECT COUNT(DISTINCT v.id) FROM Vaga v")
    Long countTotalSlots();

    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.status = 'RESERVADA' AND r.inicio BETWEEN :startDate AND :endDate")
    Long countPendingReservations(@Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);

    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.status = 'ATIVA' AND r.inicio BETWEEN :startDate AND :endDate")
    Long countActiveReservations(@Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);

    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.status = 'CONCLUIDA' AND r.inicio BETWEEN :startDate AND :endDate")
    Long countCompletedReservations(@Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);

    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.status = 'CANCELADA' AND r.inicio BETWEEN :startDate AND :endDate")
    Long countCanceledReservations(@Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);

    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.status = 'REMOVIDA' AND r.inicio BETWEEN :startDate AND :endDate")
    Long countRemovedReservations(@Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);

    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.inicio BETWEEN :startDate AND :endDate")
    Long countTotalReservationsInPeriod(@Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);

    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.inicio BETWEEN :startDate AND :endDate " +
           "AND (r.veiculo.tipo = 'CAMINHAO_MEDIO' OR r.veiculo.tipo = 'CAMINHAO_LONGO')")
    Long countMultipleSlotReservations(@Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);

    @Query("SELECT new map(CAST(r.veiculo.tipo AS string) as tipo, COUNT(DISTINCT r.id) as count, COUNT(DISTINCT r.veiculo.id) as uniqueVehicles) " +
           "FROM Reserva r " +
           "WHERE r.veiculo.tipo IS NOT NULL " +
           "AND r.inicio BETWEEN :startDate AND :endDate " +
           "GROUP BY r.veiculo.tipo " +
           "ORDER BY count DESC")
    List<java.util.Map<String, Object>> getVehicleTypeStats(@Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);

    @Query("SELECT new map(ev.bairro as name, COUNT(r.id) as count) " +
           "FROM Reserva r " +
           "JOIN r.vaga v " +
           "JOIN v.endereco ev " +
           "WHERE ev.bairro IS NOT NULL " +
           "AND r.inicio BETWEEN :startDate AND :endDate " +
           "GROUP BY ev.bairro " +
           "ORDER BY count DESC")
    List<java.util.Map<String, Object>> getDistrictStats(@Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);

    @Query("SELECT new map(r.cidadeOrigem as name, COUNT(r.id) as count) " +
           "FROM Reserva r " +
           "WHERE r.cidadeOrigem IS NOT NULL " +
           "AND r.inicio BETWEEN :startDate AND :endDate " +
           "GROUP BY r.cidadeOrigem " +
           "ORDER BY count DESC")
    List<java.util.Map<String, Object>> getOriginStats(@Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);

    @Query("SELECT new map(r.entradaCidade as name, COUNT(r.id) as count) " +
           "FROM Reserva r " +
           "WHERE r.entradaCidade IS NOT NULL " +
           "AND r.inicio BETWEEN :startDate AND :endDate " +
           "GROUP BY r.entradaCidade " +
           "ORDER BY count DESC")
    List<java.util.Map<String, Object>> getEntryOriginStats(@Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);

@Query(
  value = """
    SELECT
      COALESCE(CONCAT(ev.logradouro, ', ', v.numero_endereco), 'SEM ENDERECO') AS name,
      SUM(x.total_utilizacoes) AS count
    FROM (
       SELECT
         r.vaga_id,
         COUNT(1) AS total_utilizacoes
       FROM reserva r
       WHERE r.status = 'CONCLUIDA'
         AND r.inicio BETWEEN :startDate AND :endDate
       GROUP BY r.vaga_id

       UNION ALL

       SELECT
         rr.vaga_id,
         COUNT(1) AS total_utilizacoes
       FROM reserva_rapida rr
       WHERE rr.inicio BETWEEN :startDate AND :endDate
       GROUP BY rr.vaga_id
    ) x
    JOIN vaga v ON v.id = x.vaga_id
    LEFT JOIN endereco_vaga ev ON ev.id = v.endereco_id
    GROUP BY ev.logradouro, v.numero_endereco
    ORDER BY count DESC
  """,
  nativeQuery = true
)
List<Object[]> getMostUsedVagas(
    @Param("startDate") OffsetDateTime startDate,
    @Param("endDate") OffsetDateTime endDate
);

// @Query(value ="""
//               select EXISTS (
//     SELECT 1
//     FROM reserva r
//     LEFT JOIN motorista m ON r.motorista_id = m.id
//     WHERE
//         (r.criado_por = :usuarioId OR m.usuario_id = :usuarioId)
//         AND r.status IN ('RESERVADA', 'ATIVA')
// );
//        """,
//        nativeQuery = true)
// Boolean existsByCriadoPorIdOrMotoristaUsuarioId (@Param("usuarioId") UUID usuarioId);

Boolean existsByStatusInAndCriadoPorIdOrMotoristaUsuarioId (List<StatusReservaEnum> status, UUID criadoPorId, UUID motoristaUsuarioId);
}