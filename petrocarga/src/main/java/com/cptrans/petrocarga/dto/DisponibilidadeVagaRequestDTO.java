package com.cptrans.petrocarga.dto;

import com.cptrans.petrocarga.models.DisponibilidadeVaga;
import com.cptrans.petrocarga.models.Usuario;
import com.cptrans.petrocarga.models.Vaga;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;

public class DisponibilidadeVagaRequestDTO {

    @NotNull
    private UUID vagaId;

    @NotNull
    private OffsetDateTime inicio;

    @NotNull
    private OffsetDateTime fim;

    @NotNull
    private UUID criadoPorId;

    public DisponibilidadeVaga toEntity(Vaga vaga, Usuario usuario) {
        DisponibilidadeVaga disponibilidadeVaga = new DisponibilidadeVaga();
        disponibilidadeVaga.setVaga(vaga);
        disponibilidadeVaga.setInicio(this.inicio);
        disponibilidadeVaga.setFim(this.fim);
        disponibilidadeVaga.setCriadoPor(usuario);
        return disponibilidadeVaga;
    }

    // Getters and Setters
    public UUID getVagaId() {
        return vagaId;
    }

    public void setVagaId(UUID vagaId) {
        this.vagaId = vagaId;
    }

    public OffsetDateTime getInicio() {
        return inicio;
    }

    public void setInicio(OffsetDateTime inicio) {
        this.inicio = inicio;
    }

    public OffsetDateTime getFim() {
        return fim;
    }

    public void setFim(OffsetDateTime fim) {
        this.fim = fim;
    }

    public UUID getCriadoPorId() {
        return criadoPorId;
    }

    public void setCriadoPorId(UUID criadoPorId) {
        this.criadoPorId = criadoPorId;
    }
}
