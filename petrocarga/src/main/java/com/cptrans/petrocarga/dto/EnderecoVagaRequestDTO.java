package com.cptrans.petrocarga.dto;

import com.cptrans.petrocarga.models.EnderecoVaga;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

public class EnderecoVagaRequestDTO {
    @Valid
    @Size(min = 3, max = 50, message="O campo 'codigoPMP' deve ter entre 3 e 50 caracteres.")
    @Schema(
        description = "Código PMP do endereço da vaga",
        example = "Pb-1234"
    )
    private String codigoPmp;

    @Valid
    @Schema(
        description = "Logradouro do endereço da vaga",
        example = "Rua Paulo Barbosa"
    )
    private String logradouro;

    @Valid
    @Schema(
        description = "Bairro do endereço da vaga",
        example = "Centro"
    )
    private String bairro;

    public EnderecoVaga toEntity() {
        EnderecoVaga enderecoVaga = new EnderecoVaga();
        enderecoVaga.setBairro(this.bairro);
        enderecoVaga.setCodigoPmp(this.codigoPmp);
        enderecoVaga.setLogradouro(this.logradouro);
        return enderecoVaga;
    }

    public String getCodigoPmp(){
        return this.codigoPmp;
    }

    public String getLogradouro(){
        return this.logradouro;
    }

    public String getBairro(){
        return this.bairro;
    }
}
