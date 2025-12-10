package com.cptrans.petrocarga.utils;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cptrans.petrocarga.enums.PermissaoEnum;
import com.cptrans.petrocarga.models.Empresa;
import com.cptrans.petrocarga.models.Motorista;
import com.cptrans.petrocarga.models.Reserva;
import com.cptrans.petrocarga.models.ReservaRapida;
import com.cptrans.petrocarga.models.Usuario;
import com.cptrans.petrocarga.models.Vaga;
import com.cptrans.petrocarga.models.Veiculo;
import com.cptrans.petrocarga.services.EmpresaService;

@Component
public class ReservaUtils {
    @Autowired
    private EmpresaService empresaService;

    public static final String METODO_POST = "POST";
    public static final String METODO_PATCH = "PATCH";

    public void validarTempoMaximoReserva(Reserva novaReserva) {
        Vaga vagaReserva = novaReserva.getVaga();
        OffsetDateTime agora = OffsetDateTime.now(DateUtils.FUSO_BRASIL);
        if(novaReserva.getFim().toInstant().isBefore(novaReserva.getInicio().toInstant())) {
            throw new IllegalArgumentException("Fim da reserva deve ser posterior ao inicio.");
        }

        if(novaReserva.getFim().toInstant().isBefore(agora.toInstant())) {
            throw new IllegalArgumentException("Fim da reserva deve ser posterior ao horário atual.");
        }

        Integer tempoReservaEmMinutos = (int) (novaReserva.getInicio().toInstant().until(novaReserva.getFim().toInstant(), java.time.temporal.ChronoUnit.MINUTES));
        Boolean tempoValido = tempoReservaEmMinutos <= (vagaReserva.getArea().getTempoMaximo() * 60) && tempoReservaEmMinutos > 0;
        if(!tempoValido){
            throw new IllegalArgumentException("Tempo total de reserva inválido.");
        }
    }

    public void validarEspacoDisponivelNaVaga(Reserva novaReserva, Usuario usuarioLogado, List<Reserva> reservasAtivasNaVaga, List<ReservaRapida> reservasRapidasAtivasNaVaga, String metodoChamador) {
        Vaga vagaReserva = novaReserva.getVaga();
        Veiculo veiculoDaReserva = novaReserva.getVeiculo();
        Motorista motoristaDaReserva = novaReserva.getMotorista();
        Integer tamanhoDisponivelVaga = vagaReserva.getComprimento() - veiculoDaReserva.getComprimento();
   
        if(!reservasRapidasAtivasNaVaga.isEmpty()){
            for(ReservaRapida reservaRapida : reservasRapidasAtivasNaVaga){
                Boolean reservaSobrepostas = novaReserva.getInicio().toInstant().isBefore(reservaRapida.getFim().toInstant()) && novaReserva.getFim().toInstant().isAfter(reservaRapida.getInicio().toInstant());
                if(reservaRapida.getPlaca().equals(veiculoDaReserva.getPlaca())) {
                    throw new IllegalArgumentException("Veículo de placa " + veiculoDaReserva.getPlaca() + " já possui uma reserva rápida ativa nesta vaga.");
                }
                if(reservaSobrepostas){
                    tamanhoDisponivelVaga -= reservaRapida.getTipoVeiculo().getComprimento();
                    if(tamanhoDisponivelVaga < 0) throw new IllegalArgumentException("Não há espaço suficiente na vaga para a reserva no período solicitado devido a uma reserva rápida existente. Espaço disponível: " + (tamanhoDisponivelVaga + veiculoDaReserva.getComprimento()) + " metros.");
                }
            }
        }
        
        if(!reservasAtivasNaVaga.isEmpty()){
            for(Reserva reserva : reservasAtivasNaVaga){
                Boolean reservaSobrepostas = novaReserva.getInicio().toInstant().isBefore(reserva.getFim().toInstant()) && novaReserva.getFim().toInstant().isAfter(reserva.getInicio().toInstant());
                if (!metodoChamador.equals(METODO_PATCH))validarMotoristaReserva(usuarioLogado, motoristaDaReserva, reserva);
                if(reservaSobrepostas){
                    tamanhoDisponivelVaga -= reserva.getVeiculo().getComprimento();
                    if(tamanhoDisponivelVaga < 0) throw new IllegalArgumentException("Não há espaço suficiente na vaga para a reserva no período solicitado. Espaço disponível: " + (tamanhoDisponivelVaga + veiculoDaReserva.getComprimento()) + " metros.");
                }
            }
        }
    }

    public void validarPermissoesReserva(Usuario usuarioLogado, Motorista motoristaDaReserva, Veiculo veiculoDaReserva) {
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
    }

    public void validarMotoristaReserva(Usuario usuarioLogado, Motorista motoristaNovaReserva, Reserva reservaExistente) {
        if(usuarioLogado.getPermissao().equals(PermissaoEnum.MOTORISTA)){
            if(reservaExistente.getCriadoPor().getId().equals(usuarioLogado.getId()) || reservaExistente.getMotorista().getUsuario().getId().equals(usuarioLogado.getId())){
                throw new IllegalArgumentException("Motorista já possui uma reserva ativa nesta vaga.");
            }
        }
        if(usuarioLogado.getPermissao().equals(PermissaoEnum.EMPRESA)){
            if(reservaExistente.getMotorista().getId().equals(motoristaNovaReserva.getId())){
                throw new IllegalArgumentException("O motorista selecionado já possui uma reserva ativa nesta vaga.");
            } 
        }    
    }

}
