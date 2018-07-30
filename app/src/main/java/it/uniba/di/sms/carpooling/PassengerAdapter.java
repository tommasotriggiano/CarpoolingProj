package it.uniba.di.sms.carpooling;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by tommaso on 24/07/2018
 */

public class PassengerAdapter extends ArrayAdapter<Passeggero> {
    private ArrayList passenger;
    private Context context;
    int layoutResId;

    public PassengerAdapter(@NonNull Context context, int layoutResId, @NonNull ArrayList<Passeggero> passenger) {
        super(context, layoutResId, passenger);
        this.layoutResId = layoutResId;
        this.context = context;
        this.passenger = passenger;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PassengerHolder holder = null;

        if(convertView == null){
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_passenger, parent, false);
        holder = new PassengerHolder();

        holder.nome = (TextView) convertView.findViewById(R.id.nomePass);
        holder.cognome = (TextView) convertView.findViewById(R.id.cognomePass);
        holder.telefono = (TextView)convertView.findViewById(R.id.telefono);
        holder.profile = (CircleImageView) convertView.findViewById(R.id.immagineProfiloPassegero);
        convertView.setTag(holder);
        }
        else
        {
            holder = (PassengerHolder) convertView.getTag();
        }
        Context ctx = holder.profile.getContext();
        Passeggero psg = (Passeggero) passenger.get(position);
        holder.nome.setText(psg.name);
        holder.cognome.setText(psg.surname);
        holder.telefono.setText(psg.phone);
        if(((Passeggero) passenger.get(position)).urlImage != null){
            Picasso.with(ctx).load(((Passeggero) passenger.get(position)).urlImage).into(holder.profile);
        }

        return convertView;
    }

    static class PassengerHolder{
        public TextView nome,cognome,telefono;
        public CircleImageView profile;

    }


}
