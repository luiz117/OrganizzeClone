package com.luizsilva.example.organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.luizsilva.example.organizze.R;
import com.luizsilva.example.organizze.config.ConfiguracaoFirebase;
import com.luizsilva.example.organizze.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private EditText campoEmail, campoSenha;
    private Button buttonlogin;
    private Usuario usuario;
    private FirebaseAuth auth;
    private TextView txtEsqueciSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        campoEmail = findViewById(R.id.emailLoginSenha);
        campoSenha = findViewById(R.id.senhaLogin);
        buttonlogin = findViewById(R.id.btnLogin);
        txtEsqueciSenha = findViewById(R.id.txtEsqueciSenha);

        txtEsqueciSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),EsqueciSenha.class));
            }
        });

        buttonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String textoEmail = campoEmail.getText().toString();
                String textoSenha = campoSenha.getText().toString();

                //validar campos
                if(!textoEmail.isEmpty()){
                    if(!textoSenha.isEmpty()){

                        usuario = new Usuario();
                        usuario.setEmail(textoEmail);
                        usuario.setSenha(textoSenha);
                        validarLogin();


                    }else{
                        Toast.makeText(
                                LoginActivity.this,
                                "Preencha a senha",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }else{
                    Toast.makeText(
                            LoginActivity.this,
                            "Preencha o email",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    public void validarLogin(){

        auth = ConfiguracaoFirebase.getFirebaseAuth();
        auth.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    abrirtelaPrincipal();
                }else{
                    String excecao="";
                    try{
                        throw task.getException();

                    }catch (FirebaseAuthInvalidUserException e){
                        excecao="Usuário não está cadastrado";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "O e-mail e senha não correspondem a um usuário cadastrado";
                    }catch (Exception e){
                        excecao = "Erro ao cadastrar o usuário" + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(
                            LoginActivity.this,
                            excecao,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void abrirtelaPrincipal(){
        startActivity(new Intent(this, PrincipalActivity.class));
        finish();
    }
}