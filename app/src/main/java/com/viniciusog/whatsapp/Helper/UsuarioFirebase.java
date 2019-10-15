package com.viniciusog.whatsapp.Helper;

import com.google.firebase.auth.FirebaseAuth;
import com.viniciusog.whatsapp.config.ConfiguracaoFirebase;

public class UsuarioFirebase {

    public static String getIdentificadorUsuario() {
        FirebaseAuth usuario =  ConfiguracaoFirebase.getFirebaseAutenticacao();
        String emailUsuario = usuario.getCurrentUser().getEmail();
        return Base64Custom.codificarBase64(emailUsuario);
    }
}
