package it.uniba.di.sms.carpooling;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;


public class PassaggiAdapter extends RecyclerView.Adapter<PassaggiViewHolder> {
    private ArrayList<Passaggio> itemPassaggi;
    private Context context;

    public PassaggiAdapter(ArrayList<Passaggio> itemPassaggi, Context context){
        this.itemPassaggi = itemPassaggi;
        this.context = context;
    }


    @Override
    public PassaggiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offered, parent,false);
        //RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        //layoutView.setLayoutParams(lp);
        PassaggiViewHolder psg = new PassaggiViewHolder(layoutView);
        return psg;
    }

    @Override
    public void onBindViewHolder(PassaggiViewHolder holder, int position) {

        holder.data.setText(itemPassaggi.get(position).getDataPassaggio());
        holder.giorno.setText(itemPassaggi.get(position).getGiorno());
        holder.ora.setText(itemPassaggi.get(position).getOra());
        holder.casa.setText(itemPassaggi.get(position).getTipoViaggio());
        // inserire il numero dei posti occupati

    }
    public void removeItem(int position){
        itemPassaggi.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Passaggio item,int position){
        itemPassaggi.add(position,item);
        notifyItemInserted(position);
    }

    @Override
    public int getItemCount() {
        return itemPassaggi.size();
    }
}
