package it.uniba.di.sms.carpooling;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by tommaso on 13/06/2018.
 */

public class PassaggiViewHolder extends RecyclerView.ViewHolder  {

    public TextView data;
    public TextView giorno;
    public TextView ora;
    public TextView casa;
    public TextView lavoro;
    public TextView postiOccupati;

    public PassaggiViewHolder(View itemView) {
        super(itemView);
        data = (TextView) itemView.findViewById(R.id.Data);
        giorno = (TextView) itemView.findViewById(R.id.Giorno);
        ora = (TextView) itemView.findViewById(R.id.Ora);
        casa = (TextView) itemView.findViewById(R.id.casa);
        lavoro = (TextView) itemView.findViewById(R.id.lavoro);
        postiOccupati = (TextView) itemView.findViewById(R.id.postiOcc);
    }


}
