package com.cptrans.petrocarga.services;

import com.cptrans.petrocarga.models.VeiculoMotoristaAssociacao;
import com.cptrans.petrocarga.models.VeiculoMotoristaId;
import com.cptrans.petrocarga.repositories.VeiculoMotoristaAssociacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VeiculoMotoristaAssociacaoService {

    @Autowired
    private VeiculoMotoristaAssociacaoRepository veiculoMotoristaAssociacaoRepository;

    public List<VeiculoMotoristaAssociacao> findAll() {
        return veiculoMotoristaAssociacaoRepository.findAll();
    }

    public Optional<VeiculoMotoristaAssociacao> findById(VeiculoMotoristaId id) {
        return veiculoMotoristaAssociacaoRepository.findById(id);
    }

    public VeiculoMotoristaAssociacao save(VeiculoMotoristaAssociacao veiculoMotoristaAssociacao) {
        return veiculoMotoristaAssociacaoRepository.save(veiculoMotoristaAssociacao);
    }

    public void deleteById(VeiculoMotoristaId id) {
        veiculoMotoristaAssociacaoRepository.deleteById(id);
    }
}
