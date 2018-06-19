package it.uniba.di.sms.carpooling;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.DatagramSocketImpl;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyRidesFragment extends Fragment implements RecyclerItemTouchHelperListener {
    OnAddRideOfferedListener onAddRideOfferedListener;



    public interface OnAddRideOfferedListener{
        void onAddRideOffered();
    }

    CoordinatorLayout rootLayout;
    private RadioGroup radioGroup;
    private RadioButton offered,required;
    private TextView messageNotFound;
    private FloatingActionButton fab;
    private View view;
    //definisco la recyclerView
    private RecyclerView passaggiRecycler;
    //definisco l'adapter
    private PassaggiAdapter passaggiAdapter;
    //definisco il layout manager
    private RecyclerView.LayoutManager passaggiLayoutManager;
    private int  sizeResult=0;
    //definisco le variabili per il riferimento al database passaggi e al profilo che si è autenticato
    DatabaseReference ref;
    FirebaseUser user;

    private ArrayList resultPassaggi;

    public MyRidesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view= inflater.inflate(R.layout.my_rides,container,false);
        getActivity().setTitle(R.string.myrides);
        rootLayout= (CoordinatorLayout) view.findViewById(R.id.coordinator);
        radioGroup= (RadioGroup) view.findViewById(R.id.radioGroup) ;
        offered=(RadioButton)view.findViewById(R.id.offered) ;
        required=(RadioButton)view.findViewById(R.id.required) ;
        messageNotFound=(TextView) view.findViewById(R.id.message) ;
        passaggiRecycler = (RecyclerView) view.findViewById(R.id.rvPassaggiOfferti);
        passaggiRecycler.setNestedScrollingEnabled(false);
        passaggiRecycler.setHasFixedSize(true);
        fab=(FloatingActionButton)view.findViewById(R.id.fabPlus);

        user = FirebaseAuth.getInstance().getCurrentUser();
        //invoco il metodo per aggiungere i dati presi dal database nell'arraylist resultpassaggi
        initializeData();

        passaggiLayoutManager = new LinearLayoutManager(getActivity());
        passaggiRecycler.setLayoutManager(passaggiLayoutManager);
        passaggiAdapter = new PassaggiAdapter(resultPassaggi, getActivity());
        passaggiRecycler.setAdapter(passaggiAdapter);
        ItemTouchHelper.SimpleCallback itemTouchHelper= new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);

        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(passaggiRecycler);

        if (radioGroup.getCheckedRadioButtonId()==R.id.offered) {
            //if (passaggiAdapter.getItemCount()>0){
                messageNotFound.setVisibility(View.GONE);
                passaggiRecycler.setVisibility(View.VISIBLE);

            /*}else {
                messageNotFound.setVisibility(View.VISIBLE);
                passaggiRecycler.setVisibility(View.GONE);
                messageNotFound.setText("Non ci sono passaggi offerti");
            }*/

        }

        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity=(Activity)context;
        try{
            onAddRideOfferedListener= (MyRidesFragment.OnAddRideOfferedListener) activity;
        }
        catch(ClassCastException e){
            throw new ClassCastException(activity.toString()+"must implement onAddRideOfferedListener");
        }

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if( viewHolder instanceof PassaggiViewHolder){
            final Passaggio deletePassaggio= (Passaggio) resultPassaggi.get(viewHolder.getAdapterPosition());
            final int deleteIndex= viewHolder.getAdapterPosition();
            passaggiAdapter.removeItem(deleteIndex);
            String message= getResources().getString(R.string.Removed);
            Snackbar snackbar= Snackbar.make(rootLayout,message,Snackbar.LENGTH_LONG);
            snackbar.setAction(getString(R.string.UNDO), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    passaggiAdapter.restoreItem(deletePassaggio,deleteIndex);
                }
            });
            snackbar.setActionTextColor(Color.CYAN);
            snackbar.show();
        }



    }


    private void initializeData() {
        resultPassaggi = new ArrayList<Passaggio>();
        ref = FirebaseDatabase.getInstance().getReference("passaggi");
        //prendo solamente i passaggi che ha offerto l'utente autenticato
        ref.child(user.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    //aggiungo all'arraylist un oggetto di tipo passaggio ogni volta che l'utente lo aggiunge al database
                    resultPassaggi.add(data.getValue(Passaggio.class));
                }
                passaggiRecycler.scrollToPosition(resultPassaggi.size() - 1);
                passaggiAdapter.notifyItemInserted(resultPassaggi.size() - 1);


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddRideOfferedListener.onAddRideOffered();
            }
        });
    }
}
