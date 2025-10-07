package com.cptrans.petrocarga.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cptrans.petrocarga.dto.VagaRequestDTO;
import com.cptrans.petrocarga.models.EnderecoVaga;
import com.cptrans.petrocarga.models.OperacaoVaga;
import com.cptrans.petrocarga.models.Vaga;
import com.cptrans.petrocarga.repositories.VagaRepository;

import jakarta.transaction.Transactional;

@Service
public class VagaService {
    @Autowired
    private VagaRepository vagaRepository;
    @Autowired
    private EnderecoVagaService enderecoVagaService;

    public List<Vaga> listarVagas() {
        try {
            return vagaRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar vagas: " + e.getMessage());
        }
    }

    @Transactional()
    public Vaga cadastrarVaga(VagaRequestDTO vagaRequest){
        try {
            // A lógica de cadastrar o endereço permanece idêntica
            EnderecoVaga enderecoVaga = enderecoVagaService.cadastrarEnderecoVaga(vagaRequest.getEndereco());
            
            Vaga vaga = new Vaga();
            
            // A lógica de validação do comprimento permanece idêntica
            if(vagaRequest.getComprimento() == null) {
                throw new IllegalArgumentException("O campo 'comprimento' é obrigatório e não pode ser nulo ou vazio.");
            }
            
            // A verificação de 'localizacao' foi removida, pois o campo não existe mais.
            // Se precisar de uma nova verificação de unicidade, ela deve ser baseada nos novos campos.

            // Atribuição de campos atualizada para o novo modelo
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

            // Lógica para criar e associar as OperacaoVaga (substitui o antigo setDiasSemana)
            if (vagaRequest.getOperacoesVaga() != null && !vagaRequest.getOperacoesVaga().isEmpty()) {
                Set<OperacaoVaga> operacoes = vagaRequest.getOperacoesVaga().stream().map(dto -> {
                    OperacaoVaga op = new OperacaoVaga();
                    op.setDiaSemana(dto.getDiaSemana());
                    op.setHoraInicio(dto.getHoraInicio());
                    op.setHoraFim(dto.getHoraFim());
                    op.setVaga(vaga); // Link bidirecional essencial para o JPA
                    return op;
                }).collect(Collectors.toSet());
                vaga.setOperacoesVaga(operacoes);
            }
            
            return vagaRepository.save(vaga);

        } catch (Exception e) { // Captura de exceção mais genérica para abranger outros erros
            throw new RuntimeException("Erro ao cadastrar vaga: " + e.getMessage());
        }
    }
}