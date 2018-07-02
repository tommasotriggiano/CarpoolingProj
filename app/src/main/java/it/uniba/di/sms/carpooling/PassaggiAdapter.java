package it.uniba.di.sms.carpooling;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;


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
        holder.onClick(holder.cardView);

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
