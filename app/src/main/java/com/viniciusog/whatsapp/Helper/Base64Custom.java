package com.viniciusog.whatsapp.Helper;

import android.util.Base64;

public class Base64Custom {

    public static String codificarBase64(String texto) {
        //Utilizando regex para substituir caracteres inválidos
        return Base64.encodeToString(texto.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)", "");
    }

    public static String decodificarBase64(String textoCodificado) {
        return new String(Base64.decode(textoCodificado, Base64.DEFAULT));
    }
}
