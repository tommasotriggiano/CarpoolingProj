package it.uniba.di.sms.carpooling.todayRide;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
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
    public TextView data,giorno,ora,casa,telefono,nome,cognome,driver,passengers;
    public ArrayList<Map<String,Object>> todayRides;
    public CardView cardView,cardView2;
    public Context ctx;
    public RelativeLayout listaPasseggeri,rel2;
    public LinearLayout linearLayout1,linearLayout2,lin;
    public RelativeLayout linear;
    public ListView listView;
    public Button startTracking;

    public TodayMyRidesViewHolder(View itemView, Context ctx, ArrayList<Map<String,Object>> todayRides){
        super(itemView);
        this.todayRides=todayRides;
        this.ctx=ctx;
        itemView.setOnClickListener(this);


        listaPasseggeri=(RelativeLayout) itemView.findViewById(R.id.relativeToday);
        linearLayout1 = (LinearLayout) itemView.findViewById(R.id.LinearToday);
        linearLayout2 = (LinearLayout) itemView.findViewById(R.id.LinearToday2);
        linear = (RelativeLayout) itemView.findViewById(R.id.linear);
        rel2 = (RelativeLayout) itemView.findViewById(R.id.rel2);
        cardView= (CardView) itemView.findViewById(R.id.cardviewToday);
        cardView2= (CardView) itemView.findViewById(R.id.card2);
        startTracking=(Button) itemView.findViewById(R.id.startTracking);

        data = (TextView) itemView.findViewById(R.id.Data);
        giorno = (TextView) itemView.findViewById(R.id.Giorno);
        ora = (TextView) itemView.findViewById(R.id.Ora);
        casa = (TextView) itemView.findViewById(R.id.Casa);
        driver = (TextView) itemView.findViewById(R.id.driver);
        passengers = (TextView) itemView.findViewById(R.id.passeggeri);
        immagine = (CircleImageView) itemView.findViewById(R.id.immagineProfiloPassegero);
        nome = (TextView) itemView.findViewById(R.id.nomePass);
        cognome = (TextView) itemView.findViewById(R.id.cognomePass);
        telefono = (TextView) itemView.findViewById(R.id.telefono);
        listView = (ListView)itemView.findViewById(R.id.listView);

    }

    @Override
    public void onClick(View view) {
        listaPasseggeri.setVisibility(View.VISIBLE);
    }
}
