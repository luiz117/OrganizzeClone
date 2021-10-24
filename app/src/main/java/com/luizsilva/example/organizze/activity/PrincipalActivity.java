package com.luizsilva.example.organizze.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.luizsilva.example.organizze.R;
import com.luizsilva.example.organizze.adapter.AdapterMovimentacao;
import com.luizsilva.example.organizze.config.ConfiguracaoFirebase;
import com.luizsilva.example.organizze.databinding.ActivityPrincipalBinding;
import com.luizsilva.example.organizze.helper.Base64Custom;
import com.luizsilva.example.organizze.helper.DateCustom;
import com.luizsilva.example.organizze.model.Movimentacao;
import com.luizsilva.example.organizze.model.Usuario;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class PrincipalActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityPrincipalBinding binding;
    private TextView viewText, textSaudacao, textSaldo;
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAuth();
    private Double despesaTotal = 0.0;
    private Double receitaTotal = 0.0;
    private Double resumousuario = 0.0;
    private DatabaseReference userRef;
    private ValueEventListener valueEventListenerUsuario;
    private RecyclerView recyclerMovimentos;
    private AdapterMovimentacao adapterMovimentacao;
    private Movimentacao movimentacaodel;
    private List<Movimentacao> movimentacoes = new ArrayList<>();
    private DatabaseReference movimentacaoRef;
    private String mesAnoSelecionado;//=DateCustom.dateShowUser();

    Button btnBack, btnForward;
    private ValueEventListener valueEventListenerMovimentacoes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPrincipalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        textSaudacao = findViewById(R.id.textSaudacao);
        textSaldo = findViewById(R.id.textSaldo);

        viewText = findViewById(R.id.txtDataExibir);
        viewText.setText(DateCustom.dateShowUser());

        btnBack = findViewById(R.id.btnBack);
        btnForward = findViewById(R.id.btnForward);

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setTitle("Organizze");
        adapterMovimentacao = new AdapterMovimentacao(movimentacoes,this);

        recyclerMovimentos = findViewById(R.id.recyclerMovimentos);
        recyclerMovimentos.setLayoutManager(new LinearLayoutManager(this));
        recyclerMovimentos.setHasFixedSize(true);
        recyclerMovimentos.setAdapter(adapterMovimentacao);
        swipe();
        mesAnoSelecionado = DateCustom.dateToday();

        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movimentacaoRef.addValueEventListener(valueEventListenerMovimentacoes);
                mesAnoSelecionado = DateCustom.dateFirebase(1);
                recuperarMovimentacoes();
                movimentacaoRef.removeEventListener(valueEventListenerMovimentacoes);
                movimentacaoRef.addValueEventListener(valueEventListenerMovimentacoes);
                viewText.setText(DateCustom.dateShowUser());

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mesAnoSelecionado = DateCustom.dateFirebase(0);
                recuperarMovimentacoes();
                movimentacaoRef.removeEventListener(valueEventListenerMovimentacoes);
                movimentacaoRef.addValueEventListener(valueEventListenerMovimentacoes);
                viewText.setText(DateCustom.dateShowUser());
            }
        });

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_principal);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

    }


    public void swipe(){
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags,swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                excluirMovimentacao(viewHolder);
            }
        };

        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerMovimentos);

    }

    public void excluirMovimentacao(RecyclerView.ViewHolder viewHolder){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Excluir movimentação da Conta");
        alertDialog.setMessage("Voce tem certeza que deseja excluir essa movimentação");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                int p = viewHolder.getAdapterPosition();
                movimentacaodel = movimentacoes.get(p);

                String emailUsuario =  auth.getCurrentUser().getEmail();
                String idUsuario = Base64Custom.codeBase64(emailUsuario);

                movimentacaoRef = firebaseRef.child("movimentacao")
                        .child(idUsuario)
                        .child(mesAnoSelecionado);

                movimentacaoRef.child(movimentacaodel.getKey()).removeValue();
                adapterMovimentacao.notifyItemRemoved(p);
                atualizarSaldo();
            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(PrincipalActivity.this,"Cancelado", Toast.LENGTH_SHORT)
                        .show();
                adapterMovimentacao.notifyDataSetChanged();
            }
        });

        AlertDialog alert  = alertDialog.create();
        alert.show();


    }

    public void atualizarSaldo(){
        String emailUsuario =  auth.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codeBase64(emailUsuario);
        userRef= firebaseRef.child("usuario").child(idUsuario);

        if(movimentacaodel.getTipo().equals("r")){
            receitaTotal = receitaTotal - movimentacaodel.getValor();
            userRef.child("receitaTotal").setValue(receitaTotal);
        }

        if(movimentacaodel.getTipo().equals("d")){
            despesaTotal = despesaTotal - movimentacaodel.getValor();
            userRef.child("despesaTotal").setValue(despesaTotal);
        }


    }

    public void recuperarMovimentacoes(){

        String emailUsuario =  auth.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codeBase64(emailUsuario);

        movimentacaoRef = firebaseRef.child("movimentacao")
        .child(idUsuario)
        .child(mesAnoSelecionado);


        valueEventListenerMovimentacoes = movimentacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                movimentacoes.clear();

                for(DataSnapshot dados : snapshot.getChildren()){
                    Movimentacao movimentacao = dados.getValue(Movimentacao.class);
                    movimentacao.setKey(dados.getKey());
                    movimentacoes.add(movimentacao);
                }

                adapterMovimentacao.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




    public void recuperarResumo(){
        String emailUsuario =  auth.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codeBase64(emailUsuario);
        userRef= firebaseRef.child("usuario").child(idUsuario);
        valueEventListenerUsuario = userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Usuario usuario = snapshot.getValue(Usuario.class);

                despesaTotal = usuario.getDespesaTotal();
                receitaTotal = usuario.getReceitaTotal();

                resumousuario = receitaTotal - despesaTotal;

                DecimalFormat df = new DecimalFormat("0.##");
                String resultFormatado = df.format(resumousuario);

                textSaudacao.setText("Olá, " + usuario.getNome());
                textSaldo.setText("R$ " + resultFormatado);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuSair :
                auth.signOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_principal);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }


    public void adicionarDespesa(View view){

        startActivity(new Intent(this,DespesasActivity.class));

    }

    public void adicionarReceita(View view){
        startActivity(new Intent(this,ReceitasActivity.class));

    }

    public void beforeClick(View view){

        mesAnoSelecionado = DateCustom.dateFirebase(0);
        viewText.setText(DateCustom.dateShowUser());
    }

    public void afterClick(View view){

        //mesAnoSelecionado = DateCustom.dateFirebase(1);
        //viewText.setText(   DateCustom.dateShowUser());
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarResumo();
        recuperarMovimentacoes();
    }

    @Override
    protected void onStop() {
        super.onStop();
        userRef.removeEventListener(valueEventListenerUsuario);
        movimentacaoRef.removeEventListener(valueEventListenerMovimentacoes);
    }
}