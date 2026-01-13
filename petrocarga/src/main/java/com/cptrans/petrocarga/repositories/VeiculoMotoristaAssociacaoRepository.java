package com.cptrans.petrocarga.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cptrans.petrocarga.models.VeiculoEmpresaMotorista;
import com.cptrans.petrocarga.models.VeiculoEmpresaMotoristaId;

@Repository
public interface VeiculoMotoristaAssociacaoRepository extends JpaRepository<VeiculoEmpresaMotorista, VeiculoEmpresaMotoristaId> {
}
