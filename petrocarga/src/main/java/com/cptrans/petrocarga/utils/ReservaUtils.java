package com.cptrans.petrocarga.utils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cptrans.petrocarga.dto.ReservaDTO;
import com.cptrans.petrocarga.enums.PermissaoEnum;
import com.cptrans.petrocarga.enums.StatusReservaEnum;
import com.cptrans.petrocarga.models.Empresa;
import com.cptrans.petrocarga.models.Motorista;
import com.cptrans.petrocarga.models.Reserva;
import com.cptrans.petrocarga.models.ReservaRapida;
import com.cptrans.petrocarga.models.Usuario;
import com.cptrans.petrocarga.models.Vaga;
import com.cptrans.petrocarga.models.Veiculo;
import com.cptrans.petrocarga.repositories.ReservaRapidaRepository;
import com.cptrans.petrocarga.repositories.ReservaRepository;
import com.cptrans.petrocarga.services.EmpresaService;

@Component
public class ReservaUtils {
    @Autowired
    private EmpresaService empresaService;
    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private ReservaRapidaRepository reservaRapidaRepository;

    public static final String METODO_POST = "POST";
    public static final String METODO_PATCH = "PATCH";
    public static final Integer LIMITE_DE_RESERVAS_POR_PLACA = 3;

    public static void validarTempoMaximoReserva(ReservaDTO novaReserva, Vaga vagaNovaReserva) {
        OffsetDateTime agora = OffsetDateTime.now(DateUtils.FUSO_BRASIL);
        if(novaReserva.getFim().toInstant().isBefore(novaReserva.getInicio().toInstant())) {
            throw new IllegalArgumentException("Horário de Fim da reserva deve ser posterior ao horário de início.");
        }

        if(novaReserva.getInicio().toInstant().isBefore(agora.toInstant()) || novaReserva.getFim().toInstant().isBefore(agora.toInstant())) {
            throw new IllegalArgumentException("Horário da reserva deve ser posterior ao horário atual.");
        }

        Integer tempoReservaEmMinutos = (int) (novaReserva.getInicio().toInstant().until(novaReserva.getFim().toInstant(), java.time.temporal.ChronoUnit.MINUTES));
        Boolean tempoValido = tempoReservaEmMinutos <= (vagaNovaReserva.getArea().getTempoMaximo() * 60) && tempoReservaEmMinutos > 0;
        if(!tempoValido){
            throw new IllegalArgumentException("Tempo total de reserva inválido.");
        }
    }

    public void validarEspacoDisponivelNaVaga(Reserva novaReserva, Usuario usuarioLogado, List<Reserva> reservasAtivasNaVaga, List<ReservaRapida> reservasRapidasAtivasNaVaga, String metodoChamador) {
        Vaga vagaReserva = novaReserva.getVaga();
        Veiculo veiculoDaReserva = novaReserva.getVeiculo();
        Motorista motoristaDaReserva = novaReserva.getMotorista();
        Integer tamanhoDisponivelVaga = vagaReserva.getComprimento() - veiculoDaReserva.getComprimento();
        List<ReservaDTO> reservasVaga = juntarReservas(reservasAtivasNaVaga, reservasRapidasAtivasNaVaga);
        ReservaDTO novaReservaDTO = novaReserva.toReservaDTO();
        
        validarLimiteReservasPorPlaca(novaReservaDTO, metodoChamador);

        if(!reservasVaga.isEmpty()){
            for(ReservaDTO reserva : reservasVaga){ 
                Boolean reservaSobrepostas = novaReserva.getInicio().toInstant().isBefore(reserva.getFim().toInstant()) && novaReserva.getFim().toInstant().isAfter(reserva.getInicio().toInstant());
                if(reservaSobrepostas){
                    validarMotoristaReserva(usuarioLogado, motoristaDaReserva, reserva, metodoChamador);
                    tamanhoDisponivelVaga -= reserva.getTamanhoVeiculo();
                    if(tamanhoDisponivelVaga < 0) throw new IllegalArgumentException("Não há espaço suficiente na vaga para a reserva no período solicitado devido a uma reserva existente. Espaço disponível: " + (tamanhoDisponivelVaga + veiculoDaReserva.getComprimento()) + " metros.");
                }
            }

        }
    }

