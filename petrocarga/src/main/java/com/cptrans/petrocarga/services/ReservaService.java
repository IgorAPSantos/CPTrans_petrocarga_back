package com.cptrans.petrocarga.services;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.cptrans.petrocarga.dto.ReservaDTO;
import com.cptrans.petrocarga.enums.PermissaoEnum;
import com.cptrans.petrocarga.enums.StatusReservaEnum;
import com.cptrans.petrocarga.enums.TipoVeiculoEnum;
import com.cptrans.petrocarga.models.Motorista;
import com.cptrans.petrocarga.models.Reserva;
import com.cptrans.petrocarga.models.ReservaRapida;
import com.cptrans.petrocarga.models.Usuario;
import com.cptrans.petrocarga.models.Vaga;
import com.cptrans.petrocarga.models.Veiculo;
import com.cptrans.petrocarga.repositories.ReservaRepository;
import com.cptrans.petrocarga.security.UserAuthenticated;
import com.cptrans.petrocarga.utils.DateUtils;
import com.cptrans.petrocarga.utils.ReservaUtils;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private MotoristaService motoristaService;
    @Autowired
    private VeiculoService veiculoService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private VagaService vagaService;
    @Autowired
    private ReservaRapidaService reservaRapidaService;
    @Autowired
    private ReservaUtils reservaUtils;
    // @Autowired
    // private DisponibilidadeVagaService disponibilidadeVagaService;


    public List<Reserva> findAll(StatusReservaEnum status, UUID vagaId) {
        if(vagaId != null) {
            return findByVagaId(vagaId, status);
        }
        if(status != null) {
            return findByStatus(status);
        }
        return reservaRepository.findAll();
    }

    // public List<ReservaDTO> getAllReservasByVagaAndFilters(Vaga vaga, StatusReservaEnum status, String ano, String mes, String dia, String placa){
    //     List<ReservaDTO> reservasNaVaga = getReservasByData(vaga, null, status);
    //     Map<String, Object> filtros = new HashMap<>();
        
    //     if(!reservasNaVaga.isEmpty()) {
    //         if(status != null) filtros.put("status", status);
    //         if(ano != null) filtros.put("ano", ano);
    //         if(mes != null) filtros.put("mes", mes);
    //         if(dia != null) filtros.put("dia", dia);
    //         if(placa != null) filtros.put("placa", placa); 
            
    //         List<ReservaDTO> reservasFiltradas = reservasNaVaga.stream().filter(reserva -> {
    //             if(filtros.containsKey(filtros))
    //         })
    //     }

    //     return reservaUtils.filtrarReservasComParametros(reservasFiltradas, ano, mes, dia, placa);
    // }

    public List<Reserva> findByStatus(StatusReservaEnum status) {
        return reservaRepository.findByStatus(status);
    }

    public List<Reserva> findByVagaId(UUID vagaId, StatusReservaEnum status) {
        Vaga vaga = vagaService.findById(vagaId);
        if(status != null) {
            return reservaRepository.findByVagaAndStatus(vaga, status);
        }
        return reservaRepository.findByVaga(vaga);
    }
    
    public List<Reserva> findByVagaIdAndDataAndStatus(UUID vagaId, LocalDate data, StatusReservaEnum status) {
        Vaga vaga = vagaService.findById(vagaId);
        List<Reserva> reservas = reservaRepository.findByVaga(vaga);
        if(status != null) {
            reservas = reservaRepository.findByVagaAndStatus(vaga, status);
        }
        if(data != null) {
            return reservas.stream().filter(reserva -> DateUtils.toLocalDateInBrazil(reserva.getInicio()).equals(data)).toList();
        }
        return reservas;
    }

    public Reserva findById(UUID reservaId) {
        // Substituí o método para usar o novo método com fetch joins
        Reserva reserva = reservaRepository.findByIdWithJoins(reservaId);
        UserAuthenticated usuarioLogado = (UserAuthenticated) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> authorities = usuarioLogado.userDetails().getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        if(authorities.contains(PermissaoEnum.MOTORISTA.getRole())) {
            Motorista motorista = motoristaService.findByUsuarioId(usuarioLogado.id());
            if(!reserva.getMotorista().getId().equals(motorista.getId())) {
                throw new IllegalArgumentException("Usuário não pode ver as reservas de outro usuário.");   
            }
        }
        if(authorities.contains(PermissaoEnum.EMPRESA.getRole())) {
            Veiculo veiculoReserva = veiculoService.findById(reserva.getVeiculo().getId());
            if(!reserva.getCriadoPor().getId().equals(usuarioLogado.id()) || !reserva.getVeiculo().getId().equals(veiculoReserva.getId())) {
                throw new IllegalArgumentException("Usuário não pode ver as reservas de outro usuário.");   
            }
        }
        return reserva;
    }

    public List<Reserva> findByUsuarioId(UUID usuarioId, StatusReservaEnum status) {
        Usuario usuario = usuarioService.findById(usuarioId);
        if(status != null) {
            return reservaRepository.findByCriadoPorAndStatus(usuario, status);
        }
        return reservaRepository.findByCriadoPor(usuario);
    }

    public Reserva createReserva(Reserva novaReserva) {
        UserAuthenticated userAuthenticated = (UserAuthenticated) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuarioLogado = usuarioService.findById(userAuthenticated.id());
        novaReserva.setCriadoPor(usuarioLogado);
        checarExcecoesReserva(novaReserva, novaReserva.getCriadoPor(), novaReserva.getMotorista(), novaReserva.getVeiculo());
  
        return reservaRepository.save(novaReserva);
    }

    public void deleteById(UUID id) {
        reservaRepository.deleteById(id);
    }

    public void checarExcecoesReserva(Reserva novaReserva, Usuario usuarioLogado, Motorista motoristaDaReserva, Veiculo veiculoDaReserva) {
        Vaga vagaReserva = novaReserva.getVaga();
        List<Reserva> reservasAtivasNaVaga = reservaRepository.findByVagaAndStatus(vagaReserva, StatusReservaEnum.ATIVA);
        List<ReservaRapida> reservasRapidasAtivasNaVaga = reservaRapidaService.findByVagaAndStatus(vagaReserva, StatusReservaEnum.ATIVA);
   
        reservaUtils.validarTempoMaximoReserva(novaReserva);
        reservaUtils.validarEspacoDisponivelNaVaga(novaReserva, usuarioLogado, reservasAtivasNaVaga, reservasRapidasAtivasNaVaga);
        reservaUtils.validarPermissoesReserva(usuarioLogado, motoristaDaReserva, veiculoDaReserva);
    }

    public List<ReservaDTO> getReservasByData(Vaga vaga, LocalDate data, StatusReservaEnum status) {
        List<Reserva> reservas = findByVagaIdAndDataAndStatus(vaga.getId(), data, status);
        List<ReservaRapida> reservasRapidas = reservaRapidaService.findByVagaAndDataAndStatus(vaga, data, status);
        List<ReservaDTO> listaFinalReservas = new ArrayList<>();
        
        if(reservasRapidas != null && !reservasRapidas.isEmpty()) {
            reservasRapidas.forEach(rr -> listaFinalReservas.add(new ReservaDTO(vaga, rr.getInicio(), rr.getFim(), rr.getTipoVeiculo().getComprimento(), rr.getPlaca(), rr.getStatus())));
        }

        if(reservas != null && !reservas.isEmpty()) {
            reservas.forEach(r-> listaFinalReservas.add(new ReservaDTO(vaga, r.getInicio(), r.getFim(), r.getVeiculo().getComprimento(), r.getVeiculo().getPlaca(), r.getStatus())));
        }
        
        return listaFinalReservas;
    }

    public List<ReservaDTO> getReservasAtivasByDataAndPlaca(Vaga vaga, LocalDate data, String placa, StatusReservaEnum status) {
        List<ReservaDTO> reservasAtivas = getReservasByData(vaga, data, status);
        return reservasAtivas.stream()
                .filter(r -> r.getPlacaVeiculo().equalsIgnoreCase(placa))
                .toList();
    }

    public List<ReservaDTO> getReservasAtivasByPlaca(String placa){
        List<Reserva> reservasPorPlaca = reservaRepository.findByVeiculoPlacaIgnoringCaseAndStatus(placa, StatusReservaEnum.ATIVA);
        List<ReservaRapida> reservasRapidasPorPlaca = reservaRapidaService.findByPlaca(placa);
        List<ReservaDTO> listaReservasAtivasPorPlaca = new ArrayList<>();
        if(reservasRapidasPorPlaca != null && !reservasRapidasPorPlaca.isEmpty()) {
            reservasRapidasPorPlaca.forEach(rr -> listaReservasAtivasPorPlaca.add(new ReservaDTO(rr.getVaga(), rr.getInicio(), rr.getFim(), rr.getTipoVeiculo().getComprimento(), rr.getPlaca(), rr.getStatus())));
        }
        if(reservasPorPlaca != null && !reservasPorPlaca.isEmpty()) {
            reservasPorPlaca.forEach(r-> listaReservasAtivasPorPlaca.add(new ReservaDTO(r.getVaga(), r.getInicio(), r.getFim(), r.getVeiculo().getComprimento(), r.getVeiculo().getPlaca(), r.getStatus())));
        }
        return listaReservasAtivasPorPlaca;
    }

    public List<Intervalo> getIntervalosBloqueados(Vaga vaga, LocalDate data, TipoVeiculoEnum tipoVeiculo  ) {
        int capacidadeTotal = vaga.getComprimento();
        int comprimentoVeiculoDesejado = tipoVeiculo.getComprimento();

        List<ReservaDTO> reservas = getReservasByData(vaga, data, StatusReservaEnum.ATIVA);
        if (reservas.isEmpty()) {
            return List.of(); // nada reservado → nenhum bloqueio
        }
        // 1) coleta todos os pontos de corte
        TreeSet<Instant> pontos = new TreeSet<>();
        reservas.forEach(r -> {
            pontos.add(r.getInicio().toInstant());
            pontos.add(r.getFim().toInstant());
        });

        List<Instant> timeline = new ArrayList<>(pontos);

        // 2) calcula segmentos e marca os bloqueados
        List<Intervalo> intervalosBloqueados = new ArrayList<>();
        Intervalo atual = null;

        for (int i = 0; i < timeline.size() - 1; i++) {
            Instant inicio = timeline.get(i);
            Instant fim = timeline.get(i + 1);

            if (inicio.equals(fim)) continue;

            int ocupacaoAtual = 0;

            for (ReservaDTO res : reservas) {
                boolean sobrepoe = res.getInicio().toInstant().isBefore(fim)
                        && res.getFim().toInstant().isAfter(inicio);

                if (sobrepoe) {
                    ocupacaoAtual += res.getTamanhoVeiculo();
                }
            }

            int espacoRestante = capacidadeTotal - ocupacaoAtual;
            boolean cabe = espacoRestante >= comprimentoVeiculoDesejado;

            if (!cabe) {
                OffsetDateTime dtoIni = OffsetDateTime.ofInstant(inicio, ZoneOffset.of("-03:00"));
                OffsetDateTime dtoFim = OffsetDateTime.ofInstant(fim, ZoneOffset.of("-03:00"));

                if (atual == null) {
                    atual = new Intervalo(dtoIni, dtoFim);
                } else {
                    atual.setFim(dtoFim);
                }
            } else {
                if (atual != null) {
                    intervalosBloqueados.add(atual);
                    atual = null;
                }
            }
        }

        if (atual != null) intervalosBloqueados.add(atual);

        return intervalosBloqueados;
}

/* Auxiliares */

public static class Intervalo {
    private OffsetDateTime inicio;
    private OffsetDateTime fim;

    public Intervalo(OffsetDateTime inicio, OffsetDateTime fim) {
        this.inicio = inicio;
        this.fim = fim;
    }

    public OffsetDateTime getInicio() { return inicio; }
    public OffsetDateTime getFim() { return fim; }
    public void setInicio(OffsetDateTime inicio) { this.inicio = inicio; }
    public void setFim(OffsetDateTime fim) { this.fim = fim; }
}
}
