package com.cptrans.petrocarga.repositories;

import com.cptrans.petrocarga.models.VeiculoMotoristaAssociacao;
import com.cptrans.petrocarga.models.VeiculoMotoristaId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VeiculoMotoristaAssociacaoRepository extends JpaRepository<VeiculoMotoristaAssociacao, VeiculoMotoristaId> {
}
