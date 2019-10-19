package com.viniciusog.whatsapp.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.viniciusog.whatsapp.Helper.Base64Custom;

public class ConfiguracaoFirebase {

    private static DatabaseReference database;
    private static FirebaseAuth auth;
    private static StorageReference storage;

    //Retorna instancia do FirebaseDatabase
    public static DatabaseReference getFirebaseDatabase() {
        if (database == null) {
            database = FirebaseDatabase.getInstance().getReference();
        }
        return database;
    }

    //Retorna instancia do FirebaseeAuth;
    public static FirebaseAuth getFirebaseAutenticacao() {
        if (auth == null) {
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    //Retorna instância do FirebaseStorage / StorageReference
    public static StorageReference getFirebaseStorage() {
        if (storage == null) {
            storage = FirebaseStorage.getInstance().getReference();
        }
        return storage;
    }
}