package it.uniba.di.sms.carpooling.rideOffered;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

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
    CollectionReference rideRequests;

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
    public void onBindViewHolder(final PassaggiViewHolder holder, final int position) {
        final Map<String,Object> passaggio = itemPassaggi.get(position);
        if(Locale.getDefault().getLanguage().equals("en")){
            if(passaggio.get("tipoViaggio").toString().equals("Casa-Lavoro")){
                holder.casa.setText(context.getResources().getString(R.string.HomeWork));
            }
            else if(passaggio.get("tipoViaggio").toString().equals("Lavoro-Casa")){
                holder.casa.setText(context.getResources().getString(R.string.WorkHome));
            }
            String dp = passaggio.get("dataPassaggio").toString();
            String d[] = dp.split("-");
            String dataPassaggio = d[1]+"-"+d[0]+"-"+d[2];
            holder.data.setText(dataPassaggio);
        }
        else{
            holder.data.setText((String)passaggio.get("dataPassaggio"));
            holder.casa.setText((String)passaggio.get("tipoViaggio"));
        }
        holder.giorno.setText((String)passaggio.get("giorno"));
        holder.ora.setText((String)passaggio.get("ora"));
        Long occupati=(Long)passaggio.get("postiOccupati");
        Long disponibili=(Long)passaggio.get("postiDisponibili");
        if(occupati != null && disponibili != null){
        Long sum = occupati+disponibili;
        holder.postiOccupati.setText(occupati.toString()+"/"+sum);}
        rideRequests = FirebaseFirestore.getInstance().collection("RideRequests");
        String idPassaggio = passaggio.get("idPassaggio").toString();
        Query numberRequest = rideRequests.whereEqualTo("idPassaggio",idPassaggio);
        numberRequest.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                int count = 0;
                for(DocumentSnapshot d : documentSnapshots.getDocuments()){
                    String status = d.getData().get("status").toString();
                    if(status.equals("IN ATTESA")){
                        count++;
                    }
                }
                if(count == 0 ){
                    if (holder.badge.getVisibility() != View.GONE) {
                        holder.badge.setVisibility(View.GONE);
                    }
                }
                else{
                    holder.badge.setText(String.valueOf(count));
                    if (holder.badge.getVisibility() != View.VISIBLE) {
                        holder.badge.setVisibility(View.VISIBLE);
                    }
                }

            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent offeredMap = new Intent(context, OfferedMapActivity.class);
                Serializable psg= (Serializable)passaggio;
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
