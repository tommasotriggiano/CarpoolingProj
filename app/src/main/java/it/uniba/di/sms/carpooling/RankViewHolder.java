package it.uniba.di.sms.carpooling;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class RankViewHolder extends RecyclerView.ViewHolder {
    public ConstraintLayout constraintlayout;
    public CardView cardView;
    public LinearLayout linearLayout;
    public TextView nomeCognome,punti;
    public CircleImageView circleImageView;

    public ArrayList<Map<String,Object>> classifica;
    public Context ctx;
    public RankViewHolder(View itemView, Context ctx, final ArrayList<Map<String,Object>> classifica) {
        super(itemView);
        this.classifica = classifica;
        this.ctx = ctx;
        constraintlayout = (ConstraintLayout) itemView.findViewById(R.id.constraintL);
        cardView = (CardView)itemView.findViewById(R.id.item_point);
        linearLayout = (LinearLayout)itemView.findViewById(R.id.layout_casaLavoro);
        nomeCognome = (TextView)itemView.findViewById(R.id.nomeCognomeUtente);
        circleImageView = (CircleImageView)itemView.findViewById(R.id.profile);
        punti = (TextView)itemView.findViewById(R.id.nPunti);
    }
}
