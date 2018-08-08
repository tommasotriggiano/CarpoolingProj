package it.uniba.di.sms.carpooling.rideRequired;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import it.uniba.di.sms.carpooling.R;
import it.uniba.di.sms.carpooling.RequiredMapsActivity;


public class RequiredAdapter extends RecyclerView.Adapter<RequiredViewHolder>{
    private ArrayList<Map<String,Object>> itemRideRequired;
    private Context context;
    private String telefono;
    public RequiredAdapter(ArrayList<Map<String,Object>> itemRideRequired, Context context){
        this.itemRideRequired = itemRideRequired;
        this.context = context;
    }


    @Override
    public RequiredViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_required, parent,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        return new RequiredViewHolder(layoutView,context,itemRideRequired);
    }

    @Override
    public void onBindViewHolder(final RequiredViewHolder holder, final int position) {
        final Context context1 = holder.immagine.getContext();
        String idPassaggio = itemRideRequired.get(position).get("idPassaggio").toString();
        DocumentReference passaggio = FirebaseFirestore.getInstance().collection("Rides").document(idPassaggio);
        passaggio.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    Map<String,Object> passaggio = documentSnapshot.getData();

                    if(Locale.getDefault().getLanguage().equals("en")){
                        if(passaggio.get("tipoViaggio").toString().equals("Casa-Lavoro")){
                            holder.casa.setText(context.getResources().getString(R.string.HomeWork));
                        }
                        else if(passaggio.get("tipoViaggio").toString().equals("Lavoro-Casa")){
                            holder.casa.setText(context.getResources().getString(R.string.WorkHome));
                        }
                        String status = itemRideRequired.get(position).get("status").toString();
                        switch (status) {
                            case "IN ATTESA":
                                holder.status.setText(context.getResources().getString(R.string.wait));
                                break;
                            case "CONFERMATO":
                                holder.status.setText(context.getResources().getString(R.string.accepted));
                                break;
                            case "RIFIUTATO":
                                holder.status.setText(context.getResources().getString(R.string.refused));
                                break;
                        }
                        String dp = passaggio.get("dataPassaggio").toString();
                        String d[] = dp.split("-");
                        String dataPassaggio = d[1]+"-"+d[0]+"-"+d[2];
                        holder.data.setText(dataPassaggio);
                    }
                    else{
                        holder.data.setText((String)passaggio.get("dataPassaggio"));
                       String status= itemRideRequired.get(position).get("status").toString();
                        switch (status) {
                            case "IN ATTESA":
                                holder.status.setText(context.getResources().getString(R.string.wait));
                                break;
                            case "CONFERMATO":
                                holder.status.setText(context.getResources().getString(R.string.accepted));
                                break;
                            case "RIFIUTATO":
                                holder.status.setText(context.getResources().getString(R.string.refused));
                                break;
                        }
                        holder.casa.setText(passaggio.get("tipoViaggio").toString());}
                    holder.giorno.setText((String)passaggio.get("giorno"));
                    holder.ora.setText((String)passaggio.get("ora"));
                    Map<String,Object> autista = (Map<String, Object>) passaggio.get("autista");
                    holder.telefono.setText((String)autista.get("phone"));
                    holder.telefono.setPaintFlags(holder.telefono.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                    telefono=autista.get("phone").toString();
                    holder.cognome.setText((String)autista.get("surname"));
                    holder.nome.setText((String)autista.get("name"));

                    if(autista.get("urlProfileImage") != null){
                        Picasso.with(context1).load(autista.get("urlProfileImage").toString()).into(holder.immagine);
                    }else {
                        holder.immagine.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_image_profile));
                    }
                }
            }
        });

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mapRequired = new Intent(context, RequiredMapsActivity.class);
                    Serializable psg = (Serializable) itemRideRequired.get(position);
                    mapRequired.putExtra("richiestaPassaggio",psg);
                    context.startActivity(mapRequired);

            }
        });


            holder.telefono.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  AlertDialog.Builder builder= new AlertDialog.Builder(context);
                  builder.setNeutralButton(context.getResources().getString(R.string.callPhone),new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialogInterface, int i) {
                                  Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                                  dialIntent.setData(Uri.parse("tel:" + telefono));
                                  context.startActivity(dialIntent);
                              }
                  });
                  builder.show();}

            });

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
