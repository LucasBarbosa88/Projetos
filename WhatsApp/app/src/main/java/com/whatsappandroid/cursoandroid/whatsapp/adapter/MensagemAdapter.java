package com.whatsappandroid.cursoandroid.whatsapp.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.whatsappandroid.cursoandroid.whatsapp.R;
import com.whatsappandroid.cursoandroid.whatsapp.helper.Preferencias;
import com.whatsappandroid.cursoandroid.whatsapp.model.Mensagem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 24/11/2017.
 */

public class MensagemAdapter extends ArrayAdapter<Mensagem>{

    private Context context;
    private ArrayList<Mensagem> mensagens;

    public MensagemAdapter( Context c,  ArrayList<Mensagem> objects) {
        super(c, 0, objects);
        this.context = c;
        this.mensagens = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        View view = null;

        if (mensagens != null){

            Preferencias preferencias = new Preferencias(context);
            String idUsuarioRemetente = preferencias.getIdentificador();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            Mensagem mensagem = mensagens.get(position);

            if (idUsuarioRemetente.equals(mensagem.getIdUsuario())){
                view = inflater.inflate(R.layout.item_mensagem_direita, parent, false);

            }else{
                view = inflater.inflate(R.layout.item_mensagem_esquerda, parent, false);

            }


            TextView textoMensagem = (TextView) view.findViewById(R.id.txt_mensagem);
            textoMensagem.setText(mensagem.getMensagem());
        }

        return  view;
    }

}
