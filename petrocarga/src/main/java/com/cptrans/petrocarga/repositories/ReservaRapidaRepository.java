package com.cptrans.petrocarga.repositories;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cptrans.petrocarga.enums.StatusReservaEnum;
import com.cptrans.petrocarga.models.Agente;
import com.cptrans.petrocarga.models.ReservaRapida;
import com.cptrans.petrocarga.models.Vaga;

@Repository
public interface ReservaRapidaRepository extends JpaRepository<ReservaRapida, UUID>, JpaSpecificationExecutor<ReservaRapida> {
    public List<ReservaRapida> findByStatusIn(List<StatusReservaEnum> status);
    public List<ReservaRapida> findByVaga(Vaga vaga);
    public List<ReservaRapida> findByVagaAndStatus(Vaga vaga, StatusReservaEnum status);
    public List<ReservaRapida> findByVagaAndStatusIn(Vaga vaga, List<StatusReservaEnum> status);
    public List<ReservaRapida> findByPlacaIgnoringCaseAndStatus(String placa, StatusReservaEnum status);
    public List<ReservaRapida> findByAgente(Agente agente);
    public List<ReservaRapida> findByAgenteAndVagaId(Agente agente, UUID vagaId);
    public List<ReservaRapida> findByAgenteAndPlacaIgnoringCase(Agente agente, String placaVeiculo);
    public List<ReservaRapida> findByAgenteAndStatusIn(Agente agente, List<StatusReservaEnum> listaStatus);
    public List<ReservaRapida> findByFimGreaterThanAndInicioLessThanAndStatusIn(OffsetDateTime novoInicio, OffsetDateTime novoFim, List<StatusReservaEnum> status);
    public List<ReservaRapida> findByAgenteAndVagaIdAndPlacaIgnoringCaseAndStatusIn(Agente agente, UUID vagaId, String placaVeiculo, List<StatusReservaEnum> status);
    public Integer countByPlacaIgnoringCase(String placa);
    
    @Query("SELECT COUNT(rr) FROM ReservaRapida rr WHERE rr.status = 'ATIVA' AND rr.inicio BETWEEN :startDate AND :endDate")
    Long countActiveReservations(@Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);

    @Query("SELECT COUNT(rr) FROM ReservaRapida rr WHERE rr.status = 'CONCLUIDA' AND rr.inicio BETWEEN :startDate AND :endDate")
    Long countCompletedReservations(@Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);

    @Query("SELECT COUNT(rr) FROM ReservaRapida rr WHERE rr.status = 'REMOVIDA' AND rr.inicio BETWEEN :startDate AND :endDate")
    Long countRemovedReservations(@Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);

    @Query("SELECT COUNT(rr) FROM ReservaRapida rr WHERE rr.inicio BETWEEN :startDate AND :endDate")
    Long countTotalReservationsInPeriod(@Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);

    // @Query("SELECT COUNT(rr) FROM ReservaRapida rr WHERE rr.inicio BETWEEN :startDate AND :endDate " +
    //        "AND (rr.tipo_veiculo = 'CAMINHAO_MEDIO' OR rr.tipo_veiculo = 'CAMINHAO_LONGO')")
    // Long countMultipleSlotReservations(@Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);


    // @Query("SELECT new map(CAST(rr.tipoVeiculo AS string) as tipo, COUNT(DISTINCT rr.id) as count, COUNT(DISTINCT rr.placaVeiculo) as uniqueVehicles) " +
    //        "FROM ReservaRapida rr " +
    //        "WHERE rr.tipoVeiculo IS NOT NULL " +
    //        "AND rr.inicio BETWEEN :startDate AND :endDate " +
    //        "GROUP BY rr.tipoVeiculo " +
    //        "ORDER BY count DESC")
    // List<java.util.Map<String, Object>> getVehicleTypeStats(@Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);

    @Query("SELECT new map(ev.bairro as name, COUNT(rr.id) as count) " +
           "FROM ReservaRapida rr " +
           "JOIN rr.vaga v " +
           "JOIN v.endereco ev " +
           "WHERE ev.bairro IS NOT NULL " +
           "AND rr.inicio BETWEEN :startDate AND :endDate " +
           "GROUP BY ev.bairro " +
           "ORDER BY count DESC")
    List<java.util.Map<String, Object>> getDistrictStats(@Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);

}
