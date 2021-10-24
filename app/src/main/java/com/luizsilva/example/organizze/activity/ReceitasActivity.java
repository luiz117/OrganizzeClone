package com.luizsilva.example.organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.luizsilva.example.organizze.R;
import com.luizsilva.example.organizze.config.ConfiguracaoFirebase;
import com.luizsilva.example.organizze.helper.Base64Custom;
import com.luizsilva.example.organizze.helper.DateUtil;
import com.luizsilva.example.organizze.model.Movimentacao;
import com.luizsilva.example.organizze.model.Usuario;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ReceitasActivity extends AppCompatActivity {
    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private EditText campoValor;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAuth();
    private Double receitaTotal, receitaAtualizada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);

        campoValor = findViewById(R.id.editValorRec);
        campoData = findViewById(R.id.editDataRec);
        campoCategoria = findViewById(R.id.editCatRec);
        campoDescricao = findViewById(R.id.editDescRec);

        campoData.setText(DateUtil.dataAtual());
        recuperarReceitaTotal();


    }

    public void salvarReceita(View v){

        if(validarCamposReceita()){

            String dataEscolhida = campoData.getText().toString();
            Double valorRecuperado = Double.parseDouble(campoValor.getText().toString());

            movimentacao = new Movimentacao();
            movimentacao.setValor(Double.parseDouble(campoValor.getText().toString()));
            movimentacao.setCategoria(campoCategoria.getText().toString());
            movimentacao.setDescricao(campoDescricao.getText().toString());
            movimentacao.setData(campoData.getText().toString());
            movimentacao.setTipo("r");

            receitaAtualizada = receitaTotal + valorRecuperado;
            atualizarReceita(receitaAtualizada);
            movimentacao.salvar(dataEscolhida);
            finish();


        }
    }


    public Boolean validarCamposReceita(){

        String textoValor = campoValor.getText().toString();
        String textoData = campoData.getText().toString();
        String textoCategoria = campoCategoria.getText().toString();
        String textoDescricao  = campoDescricao.getText().toString();

        if(!textoValor.isEmpty()){
            if(!textoData.isEmpty()){
                if(!textoCategoria.isEmpty()){
                    if(!textoDescricao.isEmpty()){
                        return true;
                    }else {
                        Toast.makeText(
                                ReceitasActivity.this,
                                "Descrição não preenchida",
                                Toast.LENGTH_LONG).show();
                        return false;
                    }
                }else {
                    Toast.makeText(
                            ReceitasActivity.this,
                            "Categoria não preenchida",
                            Toast.LENGTH_LONG).show();
                    return false;
                }
            }else {
                Toast.makeText(
                        ReceitasActivity.this,
                        "Data não preenchida",
                        Toast.LENGTH_LONG).show();
                return false;
            }

        }else {
            Toast.makeText(
                    ReceitasActivity.this,
                    "Valor não preenchido",
                    Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public void recuperarReceitaTotal(){

        String emailUsuario =  auth.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codeBase64(emailUsuario);
        DatabaseReference userRef= firebaseRef.child("usuario").child(idUsuario);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Usuario usuario = snapshot.getValue(Usuario.class);
                receitaTotal = usuario.getReceitaTotal();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void atualizarReceita(Double receita){
        String emailUsuario =  auth.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codeBase64(emailUsuario);
        DatabaseReference userRef= firebaseRef.child("usuario").child(idUsuario);
        userRef.child("receitaTotal").setValue(receita);

    }

}