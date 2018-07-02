package it.uniba.di.sms.carpooling.rideRequired;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;


import it.uniba.di.sms.carpooling.R;

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
    public void onBindViewHolder(RequiredViewHolder holder, int position) {

        final Context context1 = holder.immagine.getContext();
        Map<String,Object> passaggio = (Map<String,Object>) itemRideRequired.get(position).get("passaggio");
        holder.data.setText((String)passaggio.get("data"));
        holder.giorno.setText((String)passaggio.get("giorno"));
        holder.ora.setText((String)passaggio.get("ora"));
        holder.casa.setText((String)passaggio.get("tipoViaggio"));
        Map<String,Object> autista = (Map<String,Object>) itemRideRequired.get(position).get("autista");
        holder.telefono.setText((String)autista.get("phone"));
        holder.cognome.setText((String)autista.get("surname"));
        holder.nome.setText((String)autista.get("name"));
        final Map<String,Object> address = (Map<String,Object>)autista.get("userAddress");
        if((boolean)itemRideRequired.get(position).get("accepted")){
            holder.status.setText(R.string.status1);
        }
        else{
            holder.status.setText(R.string.status2);
        }
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
