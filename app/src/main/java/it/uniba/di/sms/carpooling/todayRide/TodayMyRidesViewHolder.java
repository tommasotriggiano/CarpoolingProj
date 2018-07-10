package it.uniba.di.sms.carpooling.todayRide;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import it.uniba.di.sms.carpooling.R;

/**
 * Created by loiodice on 09/07/2018.
 */

public class TodayMyRidesViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {

    public CircleImageView immagine;
    public TextView data,giorno,ora,casa,telefono,nome,cognome;
    public ArrayList<Map<String,Object>> todayRides;
    public CardView cardView;
    public Context ctx;
    public RelativeLayout listaPasseggeri;

    public TodayMyRidesViewHolder(View itemView, Context ctx, ArrayList<Map<String,Object>> todayRides){
        super(itemView);
        this.todayRides=todayRides;
        this.ctx=ctx;
        itemView.setOnClickListener(this);


        //listaPasseggeri=(RelativeLayout) itemView.findViewById(R.id.lista_passeggeri);
        cardView= (CardView) itemView.findViewById(R.id.cardviewToday);
        data = (TextView) itemView.findViewById(R.id.Data);
        giorno = (TextView) itemView.findViewById(R.id.Giorno);
        ora = (TextView) itemView.findViewById(R.id.Ora);
        casa = (TextView) itemView.findViewById(R.id.Casa);
        telefono = (TextView) itemView.findViewById(R.id.Telefono);
        nome = (TextView) itemView.findViewById(R.id.nomeAut);
        cognome = (TextView) itemView.findViewById(R.id.cognomeAut);
        immagine = (CircleImageView) itemView.findViewById(R.id.immagineProfilo);
       // status = (TextView) itemView.findViewById(R.id.Status);

    }

    @Override
    public void onClick(View view) {
        listaPasseggeri.setVisibility(View.VISIBLE);
    }
}
