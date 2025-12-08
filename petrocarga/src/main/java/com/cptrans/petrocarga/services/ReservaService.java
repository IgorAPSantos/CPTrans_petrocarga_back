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

import jakarta.persistence.EntityNotFoundException;

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


    public List<Reserva> findAll(List<StatusReservaEnum> status, UUID vagaId) {
        if(status == null) status = new ArrayList<>();
        if(vagaId != null) {
            return findByVagaId(vagaId, status);
        }
        if(!status.isEmpty()) {
            return findByStatus(status);
        }
        return reservaRepository.findAll();
    }

    public List<Reserva> findAllByData(LocalDate data, List<StatusReservaEnum> status, UUID vagaId) {
        List<Reserva> reservas = findAll(status, vagaId);
        if(reservas.isEmpty()) return reservas;
        if(data != null) {
            return reservas.stream().filter(reserva -> DateUtils.toLocalDateInBrazil(reserva.getInicio()).equals(data) || DateUtils.toLocalDateInBrazil(reserva.getFim()).equals(data)).toList();
        }
        return reservas;
    }

    public List<Reserva> findByStatus(List<StatusReservaEnum> status) {
        return reservaRepository.findByStatusIn(status);
    }

    public List<Reserva> findByVagaId(UUID vagaId, List<StatusReservaEnum> status) {
        Vaga vaga = vagaService.findById(vagaId);
        if(status == null) status = new ArrayList<>();
        if(!status.isEmpty()) {
            return reservaRepository.findByVagaAndStatusIn(vaga, status);
        }
        return reservaRepository.findByVaga(vaga);
    }
    
    public List<Reserva> findByVagaIdAndDataAndStatusIn(UUID vagaId, LocalDate data, List<StatusReservaEnum> status) {
        Vaga vaga = vagaService.findById(vagaId);
        List<Reserva> reservas = reservaRepository.findByVaga(vaga);
        if (status == null) status = new ArrayList<>();
        if(!status.isEmpty()) {
            reservas = reservaRepository.findByVagaAndStatusIn(vaga, status);
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

    public List<Reserva> findByUsuarioId(UUID usuarioId, List<StatusReservaEnum> status) {
        Usuario usuario = usuarioService.findById(usuarioId);
        if(status == null ) status = new ArrayList<>();
        if(!status.isEmpty()) {
            return reservaRepository.findByCriadoPorAndStatusIn(usuario, status);
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
        List<StatusReservaEnum> listaStatus = new ArrayList<>(List.of(StatusReservaEnum.ATIVA, StatusReservaEnum.RESERVADA));
        List<Reserva> reservasAtivasNaVaga = reservaRepository.findByVagaAndStatusIn(vagaReserva, listaStatus);
        List<ReservaRapida> reservasRapidasAtivasNaVaga = reservaRapidaService.findByVagaAndStatusIn(vagaReserva, listaStatus);
        reservaUtils.validarTempoMaximoReserva(novaReserva);
        reservaUtils.validarEspacoDisponivelNaVaga(novaReserva, usuarioLogado, reservasAtivasNaVaga, reservasRapidasAtivasNaVaga);
        reservaUtils.validarPermissoesReserva(usuarioLogado, motoristaDaReserva, veiculoDaReserva);
    }

    public List<ReservaDTO> getReservasByVagaAndData(Vaga vaga, LocalDate data, List<StatusReservaEnum> status) {
        List<Reserva> reservas = findByVagaIdAndDataAndStatusIn(vaga.getId(), data, status);
        List<ReservaRapida> reservasRapidas = reservaRapidaService.findByVagaAndDataAndStatusIn(vaga, data, status);
        List<ReservaDTO> listaFinalReservas = new ArrayList<>();
        
        if(reservasRapidas != null && !reservasRapidas.isEmpty()) {
            reservasRapidas.forEach(rr -> listaFinalReservas.add(new ReservaDTO(rr.getId(), vaga, rr.getInicio(), rr.getFim(), rr.getTipoVeiculo().getComprimento(), rr.getPlaca(), rr.getStatus())));
        }

        if(reservas != null && !reservas.isEmpty()) {
            reservas.forEach(r-> listaFinalReservas.add(new ReservaDTO(r.getId(), vaga, r.getInicio(), r.getFim(), r.getVeiculo().getComprimento(), r.getVeiculo().getPlaca(), r.getStatus())));
        }
        
        return listaFinalReservas;
    }

    public List<ReservaDTO> getAllReservasByData(LocalDate data, List<StatusReservaEnum> status) {
        List<Reserva> reservas = findAllByData(data, status, null);
        List<ReservaRapida> reservasRapidas = reservaRapidaService.findAllByData( data, status);
        List<ReservaDTO> listaFinalReservas = new ArrayList<>();
        
        if(reservasRapidas != null && !reservasRapidas.isEmpty()) {
            reservasRapidas.forEach(rr -> listaFinalReservas.add(new ReservaDTO(rr.getId(), rr.getVaga(), rr.getInicio(), rr.getFim(), rr.getTipoVeiculo().getComprimento(), rr.getPlaca(), rr.getStatus())));
        }

        if(reservas != null && !reservas.isEmpty()) {
            reservas.forEach(r-> listaFinalReservas.add(new ReservaDTO(r.getId(), r.getVaga(), r.getInicio(), r.getFim(), r.getVeiculo().getComprimento(), r.getVeiculo().getPlaca(), r.getStatus())));
        }
        
        return listaFinalReservas;
    }

    public List<ReservaDTO> getReservasByVagaDataAndPlaca(Vaga vaga, LocalDate data, String placa, List<StatusReservaEnum> status) {
        List<ReservaDTO> reservas = getReservasByVagaAndData(vaga, data, status);
        return reservas.stream()
                .filter(r -> r.getPlacaVeiculo().equalsIgnoreCase(placa))
                .toList();
    }

    public List<ReservaDTO> getAllReservasByDataAndPlaca(LocalDate data, String placa, List<StatusReservaEnum> status) {
        List<ReservaDTO> reservas = getAllReservasByData( data, status);
        return reservas.stream()
                .filter(r -> r.getPlacaVeiculo().equalsIgnoreCase(placa))
                .toList();
    }

    public List<ReservaDTO> getReservasAtivasByPlaca(String placa){
        List<Reserva> reservasPorPlaca = reservaRepository.findByVeiculoPlacaIgnoringCaseAndStatusIn(placa, new ArrayList<>(List.of(StatusReservaEnum.ATIVA, StatusReservaEnum.RESERVADA)));
        List<ReservaRapida> reservasRapidasPorPlaca = reservaRapidaService.findByPlaca(placa);
        List<ReservaDTO> listaReservasAtivasPorPlaca = new ArrayList<>();
        if(reservasRapidasPorPlaca != null && !reservasRapidasPorPlaca.isEmpty()) {
            reservasRapidasPorPlaca.forEach(rr -> listaReservasAtivasPorPlaca.add(new ReservaDTO(rr.getId(), rr.getVaga(), rr.getInicio(), rr.getFim(), rr.getTipoVeiculo().getComprimento(), rr.getPlaca(), rr.getStatus())));
        }
        if(reservasPorPlaca != null && !reservasPorPlaca.isEmpty()) {
            reservasPorPlaca.forEach(r-> listaReservasAtivasPorPlaca.add(new ReservaDTO(r.getId(),r.getVaga(), r.getInicio(), r.getFim(), r.getVeiculo().getComprimento(), r.getVeiculo().getPlaca(), r.getStatus())));
        }
        return listaReservasAtivasPorPlaca;
    }

    public List<Intervalo> getIntervalosBloqueados(Vaga vaga, LocalDate data, TipoVeiculoEnum tipoVeiculo  ) {
        int capacidadeTotal = vaga.getComprimento();
        int comprimentoVeiculoDesejado = tipoVeiculo.getComprimento();

        List<ReservaDTO> reservas = getReservasByVagaAndData(vaga, data, new ArrayList<>(List.of(StatusReservaEnum.RESERVADA, StatusReservaEnum.ATIVA)));
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

    /**
     * Finaliza uma reserva de forma forçada por um AGENTE/ADMIN ou pelo job automático.
     * Regras:
     *  - Reserva deve existir e estar ATIVA
     *  - A finalização só é bloqueada se ainda não começou
     *  - Usuário autenticado deve possuir ROLE_AGENTE ou ROLE_ADMIN (garantido por @PreAuthorize no controller)
     * Efeitos:
     *  - Atualiza status para CONCLUIDA
     *  - Não altera o campo "fim" para evitar impacto em relatórios existentes
     */
    public Reserva finalizarForcado(UUID reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId).orElseThrow(() -> new EntityNotFoundException("Reserva não encontrada."));

        if (!StatusReservaEnum.ATIVA.equals(reserva.getStatus())) {
            throw new IllegalStateException("Reserva não está ativa e não pode ser finalizada.");
        }

        OffsetDateTime agora = OffsetDateTime.now();
        // Bloqueia apenas finalizações ANTES do início (não faz sentido finalizar algo que não começou)
        if (agora.isBefore(reserva.getInicio())) {
            throw new IllegalStateException("Não é possível finalizar uma reserva que ainda não começou.");
        }

        reserva.setStatus(StatusReservaEnum.REMOVIDA);
        return reservaRepository.save(reserva);
    }

    /**
     * Realiza o check-in de uma reserva.
     * Regras:
     *  - Reserva deve existir e estar ATIVA
     *  - Não pode já ter feito check-in
     *  - Check-in só é permitido dentro do período da reserva (ou poucos minutos antes)
     */
    public Reserva realizarCheckIn(UUID reservaId) {
        Reserva reserva = findById(reservaId);

        if (!StatusReservaEnum.RESERVADA.equals(reserva.getStatus())) {
            throw new IllegalStateException("Reserva não está ativa.");
        }

        if (Boolean.TRUE.equals(reserva.getCheckedIn())) {
            throw new IllegalStateException("Check-in já foi realizado para esta reserva.");
        }

        OffsetDateTime agora = OffsetDateTime.now();
        // Permite check-in até 5 minutos antes do início
        OffsetDateTime limiteAntes = reserva.getInicio().minusMinutes(5);
        
        if (agora.isBefore(limiteAntes) || agora.isAfter(reserva.getFim())) {
            throw new IllegalStateException("Check-in só pode ser realizado próximo ao horário da reserva.");
        }

        // Validar permissões do usuário
        UserAuthenticated userAuthenticated = (UserAuthenticated) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuarioLogado = usuarioService.findById(userAuthenticated.id());
        List<String> authorities = userAuthenticated.userDetails().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList();

        // MOTORISTA só pode fazer check-in em sua própria reserva
        if (authorities.contains(PermissaoEnum.MOTORISTA.getRole())) {
            Motorista motorista = motoristaService.findByUsuarioId(usuarioLogado.getId());
            if (!reserva.getMotorista().getId().equals(motorista.getId())) {
                throw new IllegalArgumentException("Motorista só pode fazer check-in em suas próprias reservas.");
            }
        }

        // EMPRESA só pode fazer check-in em reservas de seus veículos
        if (authorities.contains(PermissaoEnum.EMPRESA.getRole())) {
            if (!reserva.getCriadoPor().getId().equals(usuarioLogado.getId())) {
                throw new IllegalArgumentException("Empresa só pode fazer check-in em reservas criadas por ela.");
            }
        }

        // AGENTE e ADMIN podem fazer check-in em qualquer reserva (já permitido)

        reserva.setCheckedIn(true);
        reserva.setStatus(StatusReservaEnum.ATIVA);
        reserva.setCheckInEm(agora);
        return reservaRepository.save(reserva);
    }

    /**
     * Processa reservas elegíveis para no-show (sem check-in após grace period).
     * Chamado pelo scheduler automaticamente.
     * @param graceMinutes minutos de tolerância após o início
     * @return quantidade de reservas finalizadas
     */
    public int processarNoShow(int graceMinutes) {
        OffsetDateTime agora = OffsetDateTime.now();

        List<Reserva> candidatas = reservaRepository.findNoShowCandidates(
            StatusReservaEnum.RESERVADA,
            graceMinutes,
            agora
        );

        int finalizadas = 0;
        for (Reserva reserva : candidatas) {
            try {
                finalizarForcado(reserva.getId());
                finalizadas++;
            } catch (Exception e) {
                // Log e continua para não bloquear outras reservas
                System.err.println("Erro ao finalizar reserva " + reserva.getId() + ": " + e.getMessage());
            }
        }

        if (finalizadas > 0) {
            System.out.println("No-show: " + finalizadas + " reserva(s) finalizada(s) automaticamente.");
        }

        return finalizadas;
    }
}
