package com.cptrans.petrocarga.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cptrans.petrocarga.models.DiaSemana;

@Repository
public interface DiaSemanaRepository extends JpaRepository<DiaSemana, Integer> {
    
}
