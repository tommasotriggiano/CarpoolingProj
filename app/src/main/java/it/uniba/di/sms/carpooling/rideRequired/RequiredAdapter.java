package it.uniba.di.sms.carpooling.rideRequired;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;


import it.uniba.di.sms.carpooling.MainActivity;
import it.uniba.di.sms.carpooling.MapsActivity;
import it.uniba.di.sms.carpooling.R;
import it.uniba.di.sms.carpooling.RequiredMapsActivity;

import static android.content.ContentValues.TAG;

/**
 * Created by loiodice on 19/06/2018.
 */

public class RequiredAdapter extends RecyclerView.Adapter<RequiredViewHolder>{
    private ArrayList<Map<String,Object>> itemRideRequired;
    private Context context;

    public RequiredAdapter(ArrayList<Map<String,Object>> itemRideRequired, Context context){
        this.itemRideRequired = itemRideRequired;
        this.context = context;
    }


    @Override
    public RequiredViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_required, parent,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        RequiredViewHolder psg = new RequiredViewHolder(layoutView,context,itemRideRequired);
        return psg;
    }

    @Override
    public void onBindViewHolder(RequiredViewHolder holder, final int position) {

        final Context context1 = holder.immagine.getContext();

        Map<String,Object> passaggio = (Map<String,Object>) itemRideRequired.get(position).get("passaggio");

        if(Locale.getDefault().getLanguage().equals("en")){
            if(passaggio.get("tipoViaggio").toString().equals("Casa-Lavoro")){
                holder.casa.setText(context.getResources().getString(R.string.HomeWork));
            }
            else if(passaggio.get("tipoViaggio").toString().equals("Lavoro-Casa")){
                holder.casa.setText(context.getResources().getString(R.string.WorkHome));
            }
            String status = itemRideRequired.get(position).get("status").toString();
            switch (status) {
                case "IN ATTESA":
                    holder.status.setText(context.getResources().getString(R.string.wait));
                    break;
                case "CONFERMATO":
                    holder.status.setText(context.getResources().getString(R.string.accepted));
                    break;
                case "RIFIUTATO":
                    holder.status.setText(context.getResources().getString(R.string.refused));
                    break;
            }
            String dp = passaggio.get("data").toString();
            String d[] = dp.split("-");
            String dataPassaggio = d[1]+"-"+d[0]+"-"+d[2];
            holder.data.setText(dataPassaggio);
        }
        else{
        holder.data.setText((String)passaggio.get("data"));
        holder.status.setText((String)itemRideRequired.get(position).get("status"));
        holder.casa.setText(passaggio.get("tipoViaggio").toString());}
        holder.giorno.setText((String)passaggio.get("giorno"));
        holder.ora.setText((String)passaggio.get("ora"));
        Map<String,Object> autista = (Map<String,Object>) itemRideRequired.get(position).get("autista");

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapRequired = new Intent(context, RequiredMapsActivity.class);
                Serializable psg = (Serializable) itemRideRequired.get(position);
                mapRequired.putExtra("richiestaPassaggio",psg);
                context.startActivity(mapRequired);
            }
        });

        holder.telefono.setText((String)autista.get("phone"));
        holder.cognome.setText((String)autista.get("surname"));
        holder.nome.setText((String)autista.get("name"));
        if(autista.get("urlProfileImage") != null){
            Picasso.with(context1).load(autista.get("urlProfileImage").toString()).into(holder.immagine);}
    }


    public void removeItem(int position){
        itemRideRequired.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Map<String,Object> item,int position){
        itemRideRequired.add(position,item);
        notifyItemInserted(position);
    }

    @Override
    public int getItemCount() {
        return itemRideRequired.size();
    }


}
