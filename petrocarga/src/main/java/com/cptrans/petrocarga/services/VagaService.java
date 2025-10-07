package com.cptrans.petrocarga.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cptrans.petrocarga.dto.VagaPatchDTO;
import com.cptrans.petrocarga.dto.VagaRequestDTO;
import com.cptrans.petrocarga.models.EnderecoVaga;
import com.cptrans.petrocarga.models.OperacaoVaga;
import com.cptrans.petrocarga.models.Vaga;
import com.cptrans.petrocarga.repositories.EnderecoVagaRepository;
import com.cptrans.petrocarga.repositories.VagaRepository;

import jakarta.transaction.Transactional;
import jakarta.persistence.EntityNotFoundException; 


@Service
public class VagaService {
    @Autowired
    private VagaRepository vagaRepository;
    @Autowired
    private EnderecoVagaService enderecoVagaService;
   
    @Autowired
    private EnderecoVagaRepository enderecoVagaRepository; 

    

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

    
 
    public Vaga atualizarParcialmenteVaga(UUID id, VagaPatchDTO VagaPatchDTO) {
        Vaga vagaExistente = vagaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Vaga com ID " + id + " não encontrada."));


        if (VagaPatchDTO.getEnderecoId() != null) {
            EnderecoVaga novoEndereco = enderecoVagaRepository.findById(VagaPatchDTO.getEnderecoId())
                .orElseThrow(() -> new EntityNotFoundException("Endereço com ID " + VagaPatchDTO.getEnderecoId() + " não encontrado."));
            vagaExistente.setEndereco(novoEndereco);
        }
        
        if (VagaPatchDTO.getArea() != null) {
            vagaExistente.setArea(VagaPatchDTO.getArea());
        }

        if (VagaPatchDTO.getNumeroEndereco() != null) {
            vagaExistente.setNumeroEndereco(VagaPatchDTO.getNumeroEndereco());
        }

        if (VagaPatchDTO.getReferenciaEndereco() != null) {
            vagaExistente.setReferenciaEndereco(VagaPatchDTO.getReferenciaEndereco());
        }

        if (VagaPatchDTO.getTipoVaga() != null) {
            vagaExistente.setTipoVaga(VagaPatchDTO.getTipoVaga());
        }

        if (VagaPatchDTO.getReferenciaGeoInicio() != null) {
            vagaExistente.setReferenciaGeoInicio(VagaPatchDTO.getReferenciaGeoInicio());
        }

        if (VagaPatchDTO.getReferenciaGeoFim() != null) {
            vagaExistente.setReferenciaGeoFim(VagaPatchDTO.getReferenciaGeoFim());
        }

        if (VagaPatchDTO.getMaxEixos() != null) {
            vagaExistente.setMaxEixos(VagaPatchDTO.getMaxEixos());
        }

        if (VagaPatchDTO.getComprimento() != null) {
            vagaExistente.setComprimento(VagaPatchDTO.getComprimento());
        }

        if (VagaPatchDTO.getStatus() != null) {
            vagaExistente.setStatus(VagaPatchDTO.getStatus());
        }
        
        return vagaRepository.save(vagaExistente);
    }
    
    @Transactional()
    public Vaga cadastrarVaga(VagaRequestDTO vagaRequest){
        try {
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
            
            return vagaRepository.save(vaga);

        } catch (Exception e) { 
            throw new RuntimeException("Erro ao cadastrar vaga: " + e.getMessage());
        }
    }
}