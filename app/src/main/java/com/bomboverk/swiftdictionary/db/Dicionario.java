package com.bomboverk.swiftdictionary.db;

import java.io.Serializable;

public class Dicionario implements Serializable {

    private Long dicionarioID;
    private String nomeDicionario;
    private String linguagem;
    private int quantidadePalavras;
    private String dataCriacao;

    public void setDicionarioID(Long dicionarioID) {
        this.dicionarioID = dicionarioID;
    }

    public void setNomeDicionario(String nomeDicionario) {
        this.nomeDicionario = nomeDicionario;
    }

    public void setLinguagem(String linguagem) {
        this.linguagem = linguagem;
    }

    public void setQuantidadePalavras(int quantidadePalavras) {
        this.quantidadePalavras = quantidadePalavras;
    }

    public void setDataCriacao(String dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Long getDicionarioID() {
        return dicionarioID;
    }

    public String getNomeDicionario() {
        return nomeDicionario;
    }

    public String getLinguagem() {
        return linguagem;
    }

    public int getQuantidadePalavras() {
        return quantidadePalavras;
    }

    public String getDataCriacao() {
        return dataCriacao;
    }
}
