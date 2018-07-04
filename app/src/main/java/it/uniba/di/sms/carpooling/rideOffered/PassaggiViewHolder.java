package it.uniba.di.sms.carpooling.rideOffered;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

import it.uniba.di.sms.carpooling.R;

/**
 * Created by tommaso on 13/06/2018.
 */

public class PassaggiViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public RelativeLayout viewBackground;
    public LinearLayout viewForeground;
    public CardView cardView;
    public TextView data;
    public TextView giorno;
    public TextView ora;
    public TextView casa;
    public TextView postiOccupati;
    public ArrayList<Map<String,Object>> passaggi;
    public  Context ctx;

    String data1;
    public PassaggiViewHolder(View itemView, Context ctx, final ArrayList<Map<String,Object>> passaggi) {
        super(itemView);
        this.passaggi=passaggi;
        this.ctx=ctx;
        itemView.setOnClickListener(this);

        viewForeground=(LinearLayout)itemView.findViewById(R.id.viewForeground);
        viewBackground=(RelativeLayout) itemView.findViewById(R.id.viewBackground) ;
        cardView= (CardView) itemView.findViewById(R.id.cardview);
        data = (TextView) itemView.findViewById(R.id.Data);
        giorno = (TextView) itemView.findViewById(R.id.Giorno);
        ora = (TextView) itemView.findViewById(R.id.Ora);
        casa = (TextView) itemView.findViewById(R.id.casa);
        postiOccupati = (TextView) itemView.findViewById(R.id.postiOcc);
    }


    @Override
    public void onClick(View view) {
        int position = getAdapterPosition();
        Map<String,Object> psg= (Map<String,Object>)this.passaggi.get(position).get("autista");
       
    }
}
