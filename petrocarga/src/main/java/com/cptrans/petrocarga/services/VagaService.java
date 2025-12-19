package com.cptrans.petrocarga.services;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cptrans.petrocarga.enums.DiaSemanaEnum;
import com.cptrans.petrocarga.enums.StatusVagaEnum;
import com.cptrans.petrocarga.models.EnderecoVaga;
import com.cptrans.petrocarga.models.OperacaoVaga;
import com.cptrans.petrocarga.models.Vaga;
import com.cptrans.petrocarga.repositories.VagaRepository;
import com.cptrans.petrocarga.utils.DateUtils;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.transaction.annotation.Propagation; 

@Service
public class VagaService {
    @Autowired
    private VagaRepository vagaRepository;
    @Autowired
    private EnderecoVagaService enderecoVagaService;

    @Autowired
    private com.cptrans.petrocarga.repositories.ReservaRepository reservaRepository;

    @Autowired
    private com.cptrans.petrocarga.repositories.ReservaRapidaRepository reservaRapidaRepository;


    public List<Vaga> findAll() {
        return vagaRepository.findAll();
    }

    public List<Vaga> findAllByStatus(StatusVagaEnum status) {
        return vagaRepository.findByStatus(status);
    }
    
    public List<Vaga> findAllPaginadas(Integer numeroPagina, Integer tamanhoPagina, String ordenarPor, StatusVagaEnum status, String logradouro) {
        Pageable pageable = PageRequest.of(numeroPagina, tamanhoPagina, Sort.by(ordenarPor).ascending());

        Page<Vaga> vagasPage;
        boolean hasLogradouro = StringUtils.hasText(logradouro); 

        if (status != null && hasLogradouro) {
            vagasPage = vagaRepository.findByStatusAndEnderecoLogradouroContainingIgnoreCase(status, logradouro, pageable);
        } else if (status != null) {
            vagasPage = vagaRepository.findByStatus(status, pageable);
        } else if (hasLogradouro) {
            vagasPage = vagaRepository.findByEnderecoLogradouroContainingIgnoreCase(logradouro, pageable);
        } else {
            vagasPage = vagaRepository.findAll(pageable);
        }

        return vagasPage.getContent();
    }

    public Vaga findById(UUID id) {
        return vagaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Vaga com ID " + id + " não encontrada."));
    }
    
    public void deleteById(UUID id) {
        Vaga vaga = vagaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Vaga com ID " + id + " não encontrada."));
        
        vagaRepository.deleteById(vaga.getId());
    }

    private static final Logger logger = LoggerFactory.getLogger(VagaService.class);

    /**
     * Sincroniza o status físico das vagas de acordo com reservas ativas no momento.
     * Regras:
     *  - Não altera vagas com status MANUTENCAO
     *  - Se existir reserva ATIVA|RESERVADA no instante atual, marca INDISPONIVEL
     *    se estava DISPONIVEL. Caso contrário, se não existir reserva e a vaga
     *    estiver INDISPONIVEL, marca DISPONIVEL.
     *  - Erros ao processar uma vaga não impedem o processamento das demais.
     */
    public void sincronizarStatusVagas() {
        List<Vaga> vagas = findAll();
        int atualizadas = 0;
        java.time.OffsetDateTime agora = java.time.OffsetDateTime.now(DateUtils.FUSO_BRASIL);

        for (Vaga vaga : vagas) {
            try {
                if (vaga.getStatus() == StatusVagaEnum.MANUTENCAO) continue; // não altera vagas em manutenção

                boolean atualizou = sincronizarStatusVaga(vaga, agora);
                if (atualizou) atualizadas++;
            } catch (Exception e) {
                logger.error("Erro ao sincronizar vaga {}: {}", vaga.getId(), e.getMessage(), e);
            }
        }

        if (atualizadas > 0) {
            logger.info("Sincronização de status de vagas: {} vaga(s) atualizada(s).", atualizadas);
        }
    }

