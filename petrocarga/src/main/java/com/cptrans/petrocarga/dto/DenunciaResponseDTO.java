package com.cptrans.petrocarga.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.cptrans.petrocarga.enums.StatusDenunciaEnum;
import com.cptrans.petrocarga.enums.TipoDenunciaEnum;
import com.cptrans.petrocarga.models.Denuncia;

public class DenunciaResponseDTO {

    private UUID id;
    private String descricao;
    private UUID criadoPorId;
    private UUID vagaId;
    private UUID reservaId;
    private EnderecoVagaResponseDTO enderecoVaga;
    private String numeroEndereco;
    private String referenciaEndereco;
    private StatusDenunciaEnum status;
    private TipoDenunciaEnum tipo;
    private String resposta;
    private UUID atualizadoPorId;
    private OffsetDateTime criadoEm;
    private OffsetDateTime atualizadoEm;
    private OffsetDateTime encerradoEm;

    public DenunciaResponseDTO() {}

    public DenunciaResponseDTO(Denuncia denuncia){
        if(denuncia.getAtualizadoPor() != null) this.atualizadoPorId = denuncia.getAtualizadoPor().getId();
        this.id = denuncia.getId();
        this.descricao = denuncia.getDescricao();
        this.criadoPorId = denuncia.getCriadoPor().getId();
        this.vagaId = denuncia.getVaga().getId();
        this.reservaId = denuncia.getReserva().getId();
        this.enderecoVaga = denuncia.getVaga().getEndereco().toResponseDTO();
        this.numeroEndereco = denuncia.getVaga().getNumeroEndereco();
        this.referenciaEndereco = denuncia.getVaga().getReferenciaEndereco();
        this.status = denuncia.getStatus();
        this.tipo = denuncia.getTipo();
        this.resposta = denuncia.getResposta();
        this.criadoEm = denuncia.getCriadoEm();
        this.atualizadoEm = denuncia.getAtualizadoEm();
        this.encerradoEm = denuncia.getEncerradoEm();
    }

    public UUID getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public UUID getCriadoPorId() {
        return criadoPorId;
    }

    public UUID getVagaId() {
        return vagaId;
    }

    public UUID getReservaId() {
        return reservaId;
    }

    public EnderecoVagaResponseDTO getEnderecoVaga() {
        return enderecoVaga;
    }

    public String getNumeroEndereco() {
        return numeroEndereco;
    }

    public String getReferenciaEndereco() {
        return referenciaEndereco;
    }

    public StatusDenunciaEnum getStatus() {
        return status;
    }

    public TipoDenunciaEnum getTipo() {
        return tipo;
    }

    public String getResposta() {
        return resposta;
    }

    public UUID getAtualizadoPorId() {
        return atualizadoPorId;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public OffsetDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public OffsetDateTime getEncerradoEm() {
        return encerradoEm;
    }

}
