package it.uniba.di.sms.carpooling.rideOffered;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Map;

import it.uniba.di.sms.carpooling.R;
import it.uniba.di.sms.carpooling.rideOffered.PassaggiViewHolder;


public class PassaggiAdapter extends RecyclerView.Adapter<PassaggiViewHolder> {
    private ArrayList<Map<String,Object>> itemPassaggi;
    private Context context;

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
    public void onBindViewHolder(PassaggiViewHolder holder, int position) {

        holder.data.setText((String)itemPassaggi.get(position).get("dataPassaggio"));
        holder.giorno.setText((String)itemPassaggi.get(position).get("giorno"));
        holder.ora.setText((String)itemPassaggi.get(position).get("ora"));
        holder.casa.setText((String)itemPassaggi.get(position).get("tipoViaggio"));
        Object disponibili =itemPassaggi.get(position).get("postiDisponibili");
        holder.postiOccupati.setText("0"+"/"+disponibili.toString());

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