    @org.springframework.transaction.annotation.Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean sincronizarStatusVaga(Vaga vaga, java.time.OffsetDateTime agora) {
        if (vaga.getStatus() == StatusVagaEnum.MANUTENCAO) return false;

        boolean ocupadoReserva = reservaRepository.existsByVagaIdAndHorarioOcupado(vaga.getId(), agora);
        boolean ocupadoReservaRapida = reservaRapidaRepository.existsByVagaIdAndHorarioOcupado(vaga.getId(), agora);
        boolean ocupado = ocupadoReserva || ocupadoReservaRapida;

        if (ocupado) {
            if (vaga.getStatus() == StatusVagaEnum.DISPONIVEL) {
                vaga.setStatus(StatusVagaEnum.INDISPONIVEL);
                vagaRepository.save(vaga);
                return true;
            }
        } else {
            if (vaga.getStatus() == StatusVagaEnum.INDISPONIVEL) {
                vaga.setStatus(StatusVagaEnum.DISPONIVEL);
                vagaRepository.save(vaga);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public Vaga updateById(UUID id, Vaga novaVaga) {
        Vaga vagaExistente = vagaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Vaga com ID " + id + " não encontrada."));

        if(novaVaga.getEndereco() != null){
            EnderecoVaga novoEndereco = enderecoVagaService.cadastrarEnderecoVaga(novaVaga.getEndereco());
            vagaExistente.setEndereco(novoEndereco);
        } 
        
        if(novaVaga.getArea() != null) vagaExistente.setArea(novaVaga.getArea());
        if(novaVaga.getNumeroEndereco() != null) vagaExistente.setNumeroEndereco(novaVaga.getNumeroEndereco());
        if(novaVaga.getReferenciaEndereco() != null) vagaExistente.setReferenciaEndereco(novaVaga.getReferenciaEndereco());
        if(novaVaga.getTipoVaga() != null) vagaExistente.setTipoVaga(novaVaga.getTipoVaga());
        if(novaVaga.getReferenciaGeoInicio() != null) vagaExistente.setReferenciaGeoInicio(novaVaga.getReferenciaGeoInicio());
        if(novaVaga.getReferenciaGeoFim() != null) vagaExistente.setReferenciaGeoFim(novaVaga.getReferenciaGeoFim());
        if(novaVaga.getComprimento() != null) vagaExistente.setComprimento(novaVaga.getComprimento());
        if(novaVaga.getStatus() != null) vagaExistente.setStatus(novaVaga.getStatus());
        if (novaVaga.getOperacoesVaga() != null) {
            Map<DiaSemanaEnum, OperacaoVaga> mapaExistentes = vagaExistente.getOperacoesVaga()
                .stream()
                .collect(Collectors.toMap(OperacaoVaga::getDiaSemana, o -> o));

            Map<DiaSemanaEnum, OperacaoVaga> mapaNovas = novaVaga.getOperacoesVaga()
                .stream()
                .collect(Collectors.toMap(OperacaoVaga::getDiaSemana, o -> o, (o1, o2) -> o1)); // caso venha duplicado, mantém o primeiro

            for (OperacaoVaga novaOperacao : mapaNovas.values()) {
                OperacaoVaga existente = mapaExistentes.get(novaOperacao.getDiaSemana());
                if (existente != null) {
                    existente.setHoraInicio(novaOperacao.getHoraInicio());
                    existente.setHoraFim(novaOperacao.getHoraFim());
                } else {
                    novaOperacao.setVaga(vagaExistente);
                    vagaExistente.getOperacoesVaga().add(novaOperacao);
                }
            }
            vagaExistente.getOperacoesVaga().removeIf(
                operacao -> !mapaNovas.containsKey(operacao.getDiaSemana())
            );
        }
        return vagaRepository.save(vagaExistente);
    }
    
    @Transactional()
    public Vaga createVaga(Vaga novaVaga){
        EnderecoVaga enderecoVaga = enderecoVagaService.cadastrarEnderecoVaga(novaVaga.getEndereco());
        novaVaga.setEndereco(enderecoVaga);
        
        if(novaVaga.getComprimento() == null) {
            throw new IllegalArgumentException("O campo 'comprimento' é obrigatório e não pode ser nulo ou vazio.");
        }
        
        return vagaRepository.save(novaVaga);
    }
}