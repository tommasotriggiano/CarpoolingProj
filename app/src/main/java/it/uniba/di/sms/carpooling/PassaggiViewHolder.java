package it.uniba.di.sms.carpooling;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by tommaso on 13/06/2018.
 */

public class PassaggiViewHolder extends RecyclerView.ViewHolder  {
    public RelativeLayout viewBackground;
    public LinearLayout viewForeground;
    public CardView cardView;
    public TextView data;
    public TextView giorno;
    public TextView ora;
    public TextView casa;
    public TextView lavoro;
    public TextView postiOccupati;

    public PassaggiViewHolder(View itemView) {
        super(itemView);
        viewForeground=(LinearLayout)itemView.findViewById(R.id.viewForeground);
        viewBackground=(RelativeLayout) itemView.findViewById(R.id.viewBackground) ;
        cardView= (CardView) itemView.findViewById(R.id.cardview);
        data = (TextView) itemView.findViewById(R.id.Data);
        giorno = (TextView) itemView.findViewById(R.id.Giorno);
        ora = (TextView) itemView.findViewById(R.id.Ora);
        casa = (TextView) itemView.findViewById(R.id.casa);
        postiOccupati = (TextView) itemView.findViewById(R.id.postiOcc);
    }


}
