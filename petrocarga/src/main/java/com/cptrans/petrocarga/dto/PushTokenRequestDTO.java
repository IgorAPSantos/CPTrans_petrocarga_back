package com.cptrans.petrocarga.dto;

import com.cptrans.petrocarga.enums.PlataformaEnum;
import com.cptrans.petrocarga.models.PushToken;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PushTokenRequestDTO {
    @NotNull
    @NotBlank
    private String token;

    @NotNull
    private PlataformaEnum plataforma;

    public String getToken() {
        return token;
    }

    public PlataformaEnum getPlataforma() {
        return plataforma;
    }

    public PushToken toEntity(){
        return new PushToken(this.token, this.plataforma);
    }
}
