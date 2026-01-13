package com.cptrans.petrocarga.repositories;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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
    public List<ReservaRapida> findByFimGreaterThanAndInicioLessThan(OffsetDateTime novoInicio, OffsetDateTime novoFim);
    public List<ReservaRapida> findByAgenteAndVagaIdAndPlacaIgnoringCaseAndStatusIn(Agente agente, UUID vagaId, String placaVeiculo, List<StatusReservaEnum> status);
    public Integer countByPlaca(String placa);
}
