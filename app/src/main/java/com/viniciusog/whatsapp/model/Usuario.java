package com.viniciusog.whatsapp.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.viniciusog.whatsapp.Helper.UsuarioFirebase;
import com.viniciusog.whatsapp.activity.ConfiguracaoActivity;
import com.viniciusog.whatsapp.config.ConfiguracaoFirebase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

//Serializable é necessário para passar objeto entre activity e outra
public class Usuario implements Serializable {

    //Id do usuário é o email em base 64
    private String id;
    private String nome;
    private String email;
    private String senha;
    private String foto;

    public Usuario() {

    }

    public void salvar() {

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuario = firebaseRef.child("usuarios").child(getId());

        //Irá salvar o objeto 'usuario' inteiro no firebase
        usuario.setValue(this);

    }

    public void atualizar() {
        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference usuariosRef = database.child("usuarios")
                .child(identificadorUsuario);


        Map<String, Object> valoresUsuario = converterParaMap();

        usuariosRef.updateChildren(valoresUsuario);
    }

    @Exclude
    public Map<String, Object> converterParaMap() {
        HashMap<String, Object> usuarioMap = new HashMap<>();

        usuarioMap.put("email", getEmail());
        usuarioMap.put("nome", getNome());
        usuarioMap.put("foto", getFoto());

        return usuarioMap;
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

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
