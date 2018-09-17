package it.uniba.di.sms.carpooling;


import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class RankHistoryViewHolder extends RecyclerView.ViewHolder {
    public ConstraintLayout constraintlayout;
    public CardView cardView;
    public LinearLayout linearLayout;
    public TextView tvCasa,tvLavoro,data,ora,punti;

    public ArrayList<Map<String,Object>> storico;
    public Context ctx;
    public RankHistoryViewHolder(View itemView, Context ctx, final ArrayList<Map<String,Object>> storico) {
        super(itemView);
        this.storico=storico;
        this.ctx=ctx;
        constraintlayout = (ConstraintLayout) itemView.findViewById(R.id.constraint);
        cardView = (CardView)itemView.findViewById(R.id.item_historical_point);
        linearLayout = (LinearLayout)itemView.findViewById(R.id.layout_casaLavoro);
        tvCasa = (TextView)itemView.findViewById(R.id.casa);
        tvLavoro = (TextView)itemView.findViewById(R.id.lavoro);
        data = (TextView)itemView.findViewById(R.id.data);
        ora = (TextView)itemView.findViewById(R.id.ora);
        punti = (TextView)itemView.findViewById(R.id.nPunti);
    }


}
