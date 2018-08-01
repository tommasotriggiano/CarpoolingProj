package it.uniba.di.sms.carpooling.todayRide;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import it.uniba.di.sms.carpooling.PassengerAdapter;
import it.uniba.di.sms.carpooling.Passeggero;
import it.uniba.di.sms.carpooling.R;

/**
 * Created by loiodice on 09/07/2018.
 */

public class TodayMyRidesAdapter extends RecyclerView.Adapter<TodayMyRidesViewHolder> {
    private ArrayList<Map<String,Object>> itemTodayRide;
    private Context context;
    private ArrayList passenger;

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
    public void onBindViewHolder(final TodayMyRidesViewHolder holder, final int position) {
        Context context1 = holder.immagine.getContext();
        Map<String,Object> passaggio = itemTodayRide.get(position);
        holder.data.setText((String)passaggio.get("dataPassaggio"));
        holder.giorno.setText((String)passaggio.get("giorno"));
        holder.ora.setText((String)passaggio.get("ora"));
        holder.casa.setText((String)passaggio.get("tipoViaggio"));

        Map<String,Object> autista = (Map<String,Object>) passaggio.get("autista");
        holder.telefono.setText((String)autista.get("phone"));
        holder.cognome.setText((String)autista.get("surname"));
        holder.nome.setText((String)autista.get("name"));
        if(autista.get("urlProfileImage") != null){
            Picasso.with(context1).load(autista.get("urlProfileImage").toString()).into(holder.immagine);}


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.cardView2.getVisibility() == View.GONE && holder.listView.getVisibility() == View.GONE){
                    TransitionManager.beginDelayedTransition(holder.cardView2);
                    holder.cardView2.setVisibility(View.VISIBLE);
                    if(itemTodayRide.get(position).get("passeggeri") != null){
                    datiPasseggeri(position,holder.listView);
                    holder.passengers.setVisibility(View.VISIBLE);
                    holder.listView.setVisibility(View.VISIBLE);}
                    }
                else if(holder.cardView2.getVisibility() == View.VISIBLE && holder.listView.getVisibility() == View.VISIBLE){
                    TransitionManager.beginDelayedTransition(holder.cardView2);
                    holder.cardView2.setVisibility(View.GONE);
                    if(itemTodayRide.get(position).get("passeggeri") != null){
                        holder.passengers.setVisibility(View.GONE);
                    holder.listView.setVisibility(View.GONE);}
                }
            }
        });

    }

    private void datiPasseggeri(int position, final ListView listView) {
        passenger = new ArrayList<Passeggero>();
        if(itemTodayRide.get(position).get("passeggeri") != null){
        final Map<String,Object> passeggeri = (Map<String,Object>) itemTodayRide.get(position).get("passeggeri");
            for(String key: passeggeri.keySet()){
            DocumentReference pass = FirebaseFirestore.getInstance().collection("Users").document(key);
                pass.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Map<String,Object> map = documentSnapshot.getData();
                    String nome = map.get("name").toString();
                    String cognome = map.get("surname").toString();
                    String telefono = map.get("phone").toString();
                    String urlProfile = null;
                    if(map.get("urlProfileImage") != null){
                    urlProfile = map.get("urlProfileImage").toString();}
                    Passeggero psg = new Passeggero(nome,cognome,telefono,urlProfile);
                    passenger.add(psg);
                    PassengerAdapter passengerAdapter = new PassengerAdapter(context,R.layout.list_passenger,passenger);
                    listView.setAdapter(passengerAdapter);
                }
            });
            }
    }
    }


    @Override
    public int getItemCount() {
        return itemTodayRide.size();
    }
}
