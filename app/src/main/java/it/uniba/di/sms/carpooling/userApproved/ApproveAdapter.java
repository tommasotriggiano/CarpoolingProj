package it.uniba.di.sms.carpooling.userApproved;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import it.uniba.di.sms.carpooling.R;

public class ApproveAdapter extends RecyclerView.Adapter<ApproveViewHolder> {
    private final String ACCETTATO = "Accettato";
    private final String RIFIUTATO = "Rifiutato";
    private ArrayList<Map<String,Object>> itemUser;
    private Context context;

    public ApproveAdapter(ArrayList<Map<String,Object>> itemUser, Context context){
        this.itemUser = itemUser;
        this.context = context;
    }


    @Override
    public ApproveViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_approved_required, parent,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ApproveViewHolder usr = new ApproveViewHolder(layoutView,context,itemUser);
        return usr;
    }

    @Override
    public void onBindViewHolder(ApproveViewHolder holder, final int position) {
        Context context1 = holder.imgUser.getContext();
        /*se l'admin clicca su conferma viene inserito un campo boolean nel database dell'utente
        che afferma che è stato confermato
         */
        holder.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id= itemUser.get(position).get("id").toString();
                /****/
                InsertIntoAltervista(id,ACCETTATO);
                Log.i("altervista","Accettato: "+id);
                /****/
                DocumentReference users = FirebaseFirestore.getInstance().collection("Users").document(id);
                Map approved = new HashMap<>();
                approved.put("approved",true);
                users.update(approved);
                //dopo aver confermato rimuovo l'utente dalla recyclerview
                removeItem(position);
            }
        });

        holder.deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id= itemUser.get(position).get("id").toString();
                /****/
                InsertIntoAltervista(id,RIFIUTATO);
                Log.i("altervista","Rifiutato: "+id);
                /****/
                DocumentReference users = FirebaseFirestore.getInstance().collection("Users").document(id);
                Map denied = new HashMap<>();
                denied.put("approved",false);
                users.update(denied);

            }
        });

        holder.nome.setText((String)itemUser.get(position).get("name"));
        holder.cognome.setText((String)itemUser.get(position).get("surname"));
        holder.email.setText((String)itemUser.get(position).get("email"));

        if(itemUser.get(position).get("urlProfileImage") != null){
            Picasso.with(context1).load(itemUser.get(position).get("urlProfileImage").toString()).into(holder.imgUser);}

    }


    public void removeItem(int position){
        itemUser.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Map<String,Object> item,int position){
        itemUser.add(position,item);
        notifyItemInserted(position);
    }

    @Override
    public int getItemCount() {
        return itemUser.size();
    }



    public void InsertIntoAltervista(String idCode,String message){
        Log.i("altervista","startAltet");
        String url = "http://carpoolings.altervista.org/InsertNote.php?TokenID="+idCode+"&Note="+message;
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        RequestParams params = new RequestParams();
        Log.i("altervista","param");
        client.get(url, params, new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                Log.i("altervista","onStart");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.i("altervista","toast");

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("altervista","faill");
            }
        });
    }

}


