package com.cptrans.petrocarga.services;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.cptrans.petrocarga.enums.PermissaoEnum;
import com.cptrans.petrocarga.enums.StatusReservaEnum;
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
    
    public List<Reserva> findAtivasByVagaIdAndData(UUID vagaId, LocalDate data) {
        Vaga vaga = vagaService.findById(vagaId);
        if(data != null) {
            List<Reserva> reservas = reservaRepository.findByVagaAndStatus(vaga, StatusReservaEnum.ATIVA);
            return reservas.stream().filter(reserva -> DateUtils.toLocalDateInBrazil(reserva.getInicio()).equals(data)).toList();
        }
        return reservaRepository.findByVagaAndStatus(vaga, StatusReservaEnum.ATIVA);
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
}
