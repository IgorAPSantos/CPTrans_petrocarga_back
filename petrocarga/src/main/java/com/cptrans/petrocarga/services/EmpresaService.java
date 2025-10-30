package com.cptrans.petrocarga.services;

import java.util.List;

import com.cptrans.petrocarga.models.Empresa;
import com.cptrans.petrocarga.repositories.EmpresaRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
// TODO: Implementar create e update (e o que mais for necessario)
@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    public List<Empresa> findAll() {
        return empresaRepository.findAll();
    }

    public Empresa findById(UUID id) {
        return empresaRepository.findById(id).orElseThrow(() ->new EntityNotFoundException("Empresa não encontrada."));
    }

    public Empresa findByUsuarioId(UUID usuarioId) {
        return empresaRepository.findByUsuarioId(usuarioId).orElseThrow(() -> new EntityNotFoundException("Empresa nao encontrada."));
    }

    public void deleteById(UUID id) {
        Empresa empresa = empresaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Empresa nao encontrada."));
        empresaRepository.deleteById(empresa.getId());
    }
}
