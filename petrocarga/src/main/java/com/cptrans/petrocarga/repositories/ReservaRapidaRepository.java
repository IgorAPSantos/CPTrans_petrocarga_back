package com.cptrans.petrocarga.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cptrans.petrocarga.enums.StatusReservaEnum;
import com.cptrans.petrocarga.models.Agente;
import com.cptrans.petrocarga.models.ReservaRapida;
import com.cptrans.petrocarga.models.Vaga;

public interface ReservaRapidaRepository extends JpaRepository<ReservaRapida, UUID> {
    public List<ReservaRapida> findByStatus(StatusReservaEnum status);
    public List<ReservaRapida> findByVaga(Vaga vaga);
    public List<ReservaRapida> findByVagaAndStatus(Vaga vaga, StatusReservaEnum status);
    public List<ReservaRapida> findByPlacaIgnoringCaseAndStatus(String placa, StatusReservaEnum status);
    public List<ReservaRapida> findByAgente(Agente agente);
    public Integer countByPlaca(String placa);
}
