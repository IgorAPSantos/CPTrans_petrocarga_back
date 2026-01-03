package com.cptrans.petrocarga.dto;

import java.util.UUID;

import com.cptrans.petrocarga.enums.TipoDenunciaEnum;
import com.cptrans.petrocarga.models.Denuncia;
import com.cptrans.petrocarga.models.Reserva;
import com.cptrans.petrocarga.models.Usuario;
import com.cptrans.petrocarga.models.Vaga;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DenunciaRequestDTO {
    @NotNull
    @NotBlank
    private String descricao;

    @NotNull
    private UUID reservaId;

    @NotNull
    private TipoDenunciaEnum tipo;

    public DenunciaRequestDTO() {
    }
    public DenunciaRequestDTO(String descricao, UUID reservaId, TipoDenunciaEnum tipo) {
        this.descricao = descricao;
        this.reservaId = reservaId;
        this.tipo = tipo;
    }

    public Denuncia toEntity(Usuario criadoPor, Vaga vaga, Reserva reserva) {
        return new Denuncia(descricao, criadoPor, vaga, reserva, tipo);
    }

    public String getDescricao() {
        return descricao;
    }
    public UUID getReservaId() {
        return reservaId;
    }
    public TipoDenunciaEnum getTipo() {
        return tipo;
    }

}
