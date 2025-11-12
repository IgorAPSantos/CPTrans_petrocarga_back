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
import com.cptrans.petrocarga.models.Empresa;
import com.cptrans.petrocarga.models.Motorista;
import com.cptrans.petrocarga.models.Reserva;
import com.cptrans.petrocarga.models.Usuario;
import com.cptrans.petrocarga.models.Vaga;
import com.cptrans.petrocarga.models.Veiculo;
import com.cptrans.petrocarga.repositories.ReservaRepository;
import com.cptrans.petrocarga.security.UserAuthenticated;
import com.cptrans.petrocarga.utils.DateUtils;

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
    private EmpresaService empresaService;
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
    // TODO: Verificar disponibilidade da reserva ao criar
    // TODO: Verificar se há reservaRádipa no mesmo horário
    // TODO: Ver se falta algo e se dá pra melhorar o código
    public Reserva createReserva(UUID vagaId, UUID motoristaId, UUID veiculoId, Reserva novaReserva) {
        Vaga vagaReserva = vagaService.findById(vagaId);
        UserAuthenticated userAuthenticated = (UserAuthenticated) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuarioLogado = usuarioService.findById(userAuthenticated.id());
        Motorista motoristaDaReserva = motoristaService.findById(motoristaId);
        Veiculo veiculoDaReserva = veiculoService.findById(veiculoId);
        List<Reserva> reservasAtivasNaVaga = findByVagaId(vagaId, StatusReservaEnum.ATIVA);

        if(novaReserva.getFim().toInstant().isBefore(novaReserva.getInicio().toInstant())) {
            throw new IllegalArgumentException("Fim da reserva deve ser posterior ao inicio.");
        }

        Integer tempoReservaEmMinutos = (int) (novaReserva.getInicio().until(novaReserva.getFim(), java.time.temporal.ChronoUnit.MINUTES));
        Boolean tempoValido = tempoReservaEmMinutos <= vagaReserva.getArea().getTempoMaximo() * 60;
        if(!tempoValido){
            throw new IllegalArgumentException("Tempo total de reserva inválido.");
        }
        
        for(Reserva reserva : reservasAtivasNaVaga){
            Boolean novaReservaIniciaAntesDeOutras = novaReserva.getInicio().toInstant().isBefore(reserva.getInicio().toInstant()) && novaReserva.getInicio().toInstant().isBefore(reserva.getFim().toInstant());
            Boolean novaReservaFinalizaAntesDeOutras = novaReserva.getFim().toInstant().isBefore(reserva.getInicio().toInstant()) && novaReserva.getFim().toInstant().isBefore(reserva.getFim().toInstant());
            Boolean novaReservaIniciaDepoisDeOutras = novaReserva.getInicio().toInstant().isAfter(reserva.getInicio().toInstant()) && novaReserva.getInicio().toInstant().isAfter(reserva.getFim().toInstant());
            Boolean novaReservaFinalizaDepoisDeOutras = novaReserva.getFim().toInstant().isAfter(reserva.getInicio().toInstant()) && novaReserva.getFim().toInstant().isAfter(reserva.getFim().toInstant());
            if(!(novaReservaIniciaAntesDeOutras && novaReservaFinalizaAntesDeOutras) && !(novaReservaIniciaDepoisDeOutras && novaReservaFinalizaDepoisDeOutras)){
                throw new IllegalArgumentException("O horário da reserva não pode se sobrepor com outras reservas.");
            }
        }
        
        if(usuarioLogado.getPermissao().equals(PermissaoEnum.MOTORISTA) || usuarioLogado.getPermissao().equals(PermissaoEnum.EMPRESA)){
            if(!veiculoDaReserva.getUsuario().getId().equals(usuarioLogado.getId())){
                throw new IllegalArgumentException("Usuário não pode fazer reserva para um veículo de outro usuário.");
            }
        }
        if(usuarioLogado.getPermissao().equals(PermissaoEnum.MOTORISTA)){
            if(!motoristaDaReserva.getUsuario().getId().equals(usuarioLogado.getId())){
                throw new IllegalArgumentException("Usuário não pode fazer reserva para outro motorista.");
            }
        }
        if(usuarioLogado.getPermissao().equals(PermissaoEnum.EMPRESA)){
            Empresa empresa = empresaService.findByUsuarioId(usuarioLogado.getId());
            if(!motoristaDaReserva.getEmpresa().getId().equals(empresa.getId())){
                throw new IllegalArgumentException("A empresa só pode fazer reserva para motoristas associados à ela.");
            }
        }

        novaReserva.setVaga(vagaReserva);
        novaReserva.setMotorista(motoristaDaReserva);
        novaReserva.setVeiculo(veiculoDaReserva);
        novaReserva.setCriadoPor(usuarioLogado);
        return reservaRepository.save(novaReserva);
    }

    public void deleteById(UUID id) {
        reservaRepository.deleteById(id);
    }
}
