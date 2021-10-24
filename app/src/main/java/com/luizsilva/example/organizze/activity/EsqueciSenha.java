package com.luizsilva.example.organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.luizsilva.example.organizze.R;
import com.luizsilva.example.organizze.model.Usuario;

public class EsqueciSenha extends AppCompatActivity {

    private EditText campoEmail;
    private Button buttonOK;
    private Usuario usuario;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esqueci_senha);
        campoEmail = findViewById(R.id.emailLoginSenha);
        buttonOK = findViewById(R.id.btnOK);

        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recuperarLogin();
            }
        });
    }


    public void recuperarLogin(){

        String textoEmail = campoEmail.getText().toString();

        if(!textoEmail.isEmpty()) {

            FirebaseAuth.getInstance().sendPasswordResetEmail(textoEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                finish();
                            }else{
                                String excecao="";
                                try{
                                    throw task.getException();

                                }catch (FirebaseAuthInvalidUserException e){
                                    excecao="Usuário não está cadastrado";
                                }catch (Exception e){
                                    excecao = "Erro ao cadastrar o usuário" + e.getMessage();
                                    e.printStackTrace();
                                }
                                Toast.makeText(
                                        EsqueciSenha.this,
                                        excecao,
                                        Toast.LENGTH_LONG).show();
                        }
                    }
            }); } else{

            Toast.makeText(
                    EsqueciSenha.this,
                    "Preencha o email",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}