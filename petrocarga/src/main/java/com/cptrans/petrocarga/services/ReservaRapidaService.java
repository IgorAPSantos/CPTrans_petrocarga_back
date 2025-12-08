package com.cptrans.petrocarga.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.cptrans.petrocarga.enums.PermissaoEnum;
import com.cptrans.petrocarga.enums.StatusReservaEnum;
import com.cptrans.petrocarga.models.Agente;
import com.cptrans.petrocarga.models.Reserva;
import com.cptrans.petrocarga.models.ReservaRapida;
import com.cptrans.petrocarga.models.Usuario;
import com.cptrans.petrocarga.models.Vaga;
import com.cptrans.petrocarga.repositories.ReservaRapidaRepository;
import com.cptrans.petrocarga.repositories.ReservaRepository;
import com.cptrans.petrocarga.security.UserAuthenticated;
import com.cptrans.petrocarga.utils.DateUtils;
import com.cptrans.petrocarga.utils.ReservaRapidaUtils;

@Service
public class ReservaRapidaService {
    
    @Autowired
    private ReservaRapidaRepository reservaRapidaRepository;
    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private VagaService vagaService;
    @Autowired
    private ReservaRapidaUtils reservaRapidaUtils;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private AgenteService agenteService;
    
    public List<ReservaRapida> findAll(List<StatusReservaEnum> status) {
        if(status == null ) status = new ArrayList<>();
        if(!status.isEmpty()) {
            return reservaRapidaRepository.findByStatusIn(status);
        }
        return reservaRapidaRepository.findAll();
    }

    public List<ReservaRapida> findAllByData(LocalDate data, List<StatusReservaEnum> status) {
        List<ReservaRapida> reservasRapidas = findAll(status);
        if(reservasRapidas.isEmpty()) return reservasRapidas;
        if(data != null) {
            return reservasRapidas.stream()
                .filter(reservaRapida -> DateUtils.toLocalDateInBrazil(reservaRapida.getInicio()).equals(data) || DateUtils.toLocalDateInBrazil(reservaRapida.getFim()).equals(data))
                .toList();
        }
        return reservasRapidas;
        
    }

    public List<ReservaRapida> findByVagaAndStatusIn(Vaga vaga, List<StatusReservaEnum> status) {
        if(status == null ) status = new ArrayList<>();
        if(status.isEmpty()) {
            return reservaRapidaRepository.findByVaga(vaga);
        }
        return reservaRapidaRepository.findByVagaAndStatusIn(vaga, status);
    }

    public List<ReservaRapida> findByVagaAndDataAndStatusIn(Vaga vaga, LocalDate data, List<StatusReservaEnum> status) {
        List<ReservaRapida> reservasRapidas = reservaRapidaRepository.findByVaga(vaga);
        if(status == null ) status = new ArrayList<>();
        if(!status.isEmpty()) {
            reservasRapidas = reservaRapidaRepository.findByVagaAndStatusIn(vaga, status);
        }
        if(data!=null && reservasRapidas!=null && !reservasRapidas.isEmpty()) {
            return reservasRapidas.stream()
                .filter(reservaRapida -> DateUtils.toLocalDateInBrazil(reservaRapida.getInicio()).equals(data) || DateUtils.toLocalDateInBrazil(reservaRapida.getFim()).equals(data))
                .toList();
        } else {
            return reservasRapidas;
        }
    }

    public List<ReservaRapida> findByPlaca(String placa) {  
        return reservaRapidaRepository.findByPlacaIgnoringCaseAndStatus(placa, StatusReservaEnum.ATIVA);
    }

    public List<ReservaRapida> findByAgente(Agente agente) {
        return reservaRapidaRepository.findByAgente(agente);
    }

    public ReservaRapida create(ReservaRapida novaReservaRapida) {
        Integer quantidadeReservasRapidasPorPlaca = reservaRapidaRepository.countByPlaca(novaReservaRapida.getPlaca());
        Vaga vagaReserva = vagaService.findById(novaReservaRapida.getVaga().getId());
        List<StatusReservaEnum> listaStatus = new ArrayList<>(List.of(StatusReservaEnum.ATIVA, StatusReservaEnum.RESERVADA));
        List<Reserva>  reservasAtivasNaVaga = reservaRepository.findByVagaAndStatusIn(vagaReserva, listaStatus);
        List<ReservaRapida> reservasRapidasAtivasNaVaga = findByVagaAndStatusIn(vagaReserva, listaStatus);
        UserAuthenticated userAuthenticated = (UserAuthenticated) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuarioLogado = usuarioService.findById(userAuthenticated.id());
        if(usuarioLogado.getPermissao().equals(PermissaoEnum.AGENTE)){
            Agente agenteLogado = agenteService.findByUsuarioId(usuarioLogado.getId());
            novaReservaRapida.setAgente(agenteLogado);
        }

        reservaRapidaUtils.validarQuantidadeReservasPorPlaca(quantidadeReservasRapidasPorPlaca, novaReservaRapida);
        reservaRapidaUtils.validarTempoMaximoReservaRapida(novaReservaRapida, novaReservaRapida.getVaga());
        reservaRapidaUtils.validarEspacoDisponivelNaVaga(novaReservaRapida, vagaReserva, reservasAtivasNaVaga, reservasRapidasAtivasNaVaga);
        return reservaRapidaRepository.save(novaReservaRapida);

    }
}
