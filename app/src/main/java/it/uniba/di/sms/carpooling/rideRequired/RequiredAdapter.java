package it.uniba.di.sms.carpooling.rideRequired;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import it.uniba.di.sms.carpooling.PassaggiViewHolder;
import it.uniba.di.sms.carpooling.Passaggio;
import it.uniba.di.sms.carpooling.R;

/**
 * Created by loiodice on 19/06/2018.
 */

public class RequiredAdapter extends RecyclerView.Adapter<RequiredViewHolder>{
    private ArrayList<Passaggio> itemRideRequired;
    private Context context;

    public RequiredAdapter(ArrayList<Passaggio> itemRideRequired, Context context){
        this.itemRideRequired = itemRideRequired;
        this.context = context;
    }


    @Override
    public RequiredViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_required, parent,false);
        //RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        //layoutView.setLayoutParams(lp);
        RequiredViewHolder psg = new RequiredViewHolder(layoutView);
        return psg;
    }

    @Override
    public void onBindViewHolder(RequiredViewHolder holder, int position) {
        // modificare questo metodo
        /*holder.data.setText(itemRideRequired.get(position).getDataPassaggio());
        holder.giorno.setText(itemRideRequired.get(position).getGiorno());
        holder.ora.setText(itemRideRequired.get(position).getOra());
        holder.casa.setText(itemRideRequired.get(position).getTipoViaggio());*/
        // inserire gli altri campi

    }


    @Override
    public int getItemCount() {
        return itemRideRequired.size();
    }


}
