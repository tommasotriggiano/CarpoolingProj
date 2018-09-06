package it.uniba.di.sms.carpooling.rideRequired;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import it.uniba.di.sms.carpooling.MapsActivity;
import it.uniba.di.sms.carpooling.R;
import it.uniba.di.sms.carpooling.RequiredMapsActivity;

/**
 * Created by loiodice on 19/06/2018.
 */

public class RequiredViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public CardView cardView;
    public LinearLayout viewForeground;
    public LinearLayout richiesti2;
    public RelativeLayout rich;
    public CircleImageView immagine;
    public TextView data,giorno,ora,casa,telefono,nome,cognome,status;
    public ArrayList<Map<String,Object>> passaggiRichiesti;
    public Context ctx;
    public MapView mapRequired;

    public RequiredViewHolder(View itemView, Context ctx, ArrayList<Map<String,Object>> passaggiRichiesti) {
        super(itemView);
        this.passaggiRichiesti=passaggiRichiesti;
        this.ctx=ctx;

        itemView.setOnClickListener(this);

        cardView= (CardView) itemView.findViewById(R.id.cardviewRichiesti);
        viewForeground = (LinearLayout) itemView.findViewById(R.id.LinearRichiesti);
        richiesti2 = (LinearLayout) itemView.findViewById(R.id.LinearRichiesti2);
        rich = (RelativeLayout) itemView.findViewById(R.id.relativeRichiesti);

        data = (TextView) itemView.findViewById(R.id.Data);
        giorno = (TextView) itemView.findViewById(R.id.Giorno);
        ora = (TextView) itemView.findViewById(R.id.Ora);
        casa = (TextView) itemView.findViewById(R.id.Casa);
        telefono = (TextView) itemView.findViewById(R.id.Telefono);
        nome = (TextView) itemView.findViewById(R.id.nomeAut);
        cognome = (TextView) itemView.findViewById(R.id.cognomeAut);
        immagine = (CircleImageView) itemView.findViewById(R.id.immagineProfilo);
        status = (TextView) itemView.findViewById(R.id.Status);
    }

    @Override
    public void onClick(View view) {
        Context ctxCardView = cardView.getContext();
        int position = getAdapterPosition();
        Serializable psg = (Serializable)this.passaggiRichiesti.get(position);
        Intent mapRequired = new Intent(ctx,RequiredMapsActivity.class);
        mapRequired.putExtra("richiestaPassaggio",psg);
        ctxCardView.startActivity(mapRequired);



    }}
