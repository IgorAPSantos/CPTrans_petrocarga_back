package com.cptrans.petrocarga.repositories;

import java.util.List;
import java.util.Optional;

import com.cptrans.petrocarga.models.Veiculo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

import com.cptrans.petrocarga.models.Usuario;

public interface VeiculoRepository extends JpaRepository<Veiculo, UUID> {
    public Optional<Veiculo> findByPlaca(String placa);
    public List<Veiculo> findByUsuario(Usuario usuario);
    public Boolean existsByPlaca(String placa);
}
