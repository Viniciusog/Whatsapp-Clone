package com.viniciusog.whatsapp.model;

import com.google.firebase.database.DatabaseReference;
import com.viniciusog.whatsapp.config.ConfiguracaoFirebase;

public class Conversa {

    private String idRemetente;
    private String  idDestinatario;
    private String ultimaMensagem;
    private Usuario usuarioExibicao;

    public Conversa() {

    }

    public void salvar() {
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference conversaRef  = database.child("conversas");

        conversaRef.child( getIdRemetente() )
                .child( getIdDestinatario())
                .setValue( this ); //Vamos salvar todos os dados do objeto conversa

    }

    public String getIdRemetente() {
        return idRemetente;
    }

    public void setIdRemetente(String idRemetente) {
        this.idRemetente = idRemetente;
    }

    public String getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(String idDestinatario) {
        this.idDestinatario = idDestinatario;
    }

    public String getUltimaMensagem() {
        return ultimaMensagem;
    }

    public void setUltimaMensagem(String ultimaMensagem) {
        this.ultimaMensagem = ultimaMensagem;
    }

    public Usuario getUsuarioExibicao() {
        return usuarioExibicao;
    }

    public void setUsuarioExibicao(Usuario usuario) {
        this.usuarioExibicao = usuario;
    }
}