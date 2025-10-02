package com.cptrans.petrocarga.dto;

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
    private String codigoPMP;

    @Valid
    @Size(min = 3, max = 100, message="O campo 'logradouro' deve ter entre 3 e 100 caracteres.")
    @Schema(
        description = "Logradouro do endereço da vaga",
        example = "Rua Paulo Barbosa"
    )
    private String logradouro;

    @Valid
    @Size(min = 3, max = 50, message="O campo 'bairro' deve ter entre 3 e 50 caracteres.")
    @Schema(
        description = "Bairro do endereço da vaga",
        example = "Centro"
    )
    private String bairro;

    public String getCodigoPMP(){
        return this.codigoPMP;
    }

    public String getLogradouro(){
        return this.logradouro;
    }

    public String getBairro(){
        return this.bairro;
    }
}
