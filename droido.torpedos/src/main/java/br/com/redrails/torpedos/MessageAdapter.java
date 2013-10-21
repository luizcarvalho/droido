package br.com.redrails.torpedos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by luiz on 10/20/13.
 */

//DYNAMIC LOAD MESSAGES
class MessageAdapter extends ArrayAdapter<String> {
    LayoutInflater inflater;
    public ArrayList<String> messageArrayList = new ArrayList<String>();

    public MessageAdapter(Context context, int rowResourceId) {
        super(context, rowResourceId);

        inflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return messageArrayList.size();
    }

    @Override
    public String getItem(int position) {
        return messageArrayList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.row, null);
        TextView mensagem = (TextView) convertView.findViewById(R.id.mensagem);
        ImageView mensagemOption = (ImageView) convertView.findViewById(R.id.mensagem_option);
        mensagem.setText(messageArrayList.get(position).toString());
        final int message_position = position;


        mensagemOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.openMessageMenu(v);
                boolean a = true;
            }
        });

        return convertView;
    }
}
