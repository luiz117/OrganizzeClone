package com.luizsilva.example.organizze.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.app.SlideFragment;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.luizsilva.example.organizze.R;
import com.luizsilva.example.organizze.activity.CadastroActivity;
import com.luizsilva.example.organizze.activity.LoginActivity;
import com.luizsilva.example.organizze.config.ConfiguracaoFirebase;

public class MainActivity extends IntroActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);



        setButtonBackVisible(false);
        setButtonNextVisible(false);


        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_1)
                .build()
        );

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_2)
                .build()
        );

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_3)
                .build()
        );

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_4)
                .build()
        );


        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_cadastro)
                .canGoForward(false)
                .build()
        );

    }

    @Override
    protected void onStart() {
        super.onStart();
        verificarUsuarioLogado();
    }

    public void btnEntrar(View view){

        startActivity(new Intent(this, LoginActivity.class));

    }

    public void btnCadastrar(View view){
        startActivity(new Intent(this, CadastroActivity.class));

    }

    public void verificarUsuarioLogado(){

        auth = ConfiguracaoFirebase.getFirebaseAuth();
        //auth.signOut();

        if(auth.getCurrentUser() != null){
            abrirtelaPrincipal();
        }
    }

    public void abrirtelaPrincipal(){
        startActivity(new Intent(this, PrincipalActivity.class));
    }

}