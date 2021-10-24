package com.luizsilva.example.organizze.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfiguracaoFirebase {

    private static FirebaseAuth auth;
    private static DatabaseReference reference;


    public static DatabaseReference getFirebaseDatabase(){
        if(reference==null){
            reference = FirebaseDatabase.getInstance().getReference();
        }

        return reference;
    }

    //retorna a instancia do FireBaseauth
    public static FirebaseAuth getFirebaseAuth(){

        if(auth==null){
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }
}
