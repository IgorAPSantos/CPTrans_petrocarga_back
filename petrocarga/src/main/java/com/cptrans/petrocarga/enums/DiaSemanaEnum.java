package com.cptrans.petrocarga.enums;

public enum DiaSemanaEnum {
    DOMINGO (1, "Domingo"),
    SEGUNDA (2, "Segunda-feira"),
    TERCA (3, "Terça-feira"),
    QUARTA (4, "Quarta-feira"),
    QUINTA (5, "Quinta-feira"),
    SEXTA (6, "Sexta-feira"),
    SABADO (7, "Sábado");

    public Integer codigo;
    public String descricao;

    private DiaSemanaEnum(Integer codigo, String descricao){
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public static DiaSemanaEnum toEnum(Integer codigo){
        if(codigo == null){
            return null;
        }

        for(DiaSemanaEnum dia : DiaSemanaEnum.values()){
            if(codigo.equals(dia.codigo)){
                return dia;
            }
        }

        throw new IllegalArgumentException("Dia da semana inválido. Código: " + codigo);
    }
}
