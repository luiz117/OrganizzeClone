package com.luizsilva.example.organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.luizsilva.example.organizze.R;
import com.luizsilva.example.organizze.config.ConfiguracaoFirebase;
import com.luizsilva.example.organizze.helper.Base64Custom;
import com.luizsilva.example.organizze.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha;
    private Button botaoCadastrar;
    private FirebaseAuth auth;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        getSupportActionBar().setTitle("Cadastro");

        campoNome = findViewById(R.id.editNome);
        campoEmail = findViewById(R.id.editEmail);
        campoSenha = findViewById(R.id.editSenha);
        botaoCadastrar = findViewById(R.id.btnCadastrar);

        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textoNome = campoNome.getText().toString();
                String textoEmail = campoEmail.getText().toString();
                String textoSenha = campoSenha.getText().toString();
                //validar campos preeenchidos

                if(!textoNome.isEmpty()){
                    if(!textoEmail.isEmpty()){
                        if(!textoSenha.isEmpty()){

                            usuario = new Usuario();
                            usuario.setNome(textoNome);
                            usuario.setEmail(textoEmail);
                            usuario.setSenha(textoSenha);

                            cadastrarUsuario();

                        }else{
                            Toast.makeText(
                                    CadastroActivity.this,
                                    "Preencha a senha",
                                    Toast.LENGTH_SHORT)
                                    .show();
                            }
                    } else {
                        Toast.makeText(
                                CadastroActivity.this,
                                "Preencha o email",
                                Toast.LENGTH_SHORT)
                                .show();
                        }
                    }else{
                    Toast.makeText(
                            CadastroActivity.this,
                            "Preencha o nome",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void cadastrarUsuario(){

        auth = ConfiguracaoFirebase.getFirebaseAuth();
        auth.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String idUsuario = Base64Custom.codeBase64(usuario.getEmail());
                    usuario.setIdUsuario(idUsuario);
                    usuario.salvarusuario();
                    finish();

                }else{

                    String excecao="";
                    try{
                        throw task.getException();

                    }catch (FirebaseAuthWeakPasswordException e) {
                        excecao = "Digite uma senha mais forte com letras e números e mais de 7 caracteres";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "Por favor, digite um email válido";
                    }catch (FirebaseAuthUserCollisionException e){
                        excecao = "Conta já existente";
                    }
                    catch (Exception e){
                        excecao = "Erro ao cadastrar o usuário" + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(
                            CadastroActivity.this,
                            excecao,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}