package it.uniba.di.sms.carpooling;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;


import it.uniba.di.sms.carpooling.rideOffered.PassaggiViewHolder;


public class RankHistoryAdapter extends RecyclerView.Adapter<RankHistoryViewHolder> {
    private ArrayList<Map<String,Object>> itemHistoricalPoint;
    public Context context;
    FirebaseUser userRf = FirebaseAuth.getInstance().getCurrentUser();

    public RankHistoryAdapter(ArrayList<Map<String,Object>> itemHistoricalPoint, Context context){
        this.itemHistoricalPoint = itemHistoricalPoint;
        this.context = context;
    }

    @Override
    public RankHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historical_point, parent,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        RankHistoryViewHolder pnt = new RankHistoryViewHolder(layoutView,context,itemHistoricalPoint);
        return pnt;
    }

    @Override
    public void onBindViewHolder(final RankHistoryViewHolder holder, int position) {
        final Map<String,Object> storico_punti = itemHistoricalPoint.get(position);
        String idPassaggio = storico_punti.get("idPassaggio").toString();
        DocumentReference pass = FirebaseFirestore.getInstance().collection("Rides").document(idPassaggio);
        pass.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                Map<String,Object> passaggio = documentSnapshot.getData();
                holder.tvCasa.setText(passaggio.get("tipoViaggio").toString());
                holder.data.setText(passaggio.get("dataPassaggio").toString());
                holder.ora.setText(passaggio.get("ora").toString());}

            }
        });
        holder.punti.setText(storico_punti.get("punti").toString());

    }

    @Override
    public int getItemCount() {
        return itemHistoricalPoint.size();
    }
}
