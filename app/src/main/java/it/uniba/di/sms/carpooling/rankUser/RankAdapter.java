package it.uniba.di.sms.carpooling.rankUser;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import it.uniba.di.sms.carpooling.R;

public class RankAdapter extends RecyclerView.Adapter<RankViewHolder>  {
    private ArrayList<Map<String,Object>> classifica;
    public Context context;

    public RankAdapter(ArrayList<Map<String,Object>> classifica, Context context){
        this.classifica = classifica;
        this.context = context;
    }

    @Override
    public RankViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rank_point, parent,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        RankViewHolder pnt = new RankViewHolder(layoutView,context,classifica);
        return pnt;
    }

    @Override
    public void onBindViewHolder(final RankViewHolder holder, int position) {
        Context context1 = holder.circleImageView.getContext();
        final Map<String,Object> itemClassifica = classifica.get(position);
        holder.punti.setText(itemClassifica.get("punti").toString());
        String nomeCognome = itemClassifica.get("name").toString() + " "+itemClassifica.get("surname").toString();
        holder.nomeCognome.setText(nomeCognome);
        if(itemClassifica.get("urlProfileImage")!= null){
            Picasso.with(context1).load(itemClassifica.get("urlProfileImage").toString()).into(holder.circleImageView);
        }


    }

    @Override
    public int getItemCount() {
        return classifica.size();
    }
}
