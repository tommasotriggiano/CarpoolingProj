package it.uniba.di.sms.carpooling.userApproved;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import it.uniba.di.sms.carpooling.R;

public class ApproveViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public RelativeLayout viewBackground;
    public LinearLayout viewForeground;
    public LinearLayout viewButton;

    public CardView cardView;
    public TextView nome;
    public TextView cognome;
    public TextView email;
    public ArrayList<Map<String,Object>> user;
    public CircleImageView imgUser;
    public Context ctx;
    public Button confirm;
    public Button deny;

    public ApproveViewHolder(View itemView, Context ctx, ArrayList<Map<String,Object>> user) {
        super(itemView);
        this.user = user;
        this.ctx=ctx;

        itemView.setOnClickListener(this);
        viewForeground=(LinearLayout)itemView.findViewById(R.id.view1);
        viewBackground=(RelativeLayout) itemView.findViewById(R.id.view2);
        viewButton = (LinearLayout) itemView.findViewById(R.id.viewBtn);
        cardView= (CardView) itemView.findViewById(R.id.cardviewApproved);
        nome = (TextView) itemView.findViewById(R.id.nomeAut);
        cognome = (TextView) itemView.findViewById(R.id.cognomeAut);
        email = (TextView) itemView.findViewById(R.id.emailApp);
        imgUser = (CircleImageView) itemView.findViewById(R.id.imageProfile);
        confirm = (Button) itemView.findViewById(R.id.accetta);
        deny = (Button) itemView.findViewById(R.id.rifiuta);

    }

    @Override
    public void onClick(View view) {
        int position = getAdapterPosition();
        Map<String,Object> usr= this.user.get(position);

    }
}

