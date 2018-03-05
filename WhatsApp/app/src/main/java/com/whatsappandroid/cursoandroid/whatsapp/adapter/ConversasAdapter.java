package com.whatsappandroid.cursoandroid.whatsapp.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.whatsappandroid.cursoandroid.whatsapp.R;
import com.whatsappandroid.cursoandroid.whatsapp.model.Conversa;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 25/11/2017.
 */

public class ConversasAdapter extends ArrayAdapter{

    private ArrayList<Conversa> conversas;
    private Context context;

    public ConversasAdapter(Context c, ArrayList<Conversa> objects) {
        super(c, 0, objects);
        this.context = c;
        this.conversas = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = null;

        if (conversas != null){

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.lista_conversa, parent, false);

            TextView nome = (TextView) view.findViewById(R.id.txt_titulo);
            TextView ultimaMensagem = (TextView) view.findViewById(R.id.txt_subtitulo);
            Conversa conversa = conversas.get(position);
            nome.setText(conversa.getNome());
            ultimaMensagem.setText(conversa.getMensagem());


        }

        return view;
    }
}
