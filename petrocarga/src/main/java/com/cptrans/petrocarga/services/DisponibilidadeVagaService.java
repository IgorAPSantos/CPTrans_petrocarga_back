package com.cptrans.petrocarga.services;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.cptrans.petrocarga.dto.DisponibilidadeVagaRequestDTO;
import com.cptrans.petrocarga.models.DisponibilidadeVaga;
import com.cptrans.petrocarga.models.Usuario;
import com.cptrans.petrocarga.models.Vaga;
import com.cptrans.petrocarga.repositories.DisponibilidadeVagaRepository;
import com.cptrans.petrocarga.security.UserAuthenticated;
import com.cptrans.petrocarga.utils.DateUtils;

import jakarta.persistence.EntityNotFoundException;

@Service
public class DisponibilidadeVagaService {

    @Autowired
    private DisponibilidadeVagaRepository disponibilidadeVagaRepository;

    @Autowired
    private VagaService vagaService;

    @Autowired
    private UsuarioService usuarioService;

    public DisponibilidadeVaga save (DisponibilidadeVaga disponibilidadeVaga) {
        return disponibilidadeVagaRepository.save(disponibilidadeVaga); 
    }

    public List<DisponibilidadeVaga> saveAll (List<DisponibilidadeVaga> disponibilidadeVaga) {
        return disponibilidadeVagaRepository.saveAll(disponibilidadeVaga); 
    }

    public List<DisponibilidadeVaga> findAll() {
        return disponibilidadeVagaRepository.findAll();
    }

