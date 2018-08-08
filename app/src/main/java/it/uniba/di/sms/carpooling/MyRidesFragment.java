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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms.carpooling.rideOffered.PassaggiAdapter;
import it.uniba.di.sms.carpooling.rideOffered.PassaggiViewHolder;
import it.uniba.di.sms.carpooling.rideRequired.RequiredAdapter;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyRidesFragment extends Fragment implements RecyclerItemTouchHelperListener {
    OnAddRideOfferedListener onAddRideOfferedListener;
    ShowRideRequiredListener showRideRequiredListener;


    public interface OnAddRideOfferedListener{
        void onAddRideOffered();
    }
    public interface ShowRideRequiredListener{
        void showRideRequired();
    }

    CoordinatorLayout rootLayout;
    private FloatingActionButton fab;
    //definisco la recyclerView
    private RecyclerView passaggiRecycler;
    private RecyclerView requiredRecycler;
    //definisco l'adapter
    private PassaggiAdapter passaggiAdapter;
    private RequiredAdapter requiredAdapter;
    FirebaseUser user;
    CollectionReference passaggi;
    CollectionReference requests;
    CollectionReference passaggiRichiesti;
    private ArrayList resultPassaggi;
    private ArrayList resultRequired;
    private TextView messageNotFound;

    public MyRidesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.my_rides, container, false);
        getActivity().setTitle(R.string.myrides);
        rootLayout= (CoordinatorLayout) view.findViewById(R.id.coordinator);
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        messageNotFound = (TextView) view.findViewById(R.id.message);

        passaggiRecycler = (RecyclerView) view.findViewById(R.id.rvPassaggiOfferti);
        passaggiRecycler.setNestedScrollingEnabled(false);
        passaggiRecycler.setHasFixedSize(true);

        requiredRecycler = (RecyclerView) view.findViewById(R.id.rvPassaggiRichiesti);
        requiredRecycler.setNestedScrollingEnabled(false);
        requiredRecycler.setHasFixedSize(true);

        fab=(FloatingActionButton) view.findViewById(R.id.fabPlus);

        user = FirebaseAuth.getInstance().getCurrentUser();
        passaggi = FirebaseFirestore.getInstance().collection("Rides");
        requests = FirebaseFirestore.getInstance().collection("RideRequests");
        //invoco il metodo per aggiungere i dati presi dal database nell'arraylist resultpassaggi
        passaggiRecycler.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);
        initializeDataPassaggi();
        initializeDataRequired();




        ItemTouchHelper.SimpleCallback itemTouchHelper= new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(passaggiRecycler);

        RecyclerView.LayoutManager passaggiLayoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.LayoutManager requiredLayoutManager = new LinearLayoutManager(getActivity());

        passaggiRecycler.setLayoutManager(passaggiLayoutManager);
        requiredRecycler.setLayoutManager(requiredLayoutManager);

        if (getArguments()!= null && getArguments().getString("REQUIRED")!= null){
            radioGroup.check(R.id.required);
            passaggiRecycler.setVisibility(View.INVISIBLE);
            requiredRecycler.setVisibility(View.VISIBLE);
            fab.setVisibility(View.GONE);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.required){
                    passaggiRecycler.setVisibility(View.INVISIBLE);
                    requiredRecycler.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.GONE);
                }
                else{

                    passaggiRecycler.setVisibility(View.VISIBLE);
                    requiredRecycler.setVisibility(View.INVISIBLE);
                    fab.setVisibility(View.VISIBLE);
                }

            }
        });



        return view;
    }

    private void initializeDataRequired() {
        resultRequired = new ArrayList<Map<String, Object>>();

        if (!(resultRequired.isEmpty())) {
            resultRequired.clear();
        }

        passaggiRichiesti = FirebaseFirestore.getInstance().collection("RideRequests");

        Query required = passaggiRichiesti.whereEqualTo("idPasseggero", user.getUid());

        ListenerRegistration listenerRegistration = required.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, e.toString());
                    return;
                }
                for (final DocumentChange dc : documentSnapshots.getDocumentChanges()) {
                    DocumentSnapshot document = dc.getDocument();
                    Map<String, Object> richieste = document.getData();

                    switch (dc.getType()) {
                        case ADDED:
                            resultRequired.add(richieste);
                            break;
                        case MODIFIED:
                            resultRequired.set(dc.getNewIndex(), richieste);
                            break;
                        case REMOVED:
                            resultRequired.remove(dc.getOldIndex());

                    }


                }
                requiredAdapter = new RequiredAdapter(resultRequired, getActivity());
                requiredRecycler.setAdapter(requiredAdapter);
            }
        });
        requiredRecycler.scrollToPosition(resultPassaggi.size() - 1);
    }





    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity=(Activity)context;
        try{
            onAddRideOfferedListener= (MyRidesFragment.OnAddRideOfferedListener) activity;
            showRideRequiredListener=(MyRidesFragment.ShowRideRequiredListener)activity;
        }
        catch(ClassCastException e){
            throw new ClassCastException(activity.toString()+"must implement listener");
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
                        //Toast.makeText(getContext(),deletePassaggio.toString(),Toast.LENGTH_LONG).show();
                    Query findRideToDelete = passaggi
                            .whereEqualTo("autista.id",user.getUid())
                            .whereEqualTo("dataPassaggio",deletePassaggio.get("dataPassaggio"))
                            .whereEqualTo("ora",deletePassaggio.get("ora"));
                    Query deleteRequest = requests.whereEqualTo("idPassaggio",deletePassaggio.get("idPassaggio"));

                    findRideToDelete.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (DocumentSnapshot d : task.getResult()) {
                                passaggi.document(d.getId()).delete();
                            }
                        }
                    });
                    deleteRequest.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (DocumentSnapshot d : task.getResult()) {
                                requests.document(d.getId()).delete();
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

        Query offered = passaggi.whereEqualTo("autista.id",user.getUid()).orderBy("dataPassaggio").orderBy("ora");

        offered.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(e != null){
                    Log.e(TAG,e.toString());
                    return;
                }
                for(DocumentChange dc : documentSnapshots.getDocumentChanges()){
                    DocumentSnapshot document = dc.getDocument();
                    Map<String,Object> passaggio = document.getData();

                            switch(dc.getType()){
                                case ADDED:
                                    resultPassaggi.add(passaggio);
                                    break;
                                case MODIFIED:
                                    resultPassaggi.set(dc.getNewIndex(),passaggio);
                                    break;
                            }
                        }
                passaggiAdapter = new PassaggiAdapter(resultPassaggi, getActivity());
                passaggiRecycler.setAdapter(passaggiAdapter);
            }
        });
        passaggiRecycler.scrollToPosition(resultPassaggi.size() - 1);
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
