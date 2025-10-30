package com.cptrans.petrocarga.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cptrans.petrocarga.models.VeiculoEmpresaMotorista;
import com.cptrans.petrocarga.models.VeiculoEmpresaMotoristaId;

public interface VeiculoMotoristaAssociacaoRepository extends JpaRepository<VeiculoEmpresaMotorista, VeiculoEmpresaMotoristaId> {
}
