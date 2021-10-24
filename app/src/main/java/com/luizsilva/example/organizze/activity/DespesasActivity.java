package com.luizsilva.example.organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

public class DespesasActivity extends AppCompatActivity {

    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private EditText campoValor;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAuth();
    private Double despesaTotal, despesaAtualizada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);

        campoValor = findViewById(R.id.editValorRec);
        campoData = findViewById(R.id.editDataDesp);
        campoCategoria = findViewById(R.id.editCatDespesa);
        campoDescricao = findViewById(R.id.editDespesaDesc);

        campoData.setText(DateUtil.dataAtual());
        recuperarDespesaTotal();
    }


    public void salvarDespesa(View v){

        if(validarCamposDespesa()){

            String dataEscolhida = campoData.getText().toString();
            Double valorRecuperado = Double.parseDouble(campoValor.getText().toString());

            movimentacao = new Movimentacao();
            movimentacao.setValor(Double.parseDouble(campoValor.getText().toString()));
            movimentacao.setCategoria(campoCategoria.getText().toString());
            movimentacao.setDescricao(campoDescricao.getText().toString());
            movimentacao.setData(campoData.getText().toString());
            movimentacao.setTipo("d");

            despesaAtualizada = despesaTotal + valorRecuperado;
            atualizarDespesa(despesaAtualizada);


            movimentacao.salvar(dataEscolhida);
            finish();
        }
    }


    public Boolean validarCamposDespesa(){

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
                                DespesasActivity.this,
                                "Descrição não preenchida",
                                Toast.LENGTH_LONG).show();
                        return false;
                    }
                }else {
                    Toast.makeText(
                            DespesasActivity.this,
                            "Categoria não preenchida",
                            Toast.LENGTH_LONG).show();
                    return false;
                }
            }else {
                Toast.makeText(
                        DespesasActivity.this,
                        "Data não preenchida",
                        Toast.LENGTH_LONG).show();
                return false;
            }

        }else {
            Toast.makeText(
                    DespesasActivity.this,
                    "Valor não preenchido",
                    Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public void recuperarDespesaTotal(){

        String emailUsuario =  auth.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codeBase64(emailUsuario);
        DatabaseReference userRef= firebaseRef.child("usuario").child(idUsuario);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Usuario usuario = snapshot.getValue(Usuario.class);
                despesaTotal = usuario.getDespesaTotal();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void atualizarDespesa(Double despesa){
        String emailUsuario =  auth.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codeBase64(emailUsuario);
        DatabaseReference userRef= firebaseRef.child("usuario").child(idUsuario);
        userRef.child("despesaTotal").setValue(despesa);

    }

}