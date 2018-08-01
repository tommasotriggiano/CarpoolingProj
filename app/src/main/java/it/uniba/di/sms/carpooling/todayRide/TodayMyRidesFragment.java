package it.uniba.di.sms.carpooling.todayRide;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import it.uniba.di.sms.carpooling.R;
import it.uniba.di.sms.carpooling.rideOffered.PassaggiAdapter;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class TodayMyRidesFragment extends Fragment {

    private RecyclerView passaggiOggiRecycler;
    private ArrayList resultPassaggi;
    CollectionReference passaggi;
    private TodayMyRidesAdapter passaggiOggiAdapter;
    FirebaseUser user;

    public TodayMyRidesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.today_rides, container,false);
        getActivity().setTitle(R.string.HomePage);

        passaggiOggiRecycler = (RecyclerView) view.findViewById(R.id.rvTodayRides);
        passaggiOggiRecycler.setNestedScrollingEnabled(false);
        passaggiOggiRecycler.setHasFixedSize(true);
        user = FirebaseAuth.getInstance().getCurrentUser();
        initializeDataPassaggi();
        LinearLayoutManager  passaggiOggiLayoutManager = new LinearLayoutManager(getActivity());
        passaggiOggiRecycler.setLayoutManager(passaggiOggiLayoutManager);
        passaggiOggiLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        return view;
    }



    private void initializeDataPassaggi() {
        resultPassaggi = new ArrayList<Map<String,Object>>();
        if(!(resultPassaggi.isEmpty())){
            resultPassaggi.clear();
        }

        passaggi = FirebaseFirestore.getInstance().collection("Rides");
        //prendo solamente i passaggi che ha offerto l'utente autenticato

        //per prendere la data corrente
        String today=getDate(System.currentTimeMillis());
        Toast.makeText(getActivity(),today,Toast.LENGTH_SHORT).show();
        Query todayRides = passaggi.whereEqualTo("autista.id",user.getUid()).whereEqualTo("dataPassaggio",today);
        final Query todayRides2 = passaggi.whereEqualTo("dataPassaggio",today);


        todayRides.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for(DocumentChange dc : documentSnapshots.getDocumentChanges()){
                    DocumentSnapshot document = dc.getDocument();
                    Map<String,Object> map = document.getData();
                    switch(dc.getType()){
                        case ADDED:
                            resultPassaggi.add(map);
                            break;
                        case MODIFIED:
                            resultPassaggi.set(dc.getNewIndex(),map);
                            break;

                    }

                }
            }
        });

        todayRides2.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for (DocumentChange dc : documentSnapshots.getDocumentChanges()) {
                    DocumentSnapshot document = dc.getDocument();
                    Map<String, Object> map = document.getData();
                    if (map.get("passeggeri") != null) {
                        Map<String,Object> passeggeri = (Map<String, Object>) map.get("passeggeri");
                        switch (dc.getType()) {
                            case ADDED:
                                if (passeggeri.containsKey(user.getUid())) {
                                    resultPassaggi.add(map);
                                }
                                break;
                            case MODIFIED:
                                if (passeggeri.containsKey(user.getUid())) {
                                    resultPassaggi.set(dc.getNewIndex(), map);
                                }
                                break;

                        }

                    }
                }
                passaggiOggiAdapter = new TodayMyRidesAdapter(resultPassaggi, getActivity());
                passaggiOggiRecycler.setAdapter(passaggiOggiAdapter);
            }
        });
        passaggiOggiRecycler.scrollToPosition(resultPassaggi.size() - 1);

    }






    private String getDate(long time_stamp_server) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        return formatter.format(time_stamp_server);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


}
