package it.uniba.di.sms.carpooling;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import it.uniba.di.sms.carpooling.rideOffered.PassaggiAdapter;
import it.uniba.di.sms.carpooling.rideOffered.PassaggiViewHolder;
import it.uniba.di.sms.carpooling.rideRequired.RequiredAdapter;

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
    private RecyclerView requiredRecycler;
    //definisco l'adapter
    private PassaggiAdapter passaggiAdapter;
    private RequiredAdapter requiredAdapter;
    //definisco il layout manager
    private RecyclerView.LayoutManager passaggiLayoutManager;
    private RecyclerView.LayoutManager requiredLayoutManager;
    private int  sizeResult=0;
    //definisco le variabili per il riferimento al database passaggi e al profilo che si è autenticato
    //DatabaseReference ref;
    FirebaseUser user;
    CollectionReference passaggi;
    CollectionReference passaggiRichiesti;
    private ArrayList resultPassaggi;
    private ArrayList resultRequired;

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

        requiredRecycler = (RecyclerView) view.findViewById(R.id.rvPassaggiRichiesti);
        requiredRecycler.setNestedScrollingEnabled(false);
        requiredRecycler.setHasFixedSize(true);

        fab=(FloatingActionButton)view.findViewById(R.id.fabPlus);

        user = FirebaseAuth.getInstance().getCurrentUser();
        //invoco il metodo per aggiungere i dati presi dal database nell'arraylist resultpassaggi
        initializeDataPassaggi();
        initializeDataRequired();

        passaggiRecycler.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);
        requiredRecycler.setVisibility(View.GONE);


        passaggiAdapter = new PassaggiAdapter(resultPassaggi, getActivity());
        requiredAdapter = new RequiredAdapter(resultRequired,getActivity());

        passaggiRecycler.setAdapter(passaggiAdapter);
        requiredRecycler.setAdapter(requiredAdapter);

        ItemTouchHelper.SimpleCallback itemTouchHelper= new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(passaggiRecycler);

        passaggiLayoutManager = new LinearLayoutManager(getActivity());
        requiredLayoutManager = new LinearLayoutManager(getActivity());

        passaggiRecycler.setLayoutManager(passaggiLayoutManager);
        requiredRecycler.setLayoutManager(requiredLayoutManager);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.required){
                    passaggiRecycler.setVisibility(View.GONE);
                    requiredRecycler.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.GONE);
                }
                else{
                    passaggiRecycler.setVisibility(View.VISIBLE);
                    requiredRecycler.setVisibility(View.GONE);
                    fab.setVisibility(View.VISIBLE);
                }

            }
        });



        return view;
    }

    private void initializeDataRequired() {
        resultRequired = new ArrayList<Map<String,Object>>();

        if(!(resultRequired.isEmpty())){
            resultRequired.clear();
        }

        passaggiRichiesti = FirebaseFirestore.getInstance().collection("RideRequests");

        Query required = passaggiRichiesti.whereEqualTo("passeggero.id",user.getUid());
        required.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot document : task.getResult()){
                    Map<String,Object> passaggiReq = document.getData();
                    resultRequired.add(passaggiReq);
                }
                requiredRecycler.scrollToPosition(resultRequired.size() - 1);
                requiredAdapter.notifyItemInserted(resultRequired.size() - 1);

            }
        });
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
            final Map<String,Object> deletePassaggio= (Map<String,Object>) resultPassaggi.get(viewHolder.getAdapterPosition());
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


            snackbar.addCallback(new Snackbar.Callback(){
                @Override
                /*se la snackbar scompare vuol dire che il passaggio non può più essere ripristinato
                * quindi posso cancellarlo dal database
                */
                public void onDismissed(Snackbar snackbar1,int event){
                    if(event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT){
                    Query findRideToDelete = passaggi
                            .whereEqualTo("autista.id",user.getUid())
                            .whereEqualTo("dataPassaggio",deletePassaggio.get("dataPassaggio"))
                            .whereEqualTo("ora",deletePassaggio.get("ora"));

                    findRideToDelete.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (DocumentSnapshot d : task.getResult()) {
                                passaggi.document(d.getId()).delete();
                            }
                        }
                    });
                    }
                }
            });
        }
    }








    private void initializeDataPassaggi() {
        resultPassaggi = new ArrayList<Map<String,Object>>();
        if(!(resultPassaggi.isEmpty())){
            resultRequired.clear();
        }
        passaggi = FirebaseFirestore.getInstance().collection("Rides");
        //prendo solamente i passaggi che ha offerto l'utente autenticato

        Query offered = passaggi.whereEqualTo("autista.id",user.getUid());
        offered.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot document : task.getResult()){
                    Map<String,Object> passaggiOff = document.getData();
                    resultPassaggi.add(passaggiOff);
                }
                passaggiRecycler.scrollToPosition(resultPassaggi.size() - 1);
                passaggiAdapter.notifyItemInserted(resultPassaggi.size() - 1);

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
