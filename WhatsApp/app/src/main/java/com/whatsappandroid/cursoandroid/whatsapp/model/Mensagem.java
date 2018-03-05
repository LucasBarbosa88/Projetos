package com.whatsappandroid.cursoandroid.whatsapp.model;

/**
 * Created by Lucas on 24/11/2017.
 */

public class Mensagem {

    public Mensagem() {
    }

    private String idUsuario;
    private String mensagem;

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
