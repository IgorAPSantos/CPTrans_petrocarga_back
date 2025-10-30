package com.cptrans.petrocarga.enums;

public enum TipoVeiculoEnum {
    AUTOMOVEL("Automóvel",5),
    VUC("Veículo Urbano de Carga",7),
    CAMINHONETA("Caminhoneta",8),
    CAMINHAO_MEDIO("Caminhão Médio",12),
    CAMINHAO_LONGO("Caminhão Longo",18),
    CARRETA("Carreta",30);

    private final String descricao;
    private final Integer comprimento;

    private TipoVeiculoEnum(String descricao, Integer comprimento) {
        this.descricao = descricao;
        this.comprimento = comprimento;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getComprimento() {
        return comprimento;
    }
}
