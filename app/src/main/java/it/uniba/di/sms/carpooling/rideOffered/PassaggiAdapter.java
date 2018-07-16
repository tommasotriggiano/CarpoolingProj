package it.uniba.di.sms.carpooling.rideOffered;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import it.uniba.di.sms.carpooling.OfferedMapActivity;
import it.uniba.di.sms.carpooling.R;
import it.uniba.di.sms.carpooling.rideOffered.PassaggiViewHolder;


public class PassaggiAdapter extends RecyclerView.Adapter<PassaggiViewHolder> {
    private ArrayList<Map<String,Object>> itemPassaggi;
    public Context context;

    public PassaggiAdapter(ArrayList<Map<String,Object>> itemPassaggi, Context context){
        this.itemPassaggi = itemPassaggi;
        this.context = context;
    }


    @Override
    public PassaggiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offered, parent,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        PassaggiViewHolder psg = new PassaggiViewHolder(layoutView,context,itemPassaggi);
        return psg;
    }

    @Override
    public void onBindViewHolder(PassaggiViewHolder holder, final int position) {
        if(Locale.getDefault().getLanguage().equals("en")){
            if(itemPassaggi.get(position).get("tipoViaggio").toString().equals("Casa-Lavoro")){
                holder.casa.setText(context.getResources().getString(R.string.HomeWork));
            }
            else if(itemPassaggi.get(position).get("tipoViaggio").toString().equals("Lavoro-Casa")){
                holder.casa.setText(context.getResources().getString(R.string.WorkHome));
            }
            String dp = itemPassaggi.get(position).get("dataPassaggio").toString();
            String d[] = dp.split("-");
            String dataPassaggio = d[1]+"-"+d[0]+"-"+d[2];
            holder.data.setText(dataPassaggio);
        }
        else{
            holder.data.setText((String)itemPassaggi.get(position).get("dataPassaggio"));
            holder.casa.setText((String)itemPassaggi.get(position).get("tipoViaggio"));
        }
        holder.giorno.setText((String)itemPassaggi.get(position).get("giorno"));
        holder.ora.setText((String)itemPassaggi.get(position).get("ora"));
        Long occupati=(Long)itemPassaggi.get(position).get("postiOccupati");
        Long disponibili=(Long)itemPassaggi.get(position).get("postiDisponibili");
        if(occupati != null && disponibili != null){
        Long sum = occupati+disponibili;
        holder.postiOccupati.setText(occupati.toString()+"/"+sum);}
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent offeredMap = new Intent(context, OfferedMapActivity.class);
                Serializable psg= (Serializable)itemPassaggi.get(position);
                offeredMap.putExtra("passaggio",psg);
                context.startActivity(offeredMap);

            }
        });

    }
    public void removeItem(int position){
        itemPassaggi.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Map<String,Object> item,int position){
        itemPassaggi.add(position,item);
        notifyItemInserted(position);
    }

    @Override
    public int getItemCount() {
        return itemPassaggi.size();
    }
}
