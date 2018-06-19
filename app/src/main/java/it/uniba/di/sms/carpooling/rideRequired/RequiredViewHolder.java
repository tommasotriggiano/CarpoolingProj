package it.uniba.di.sms.carpooling.rideRequired;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import it.uniba.di.sms.carpooling.R;

/**
 * Created by loiodice on 19/06/2018.
 */

public class RequiredViewHolder extends RecyclerView.ViewHolder {
    public CardView cardView;
    public ImageView immagine;
    public TextView data,giorno,ora,casa,telefono,nome,cognome,status;
    public RequiredViewHolder(View itemView) {
        super(itemView);
        cardView= (CardView) itemView.findViewById(R.id.cardview);
        data = (TextView) itemView.findViewById(R.id.Data);
        giorno = (TextView) itemView.findViewById(R.id.Giorno);
        ora = (TextView) itemView.findViewById(R.id.Ora);
        casa = (TextView) itemView.findViewById(R.id.Casa);
        telefono = (TextView) itemView.findViewById(R.id.Telefono);
        nome = (TextView) itemView.findViewById(R.id.nomeAut);
        cognome = (TextView) itemView.findViewById(R.id.cognomeAut);
        immagine = (ImageView) itemView.findViewById(R.id.immagineProfilo);
    }
}
