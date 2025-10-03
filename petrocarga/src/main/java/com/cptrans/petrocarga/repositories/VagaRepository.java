package com.cptrans.petrocarga.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cptrans.petrocarga.models.Vaga;

@Repository
public interface  VagaRepository extends JpaRepository<Vaga, Integer> {

    public Optional<Vaga> findByLocalizacao(String localizacao);
}
