package com.example.myapplication;

public class Acesso {

    private long id;
    private String plataforma;
    private long categoriaId;
    private String acesso;
    private String senha;

    public Acesso(long id, String plataforma, long categoriaId, String acesso, String senha) {
        this.id = id;
        this.plataforma = plataforma;
        this.categoriaId = categoriaId;
        this.acesso = acesso;
        this.senha = senha;
    }

    public long getId() {
        return id;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }

    public long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getAcesso() {
        return acesso;
    }

    public void setAcesso(String acesso) {
        this.acesso = acesso;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
