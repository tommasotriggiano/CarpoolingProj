package it.uniba.di.sms.carpooling.todayRide;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;

import it.uniba.di.sms.carpooling.R;
import it.uniba.di.sms.carpooling.rideRequired.RequiredViewHolder;

/**
 * Created by loiodice on 09/07/2018.
 */

public class TodayMyRidesAdapter extends RecyclerView.Adapter<TodayMyRidesViewHolder> {
    private ArrayList<Map<String,Object>> itemTodayRide;
    private Context context;


    public TodayMyRidesAdapter(ArrayList<Map<String,Object>> itemTodayRide, Context context){
        this.itemTodayRide = itemTodayRide;
        this.context = context;
    }
    @Override
    public TodayMyRidesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_today_rides, parent,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        TodayMyRidesViewHolder ride = new TodayMyRidesViewHolder(layoutView,context,itemTodayRide);
        return ride;
    }

    @Override
    public void onBindViewHolder(TodayMyRidesViewHolder holder, int position) {
        final Context context1 = holder.immagine.getContext();

        Map<String,Object> passaggio = (Map<String,Object>) itemTodayRide.get(position).get("passaggio");
        holder.data.setText((String)passaggio.get("dataPassaggio"));
        holder.giorno.setText((String)passaggio.get("giorno"));
        holder.ora.setText((String)passaggio.get("ora"));
        holder.casa.setText((String)passaggio.get("tipoViaggio"));
        Map<String,Object> autista = (Map<String,Object>) itemTodayRide.get(position).get("autista");
        holder.telefono.setText((String)autista.get("phone"));
        holder.cognome.setText((String)autista.get("surname"));
        holder.nome.setText((String)autista.get("name"));
       // holder.status.setText((String)itemTodayRide.get(position).get("status"));
        if(autista.get("urlProfileImage") != null){
            Picasso.with(context1).load(autista.get("urlProfileImage").toString()).into(holder.immagine);}

    }

    @Override
    public int getItemCount() {
        return itemTodayRide.size();
    }
}
