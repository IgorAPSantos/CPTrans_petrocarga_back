package com.cptrans.petrocarga.services;

import java.time.LocalDate;
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
 

    public List<ReservaRapida> findByVagaAndStatus(Vaga vaga, StatusReservaEnum status) {
        return reservaRapidaRepository.findByVagaAndStatus(vaga, status);
    }

    public List<ReservaRapida> findAtivasByVagaAndData(Vaga vaga, LocalDate data) {
        List<ReservaRapida> reservasRapidasAtivas = reservaRapidaRepository.findByVagaAndStatus(vaga, StatusReservaEnum.ATIVA);
        if(data!=null && reservasRapidasAtivas!=null && !reservasRapidasAtivas.isEmpty()) {
            return reservasRapidasAtivas.stream()
                .filter(reservaRapida -> DateUtils.toLocalDateInBrazil(reservaRapida.getInicio()).equals(data) || DateUtils.toLocalDateInBrazil(reservaRapida.getFim()).equals(data))
                .toList();
        } else {
            return reservasRapidasAtivas;
        }
    }

    public ReservaRapida create(ReservaRapida novaReservaRapida) {
        Integer quantidadeReservasRapidasPorPlaca = reservaRapidaRepository.countByPlaca(novaReservaRapida.getPlaca());
        Vaga vagaReserva = vagaService.findById(novaReservaRapida.getVaga().getId());
        List<Reserva>  reservasAtivasNaVaga = reservaRepository.findByVagaAndStatus(vagaReserva, StatusReservaEnum.ATIVA);
        List<ReservaRapida> reservasRapidasAtivasNaVaga = findByVagaAndStatus(vagaReserva, StatusReservaEnum.ATIVA);
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
