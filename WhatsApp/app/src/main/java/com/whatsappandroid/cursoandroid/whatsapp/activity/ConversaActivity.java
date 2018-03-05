package com.whatsappandroid.cursoandroid.whatsapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.whatsappandroid.cursoandroid.whatsapp.R;
import com.whatsappandroid.cursoandroid.whatsapp.adapter.MensagemAdapter;
import com.whatsappandroid.cursoandroid.whatsapp.config.ConfiguracaoFirebase;
import com.whatsappandroid.cursoandroid.whatsapp.helper.Base64Custom;
import com.whatsappandroid.cursoandroid.whatsapp.helper.Preferencias;
import com.whatsappandroid.cursoandroid.whatsapp.model.Conversa;
import com.whatsappandroid.cursoandroid.whatsapp.model.Mensagem;

import java.util.ArrayList;

public class ConversaActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private String nomeUsuarioDestinatario;
    private String idUsuarioDestinatario;

    private EditText edt_mensagem;
    private ImageButton btn_mensagem;
    private DatabaseReference firebase;
    private ListView listView;
    private ArrayList<Mensagem> mensagens;
    private ArrayAdapter<Mensagem> adapter;
    private ValueEventListener valueEventListenerMensagem;

    private String idUsuarioRemetente;
    private String nomeUsuarioRemetente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);

        toolbar = (Toolbar) findViewById(R.id.tb_conversa);
        edt_mensagem = (EditText) findViewById(R.id.edt_mensagem);
        btn_mensagem = (ImageButton) findViewById(R.id.btn_enviar);
        listView = (ListView) findViewById(R.id.lsv_conversa);

        Preferencias preferencias = new Preferencias(ConversaActivity.this);
        idUsuarioRemetente = preferencias.getIdentificador();
        nomeUsuarioRemetente = preferencias.getNome();

        Bundle extra = getIntent().getExtras();

        if (extra != null){
            nomeUsuarioDestinatario = extra.getString("nome");
            String emailDestinatario = extra.getString("email");
            idUsuarioRemetente = Base64Custom.codificarBase64(emailDestinatario);
        }

        toolbar.setTitle(nomeUsuarioDestinatario);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);


        mensagens = new ArrayList<>();
        adapter = new MensagemAdapter(ConversaActivity.this, mensagens);
        listView.setAdapter(adapter);

        firebase = ConfiguracaoFirebase.getFirebase()
        .child("mensagens")
        .child(idUsuarioRemetente)
        .child(idUsuarioDestinatario);

        valueEventListenerMensagem = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mensagens.clear();

                for (DataSnapshot dados : dataSnapshot.getChildren()){
                    Mensagem mensagem = dados.getValue(Mensagem.class);
                    mensagens.add(mensagem);
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        firebase.addValueEventListener(valueEventListenerMensagem);


        btn_mensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textoMensagem = edt_mensagem.getText().toString();

                if (textoMensagem.isEmpty()){
                    Toast.makeText(ConversaActivity.this, "Digite uma mensagem para enviar!", Toast.LENGTH_LONG).show();
                }else {

                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario(idUsuarioRemetente);
                    mensagem.setMensagem(textoMensagem);

                   Boolean retornoMensagemRemetente =  salvarConversa(idUsuarioRemetente, idUsuarioDestinatario, mensagem);
                    if (!retornoMensagemRemetente){
                        Toast.makeText(
                          ConversaActivity.this,
                                "Problema ao salvar mensagem, tente novamente!",
                                Toast.LENGTH_LONG
                        ).show();
                    }else{

                        Boolean retornoMensagemDestinatario = salvarConversa(idUsuarioDestinatario, idUsuarioRemetente, mensagem);
                        if (!retornoMensagemRemetente){
                            Toast.makeText(
                                    ConversaActivity.this,
                                    "Problema enviar mensagem ao destinatário, tente novamente!",
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    }

                    Conversa conversa = new Conversa();
                    conversa.setIdUsuario(idUsuarioDestinatario);
                    conversa.setNome(nomeUsuarioDestinatario);
                    conversa.setMensagem(textoMensagem);

                    Boolean retornoConversaRemetente =  salvarConversa(idUsuarioRemetente, idUsuarioDestinatario, conversa);
                    if (!retornoConversaRemetente){
                        Toast.makeText(
                                ConversaActivity.this,
                                "Problema ao salvar conversa" +
                                        ", tente novamente!",
                                Toast.LENGTH_LONG
                        ).show();
                    }else{

                     conversa = new Conversa();
                        conversa.setIdUsuario(idUsuarioRemetente);
                        conversa.setNome(nomeUsuarioRemetente);
                        conversa.setMensagem(textoMensagem);

                        Boolean retornoConversaDestinatario =  salvarConversa(idUsuarioDestinatario, idUsuarioRemetente, conversa);
                        if (!retornoConversaDestinatario){
                            Toast.makeText(
                                    ConversaActivity.this,
                                    "Problema ao salvar conversa" +
                                            ", tente novamente!",
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    }


                    edt_mensagem.setText("");
                }

            }
        });

    }


    private boolean salvarConversa(String idRemetente, String idDestinatario, Mensagem mensagem){
        try{
            firebase = ConfiguracaoFirebase.getFirebase().child("mensagem");

            firebase.child(idRemetente)
                    .child(idDestinatario)
                    .push()
                    .setValue(mensagem);

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private boolean salvarConversa(String idRemetente, String idDestinatario, Conversa conversa){
        try{
            firebase = ConfiguracaoFirebase.getFirebase().child("conversas");
            firebase.child(idRemetente)
                    .child(idDestinatario)
                    .setValue(conversa);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerMensagem);
    }
}

