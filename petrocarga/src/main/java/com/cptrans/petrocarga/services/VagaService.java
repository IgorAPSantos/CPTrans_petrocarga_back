package com.cptrans.petrocarga.services;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cptrans.petrocarga.dto.VagaRequestDTO;
import com.cptrans.petrocarga.models.EnderecoVaga;
import com.cptrans.petrocarga.models.OperacaoVaga;
import com.cptrans.petrocarga.models.Vaga;
import com.cptrans.petrocarga.repositories.VagaRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional; 


@Service
public class VagaService {
    @Autowired
    private VagaRepository vagaRepository;
    @Autowired
    private EnderecoVagaService enderecoVagaService;
    @Autowired
    private OperacaoVagaService operacaoVagaService; 

    

    public List<Vaga> listarVagas() {
        try {
            return vagaRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar vagas: " + e.getMessage());
        }
    }
    
    
    public void deletarVaga(UUID id) {
        Vaga vaga = vagaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Vaga com ID " + id + " não encontrada."));
        
        vagaRepository.deleteById(vaga.getId());
    }

    
    @Transactional
    public Vaga atualizarParcialmenteVaga(UUID id, VagaRequestDTO vagaRequest) {
        Vaga vagaExistente = vagaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Vaga com ID " + id + " não encontrada."));

        if(vagaRequest.getEndereco() != null) vagaExistente.setEndereco(enderecoVagaService.cadastrarEnderecoVaga(vagaRequest.getEndereco()));
        
        if(vagaRequest.getArea() != null) vagaExistente.setArea(vagaRequest.getArea());
        if(vagaExistente.getNumeroEndereco() != null) vagaExistente.setNumeroEndereco(vagaRequest.getNumeroEndereco());
        if(vagaRequest.getReferenciaEndereco() != null) vagaExistente.setReferenciaEndereco(vagaRequest.getReferenciaEndereco());
        if(vagaRequest.getTipoVaga() != null) vagaExistente.setTipoVaga(vagaRequest.getTipoVaga());
        if(vagaRequest.getReferenciaGeoInicio() != null) vagaExistente.setReferenciaGeoInicio(vagaRequest.getReferenciaGeoInicio());
        if(vagaRequest.getReferenciaGeoFim() != null) vagaExistente.setReferenciaGeoFim(vagaRequest.getReferenciaGeoFim());
        if(vagaRequest.getMaxEixos() != null) vagaExistente.setMaxEixos(vagaRequest.getMaxEixos());
        if(vagaRequest.getComprimento() != null) vagaExistente.setComprimento(vagaRequest.getComprimento());
        if(vagaRequest.getStatus() != null) vagaExistente.setStatus(vagaRequest.getStatus());
        if(vagaRequest.getOperacoesVaga() != null && !vagaRequest.getOperacoesVaga().isEmpty()){
            Set<OperacaoVaga> operacoes = vagaRequest.getOperacoesVaga().stream().map(dto -> {
                OperacaoVaga op = new OperacaoVaga();
                op.setDiaSemana(dto.getDiaSemana());
                op.setHoraInicio(dto.getHoraInicio());
                op.setHoraFim(dto.getHoraFim());
                op.setVaga(vagaExistente); 
                return op;
            }).collect(Collectors.toSet());
            vagaExistente.setOperacoesVaga(operacoes);
        }
        Vaga vagaAtualizada = vagaRepository.save(vagaExistente);
        operacaoVagaService.salvarOperacaoVaga(vagaAtualizada.getOperacoesVaga());
        
        return vagaAtualizada;
    }
    
    @Transactional()
    public Vaga cadastrarVaga(VagaRequestDTO vagaRequest){
        EnderecoVaga enderecoVaga = enderecoVagaService.cadastrarEnderecoVaga(vagaRequest.getEndereco());
        
        Vaga vaga = new Vaga();
        
        if(vagaRequest.getComprimento() == null) {
            throw new IllegalArgumentException("O campo 'comprimento' é obrigatório e não pode ser nulo ou vazio.");
        }
        
        vaga.setEndereco(enderecoVaga);
        vaga.setArea(vagaRequest.getArea());
        vaga.setNumeroEndereco(vagaRequest.getNumeroEndereco());
        vaga.setReferenciaEndereco(vagaRequest.getReferenciaEndereco());
        vaga.setTipoVaga(vagaRequest.getTipoVaga());
        vaga.setReferenciaGeoInicio(vagaRequest.getReferenciaGeoInicio());
        vaga.setReferenciaGeoFim(vagaRequest.getReferenciaGeoFim());
        vaga.setMaxEixos(vagaRequest.getMaxEixos());
        vaga.setComprimento(vagaRequest.getComprimento());
        vaga.setStatus(vagaRequest.getStatus());

        if (vagaRequest.getOperacoesVaga() != null && !vagaRequest.getOperacoesVaga().isEmpty()) {
            Set<OperacaoVaga> operacoes = vagaRequest.getOperacoesVaga().stream().map(dto -> {
                OperacaoVaga op = new OperacaoVaga();
                op.setDiaSemana(dto.getDiaSemana());
                op.setHoraInicio(dto.getHoraInicio());
                op.setHoraFim(dto.getHoraFim());
                op.setVaga(vaga); 
                return op;
            }).collect(Collectors.toSet());
            vaga.setOperacoesVaga(operacoes);
        }
        Vaga novaVaga = vagaRepository.save(vaga);
        operacaoVagaService.salvarOperacaoVaga(novaVaga.getOperacoesVaga());
        return novaVaga;
    }
}