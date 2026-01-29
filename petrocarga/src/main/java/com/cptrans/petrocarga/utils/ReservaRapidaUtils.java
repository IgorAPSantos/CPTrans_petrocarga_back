package com.cptrans.petrocarga.utils;

import java.util.List;

import org.springframework.stereotype.Component;

import com.cptrans.petrocarga.models.Reserva;
import com.cptrans.petrocarga.models.ReservaRapida;
import com.cptrans.petrocarga.models.Vaga;

@Component
public class ReservaRapidaUtils {
    public static final Integer  LIMITE_DE_RESERVAS_POR_PLACA = 3;

    public static void validarQuantidadeReservasPorPlaca(Integer quantidadeReservasRapidasPorPlaca, ReservaRapida novaReservaRapida) {
        if(quantidadeReservasRapidasPorPlaca >= LIMITE_DE_RESERVAS_POR_PLACA ){
            throw new IllegalArgumentException("Veículo com placa " + novaReservaRapida.getPlaca() + " já atingiu o limite de reservas rápidas (" + LIMITE_DE_RESERVAS_POR_PLACA + ").");
        }
    }

    public void validarEspacoDisponivelNaVaga(ReservaRapida novaReservaRapida, Vaga vagaReserva, List<Reserva> reservasAtivasNaVaga, List<ReservaRapida> reservasRapidasAtivasNaVaga) {
        Integer tamanhoDisponivelVaga = vagaReserva.getComprimento() - novaReservaRapida.getTipoVeiculo().getComprimento();
   
        if(!reservasRapidasAtivasNaVaga.isEmpty()){
            for(ReservaRapida reservaRapida : reservasRapidasAtivasNaVaga){
                System.out.println(reservaRapida.getVaga().getId());
                System.out.println(reservaRapida.getStatus());
                System.out.println(reservaRapida.getPlaca());
                System.out.println(reservaRapida.getInicio().atZoneSameInstant(DateUtils.FUSO_BRASIL) + " - " + reservaRapida.getFim().atZoneSameInstant(DateUtils.FUSO_BRASIL));
                Boolean reservaSobrepostas = novaReservaRapida.getInicio().toInstant().isBefore(reservaRapida.getFim().toInstant()) && novaReservaRapida.getFim().toInstant().isAfter(reservaRapida.getInicio().toInstant());
                validarReservaRapidaAtivaPorPlaca(reservaRapida.getPlaca(), novaReservaRapida.getPlaca());
                if(reservaSobrepostas){
                    tamanhoDisponivelVaga -= reservaRapida.getTipoVeiculo().getComprimento();
                    if(tamanhoDisponivelVaga < 0) throw new IllegalArgumentException("Não há espaço suficiente na vaga para a reserva no período solicitado devido a uma reserva rápida existente. Espaço disponível: " + (tamanhoDisponivelVaga + novaReservaRapida.getTipoVeiculo().getComprimento()) + " metros.");
                }
            }
        }
        if(!reservasAtivasNaVaga.isEmpty()){
            for(Reserva reserva : reservasAtivasNaVaga){
                Boolean reservaSobrepostas = novaReservaRapida.getInicio().toInstant().isBefore(reserva.getFim().toInstant()) && novaReservaRapida.getFim().toInstant().isAfter(reserva.getInicio().toInstant());
    
                if(reserva.getVeiculo().getPlaca().equals(novaReservaRapida.getPlaca())) {
                    throw new IllegalArgumentException("Veículo com placa " + novaReservaRapida.getPlaca() + " já possui uma reserva ativa nesta vaga.");
                }
                if(reservaSobrepostas){
                    tamanhoDisponivelVaga -= reserva.getVeiculo().getComprimento();
                    if(tamanhoDisponivelVaga < 0) throw new IllegalArgumentException("Não há espaço suficiente na vaga para a reserva no período solicitado. Espaço disponível: " + (tamanhoDisponivelVaga + novaReservaRapida.getTipoVeiculo().getComprimento()) + " metros.");
                }
            }

        }
        
    }

    public void validarReservaRapidaAtivaPorPlaca(String PlacaReservaAtiva, String PlacaNovaReserva) {
          if(PlacaReservaAtiva.equals(PlacaNovaReserva)) {
                throw new IllegalArgumentException("Veículo com placa " + PlacaNovaReserva + " já possui uma reserva ativa nesta vaga.");
            }
    }

    public Integer getLIMITE_DE_RESERVAS_POR_PLACA() {
        return LIMITE_DE_RESERVAS_POR_PLACA;
    }
}