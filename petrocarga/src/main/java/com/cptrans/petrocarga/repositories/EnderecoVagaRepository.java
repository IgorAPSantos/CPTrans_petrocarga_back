package com.cptrans.petrocarga.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cptrans.petrocarga.models.EnderecoVaga;

@Repository
public interface EnderecoVagaRepository extends JpaRepository<EnderecoVaga, Integer>{
    public Optional<EnderecoVaga> findByCodigoPmp(String codigoPmp);
    public Optional<EnderecoVaga> findByLogradouro(String logradouro);
}