    public void validarLimiteReservasPorPlaca (ReservaDTO novaReserva, String metodoChamador){
        Integer quantidadeReservasPorPlaca = reservaRepository.countByVeiculoPlacaIgnoringCaseAndStatusIn(novaReserva.getPlacaVeiculo(), List.of(StatusReservaEnum.ATIVA, StatusReservaEnum.RESERVADA));
        List<Reserva> reservasNormaisSobrepostas = reservaRepository.findByFimGreaterThanAndInicioLessThan(novaReserva.getInicio(), novaReserva.getFim());
        List<ReservaRapida> reservasRapidasSobrepostas = reservaRapidaRepository.findByFimGreaterThanAndInicioLessThan(novaReserva.getInicio(), novaReserva.getFim());
        List<ReservaDTO> reservasSobrepostas = juntarReservas(reservasNormaisSobrepostas, reservasRapidasSobrepostas);
        if (quantidadeReservasPorPlaca.equals(LIMITE_DE_RESERVAS_POR_PLACA)) throw new IllegalArgumentException("Veículo de placa " + novaReserva.getPlacaVeiculo() + " ja atingiu o limite de " + LIMITE_DE_RESERVAS_POR_PLACA + " reservas ativas/reservadas.");
        if(reservasSobrepostas != null && !reservasSobrepostas.isEmpty()  ){
            for(ReservaDTO reserva : reservasSobrepostas){
                if(reserva.getStatus().equals(StatusReservaEnum.ATIVA) || reserva.getStatus().equals(StatusReservaEnum.RESERVADA)) {
                    if( reserva.getPlacaVeiculo().equals(novaReserva.getPlacaVeiculo()) && !metodoChamador.equals(METODO_PATCH)){
                        throw new IllegalArgumentException("Veículo de placa " + novaReserva.getPlacaVeiculo() + " ja possui uma reserva com status: " + reserva.getStatus() + " na vaga de id: " + reserva.getVagaId() + " com inicio: " + reserva.getInicio().atZoneSameInstant(DateUtils.FUSO_BRASIL) + " e fim: " + reserva.getFim().atZoneSameInstant(DateUtils.FUSO_BRASIL) + ".");
                    }
                }
            } 
        }
    }

    public void validarPermissoesReserva(Usuario usuarioLogado, Motorista motoristaDaReserva, Veiculo veiculoDaReserva) {
        if (usuarioLogado.getPermissao().equals(PermissaoEnum.MOTORISTA) || usuarioLogado.getPermissao().equals(PermissaoEnum.EMPRESA)){
            if(!veiculoDaReserva.getUsuario().getId().equals(usuarioLogado.getId())){
                throw new IllegalArgumentException("Usuário não pode fazer reserva para um veículo de outro usuário.");
            }
        }

        if (usuarioLogado.getPermissao().equals(PermissaoEnum.MOTORISTA)){
            if(!motoristaDaReserva.getUsuario().getId().equals(usuarioLogado.getId())){
                throw new IllegalArgumentException("Usuário não pode fazer reserva para outro motorista.");
            }
        }
        
        if (usuarioLogado.getPermissao().equals(PermissaoEnum.EMPRESA)){
            Empresa empresa = empresaService.findByUsuarioId(usuarioLogado.getId());
            if(!motoristaDaReserva.getEmpresa().getId().equals(empresa.getId())){
                throw new IllegalArgumentException("A empresa só pode fazer reserva para motoristas associados à ela.");
            }
        }
    }

    private static void validarMotoristaReserva(Usuario usuarioLogado, Motorista motoristaNovaReserva, ReservaDTO reservaExistente, String metodoChamador) {
        if(usuarioLogado.getPermissao().equals(PermissaoEnum.MOTORISTA)){
            if((reservaExistente.getMotoristaId()!= null && reservaExistente.getCriadoPor() != null) && (reservaExistente.getCriadoPor().getId().equals(usuarioLogado.getId()) || reservaExistente.getMotoristaId().equals(usuarioLogado.getId()))){
                if ((reservaExistente.getStatus().equals(StatusReservaEnum.ATIVA) || reservaExistente.getStatus().equals(StatusReservaEnum.RESERVADA)) && !metodoChamador.equals(METODO_PATCH) ){
                    throw new IllegalArgumentException("Motorista já possui uma reserva ativa nesta vaga.");
                }
            }
        }
        if(usuarioLogado.getPermissao().equals(PermissaoEnum.EMPRESA)){
            if(reservaExistente.getMotoristaId().equals(motoristaNovaReserva.getId()) && (reservaExistente.getStatus().equals(StatusReservaEnum.ATIVA) || reservaExistente.getStatus().equals(StatusReservaEnum.RESERVADA))){
                throw new IllegalArgumentException("O motorista selecionado já possui uma reserva ativa nesta vaga.");
            } 
        }    
    }

    public static List<ReservaDTO> juntarReservas(List<Reserva> reservas, List<ReservaRapida> reservasRapidas) {
        List<ReservaDTO> listaFinalReservas = new ArrayList<>(); 

        if(reservasRapidas != null && !reservasRapidas.isEmpty()) {
                reservasRapidas.forEach(rr -> listaFinalReservas.add(new ReservaDTO(rr.getId(), rr.getVaga().getId(), rr.getVaga().getNumeroEndereco(), rr.getVaga().getReferenciaEndereco(), rr.getVaga().getEndereco().toResponseDTO(), rr.getInicio(), rr.getFim(), rr.getTipoVeiculo().getComprimento(), rr.getPlaca(), rr.getStatus(), rr.getAgente().getUsuario(), rr.getCriadoEm())));
        }
    
        if(reservas != null && !reservas.isEmpty()) {
            reservas.forEach(r-> listaFinalReservas.add(new ReservaDTO(r.getId(), r.getCidadeOrigem(), r.getEntradaCidade(), r.isCheckedIn(), r.getCheckInEm(), r.getCheckOutEm(), r.getVaga(), r.getInicio(), r.getFim(), r.getVeiculo(), r.getStatus(), r.getCriadoPor(), r.getCriadoEm(), r.getMotorista())));
        }
    
        return listaFinalReservas;
    }

    public Boolean existsByUsuarioId(UUID usuarioId) {
        return reservaRepository.existsByCriadoPorIdOrMotoristaUsuarioId(usuarioId);
    }
}