package com.whatsappandroid.cursoandroid.whatsapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.whatsappandroid.cursoandroid.whatsapp.R;
import com.whatsappandroid.cursoandroid.whatsapp.adapter.TabAdapter;
import com.whatsappandroid.cursoandroid.whatsapp.config.ConfiguracaoFirebase;
import com.whatsappandroid.cursoandroid.whatsapp.helper.Base64Custom;
import com.whatsappandroid.cursoandroid.whatsapp.helper.Preferencias;
import com.whatsappandroid.cursoandroid.whatsapp.helper.SlidingTabLayout;
import com.whatsappandroid.cursoandroid.whatsapp.model.Contato;
import com.whatsappandroid.cursoandroid.whatsapp.model.Usuario;

public class MainActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private FirebaseAuth usuarioAutenticacao;

    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private String identificadorContato;
    private DatabaseReference firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usuarioAutenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("WhatsApp");
        setSupportActionBar(toolbar);

        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tabs);
        viewPager = (ViewPager) findViewById(R.id.vp_pagina);

        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.colorAccent));

        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);

        slidingTabLayout.setViewPager(viewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.item_sair:
                deslogarUsuario();
                return true;

            case R.id.item_config:
                return true;
            case R.id.item_adicionar:
                abrirCadastroContato();
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    private void abrirCadastroContato(){

        AlertDialog.Builder alertDioalog = new AlertDialog.Builder(MainActivity.this);

        alertDioalog.setTitle("Novo Contato");
        alertDioalog.setMessage("E-mail do usuário");
        alertDioalog.setCancelable(false);

        final EditText editText = new EditText(MainActivity.this);
        alertDioalog.setView( editText );

        alertDioalog.setPositiveButton("Cadastrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String emailContato = editText.getText().toString();


                if (emailContato.isEmpty()){
                    Toast.makeText(MainActivity.this, "Preencha o e-mail!", Toast.LENGTH_LONG).show();

                }else{
                    //Verificar usuario já cadastrado
                    identificadorContato = Base64Custom.codificarBase64(emailContato);
                    //Recuperar instãncia firebase
                    firebase = ConfiguracaoFirebase.getFirebase().child("usuarios").child(identificadorContato);

                    firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getValue() != null){

                                Usuario usuarioContato = dataSnapshot.getValue(Usuario.class);

                                Preferencias preferencias = new Preferencias(MainActivity.this);
                                String identificadorUsuarioLogado = preferencias.getIdentificador();

                                firebase = ConfiguracaoFirebase.getFirebase();
                                firebase = firebase.child("contatos")
                                        .child(identificadorUsuarioLogado)
                                        .child(identificadorContato);

                                Contato contato = new Contato();
                                contato.setIdentificadorUsuario(identificadorContato);
                                contato.setEmail(usuarioContato.getEmail());
                                contato.setNome(usuarioContato.getNome());

                                firebase.setValue(contato);



                            }else{
                                Toast.makeText(MainActivity.this, "Usuário não possui cadastro!", Toast.LENGTH_LONG).show();

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

            }
        });

        alertDioalog.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {



            }
        });

        alertDioalog.create();
        alertDioalog.show();

    }

    private void deslogarUsuario(){

        usuarioAutenticacao.signOut();
        Intent intent =  new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

    }

}
