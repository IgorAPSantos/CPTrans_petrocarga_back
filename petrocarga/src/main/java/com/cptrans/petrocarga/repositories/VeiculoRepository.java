package com.cptrans.petrocarga.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cptrans.petrocarga.models.Usuario;
import com.cptrans.petrocarga.models.Veiculo;

public interface VeiculoRepository extends JpaRepository<Veiculo, UUID> {
    public List<Veiculo> findByPlaca(String placa);
    public Optional<Veiculo> findByPlacaAndUsuario(String placa, Usuario usuario);
    public List<Veiculo> findByUsuario(Usuario usuario);
    public Boolean existsByPlaca(String placa);
}
