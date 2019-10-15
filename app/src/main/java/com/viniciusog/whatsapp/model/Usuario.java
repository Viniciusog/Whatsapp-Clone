package com.viniciusog.whatsapp.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.viniciusog.whatsapp.activity.ConfiguracaoActivity;
import com.viniciusog.whatsapp.config.ConfiguracaoFirebase;

public class Usuario {

    //Id do usuário é o email em base 64
    private String id;
    private String nome;
    private String email;
    private String senha;

    public Usuario() {

    }

    public void salvar() {

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuario = firebaseRef.child("usuarios").child( getId() );

        //Irá salvar o objeto 'usuario' inteiro no firebase
        usuario.setValue( this );

    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