    public DisponibilidadeVaga findById(UUID id) {
        return disponibilidadeVagaRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("DisponibilidadeVaga não encontrada."));
    }

    public List<DisponibilidadeVaga> findByVagaId(UUID vagaId) {
        return disponibilidadeVagaRepository.findByVagaId(vagaId);
    }

    public DisponibilidadeVaga createDisponibilidadeVaga(DisponibilidadeVaga novaDisponibilidadeVaga, UUID vagaId) {
        UserAuthenticated usuarioLogado = (UserAuthenticated) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioService.findById(usuarioLogado.id());
        Vaga vaga = vagaService.findById(vagaId);
        if(!disponibilidadeValida(novaDisponibilidadeVaga, vaga)) throw new IllegalArgumentException("Informações inválidas.");
        novaDisponibilidadeVaga.setVaga(vaga);
        novaDisponibilidadeVaga.setCriadoPor(usuario);

        return disponibilidadeVagaRepository.save(novaDisponibilidadeVaga);
    }

    public List<DisponibilidadeVaga> createMultipleDisponibilidadeVagas(DisponibilidadeVaga novaDisponibilidadeVaga, List<UUID> listaVagaId) {
        UserAuthenticated userAuthenticated = (UserAuthenticated) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuarioLogado = usuarioService.findById(userAuthenticated.id());
        List<Vaga> listaVagas = new ArrayList<>();
        List<DisponibilidadeVaga> disponibilidadesCriadas = new ArrayList<>();

        if(listaVagaId.isEmpty()) throw new IllegalArgumentException("A lista de vagas não pode estar vazia.");
        
        listaVagaId.forEach(id -> {
            Vaga vaga = vagaService.findById(id);
            listaVagas.add(vaga);
        });

        for(Vaga vaga : listaVagas) {
            if(disponibilidadeValida(novaDisponibilidadeVaga, vaga)) {
                DisponibilidadeVaga disponibilidadeVaga = new DisponibilidadeVaga();
                disponibilidadeVaga.setInicio(novaDisponibilidadeVaga.getInicio());
                disponibilidadeVaga.setFim(novaDisponibilidadeVaga.getFim());
                disponibilidadeVaga.setVaga(vaga);
                disponibilidadeVaga.setCriadoPor(usuarioLogado);
                disponibilidadesCriadas.add(disponibilidadeVaga);
            } 
        }
        if(disponibilidadesCriadas.isEmpty()) throw new IllegalArgumentException("Nenhuma disponibilidade foi criada. Verifique os dados informados.");
        return disponibilidadeVagaRepository.saveAll(disponibilidadesCriadas);
    }

    public DisponibilidadeVaga updateDisponibilidadeVaga(UUID disponibilidadeId, DisponibilidadeVagaRequestDTO novaDisponibilidadeVaga) {
        UserAuthenticated userAuthenticated = (UserAuthenticated) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuarioLogado = usuarioService.findById(userAuthenticated.id());
        DisponibilidadeVaga disponibilidadeCadastrada = findById(disponibilidadeId);

        if (novaDisponibilidadeVaga.getVagaId() != null) {
            Vaga vaga = vagaService.findById(novaDisponibilidadeVaga.getVagaId());
            if(!vaga.equals(disponibilidadeCadastrada.getVaga())) disponibilidadeCadastrada.setVaga(vaga);
        } 
        
        if (!usuarioLogado.equals(disponibilidadeCadastrada.getCriadoPor()))disponibilidadeCadastrada.setCriadoPor(usuarioLogado);
        
        if (novaDisponibilidadeVaga.getInicio() != null) disponibilidadeCadastrada.setInicio(novaDisponibilidadeVaga.getInicio());
        
        if (novaDisponibilidadeVaga.getFim() != null) disponibilidadeCadastrada.setFim(novaDisponibilidadeVaga.getFim());
        
        if(!disponibilidadeValida(disponibilidadeCadastrada, disponibilidadeCadastrada.getVaga())) throw new IllegalArgumentException("Informações inválidas.");
        
        return disponibilidadeCadastrada;
    }

    public List<DisponibilidadeVaga> updateDisponibilidadeVagaByCodigoPmp(DisponibilidadeVagaRequestDTO novaDisponibilidadeVaga, String codigoPmp) {
        List<DisponibilidadeVaga> disponibilidadeVagas = disponibilidadeVagaRepository.findByVagaEnderecoCodigoPmp(codigoPmp);
        List<DisponibilidadeVaga> disponibilidadesAtualizadas = new ArrayList<>();
        for (DisponibilidadeVaga disponibilidadeVaga : disponibilidadeVagas) {
            updateDisponibilidadeVaga(disponibilidadeVaga.getId(), novaDisponibilidadeVaga);
            disponibilidadesAtualizadas.add(disponibilidadeVaga);
        }
        return disponibilidadeVagaRepository.saveAll(disponibilidadesAtualizadas);
    }

    public void deleteById(UUID id) {
        DisponibilidadeVaga disponibilidadeVaga = findById(id);
        disponibilidadeVagaRepository.deleteById(disponibilidadeVaga.getId());
    }
    public void deleteByIdList(List<UUID> listaIds) {
        disponibilidadeVagaRepository.deleteAllById(listaIds);
    }

    public void deleteByCodigoPMP(String codigoPMP) {
        List<DisponibilidadeVaga> disponibilidadeVagas = disponibilidadeVagaRepository.findByVagaEnderecoCodigoPmp(codigoPMP);
        disponibilidadeVagaRepository.deleteAll(disponibilidadeVagas);
    }

    public Boolean disponibilidadeValida(DisponibilidadeVaga novaDisponibilidadeVaga, Vaga vaga) {
        OffsetDateTime agora = OffsetDateTime.now(DateUtils.FUSO_BRASIL);
        if(novaDisponibilidadeVaga.getFim().isBefore(novaDisponibilidadeVaga.getInicio())) {
            throw new IllegalArgumentException("A data de fim deve ser depois da data de inicio.");
        }
        if(novaDisponibilidadeVaga.getFim().equals(novaDisponibilidadeVaga.getInicio())) {
            throw new IllegalArgumentException("A data início e fim devem ser diferentes.");
        }
        if(novaDisponibilidadeVaga.getFim().toInstant().isBefore(agora.toInstant())) {
            throw new IllegalArgumentException("A data de fim deve ser posterior ao horário atual.");
        }
        List<DisponibilidadeVaga> disponibilidadeVagas = findByVagaId(vaga.getId());
        for (DisponibilidadeVaga disponibilidade : disponibilidadeVagas) {
            if(novaDisponibilidadeVaga.getInicio().toInstant().equals(disponibilidade.getInicio().toInstant()) && novaDisponibilidadeVaga.getFim().toInstant().equals(disponibilidade.getFim().toInstant()) && !disponibilidade.getId().equals(novaDisponibilidadeVaga.getId())) {
                System.out.println("inicio: " + novaDisponibilidadeVaga.getInicio());
                System.out.println("fim: " + novaDisponibilidadeVaga.getFim());
                System.out.println("inicio: " + disponibilidade.getInicio());
                System.out.println("fim: " + disponibilidade.getFim());
                throw new IllegalArgumentException("Já existe uma disponibilidade para a vaga de id: " + vaga.getId() + " nesse horario.");
            }
        }
        return true;
    }
}
