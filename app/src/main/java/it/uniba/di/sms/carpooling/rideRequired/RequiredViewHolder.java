package it.uniba.di.sms.carpooling.rideRequired;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import it.uniba.di.sms.carpooling.R;

/**
 * Created by loiodice on 19/06/2018.
 */

public class RequiredViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public CardView cardView;
    public LinearLayout richiesti;
    public LinearLayout richiesti2;
    public RelativeLayout rich;
    public ImageView immagine;
    public TextView data,giorno,ora,casa,telefono,nome,cognome,status;
    public ArrayList<Map<String,Object>> passaggiRichiesti;
    public  Context ctx;

    public RequiredViewHolder(View itemView, Context ctx, ArrayList<Map<String,Object>> passaggiRichiesti) {
        super(itemView);
        this.passaggiRichiesti=passaggiRichiesti;
        this.ctx=ctx;

        itemView.setOnClickListener(this);

        cardView= (CardView) itemView.findViewById(R.id.cardviewRichiesti);
        richiesti = (LinearLayout) itemView.findViewById(R.id.LinearRichiesti);
        richiesti2 = (LinearLayout) itemView.findViewById(R.id.LinearRichiesti2);
        rich = (RelativeLayout) itemView.findViewById(R.id.relativeRichiesti);

        data = (TextView) itemView.findViewById(R.id.Data);
        giorno = (TextView) itemView.findViewById(R.id.Giorno);
        ora = (TextView) itemView.findViewById(R.id.Ora);
        casa = (TextView) itemView.findViewById(R.id.Casa);
        telefono = (TextView) itemView.findViewById(R.id.Telefono);
        nome = (TextView) itemView.findViewById(R.id.nomeAut);
        cognome = (TextView) itemView.findViewById(R.id.cognomeAut);
        immagine = (ImageView) itemView.findViewById(R.id.immagineProfilo);
        status = (TextView) itemView.findViewById(R.id.Status);
    }

    @Override
    public void onClick(View view) {
        int position = getAdapterPosition();
        Map<String,Object> psg= this.passaggiRichiesti.get(position);

    }}
