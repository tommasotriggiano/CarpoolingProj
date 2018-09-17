package it.uniba.di.sms.carpooling.rankUser;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms.carpooling.R;
import it.uniba.di.sms.carpooling.RankHistoryAdapter;

public class HistoricalPointFragment extends Fragment {
    FrameLayout rootLayout;
    private ArrayList resultPassaggi;
    private RecyclerView history_recycler;
    private RankHistoryAdapter history_adapter;
    FirebaseUser user;
    CollectionReference history_rides;


    public HistoricalPointFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historical_point, container, false);
        rootLayout= (FrameLayout) view.findViewById(R.id.root);
        history_recycler = (RecyclerView) view.findViewById(R.id.rvHistoricalPoint);
        RecyclerView.LayoutManager historyLayoutManager = new LinearLayoutManager(getActivity());
        history_recycler.setLayoutManager(historyLayoutManager);
        history_recycler.setNestedScrollingEnabled(false);
        history_recycler.setHasFixedSize(true);

        user = FirebaseAuth.getInstance().getCurrentUser();

        initializeDataHistoryPoint();
        return view;
    }

    private void initializeDataHistoryPoint() {
        resultPassaggi = new ArrayList<Map<String,Object>>();
        history_rides = FirebaseFirestore.getInstance().collection("Tracking");
        Query findRides = history_rides.whereEqualTo("autista.id",user.getUid());

        findRides.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for(DocumentSnapshot d: documentSnapshots.getDocuments()){
                    Map<String,Object> map = d.getData();
                    Map<String,Object> map2 = new HashMap<>();
                    map2.put("idPassaggio",map.get("idPassaggio"));
                    Map<String,Object> autista = (Map<String, Object>) map.get("autista");
                    map2.put("punti",autista.get("punti").toString());
                    resultPassaggi.add(map2);
                }

            }
        });

        history_rides.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for(DocumentSnapshot d : documentSnapshots.getDocuments()){
                    Map<String,Object> map = d.getData();
                    if(map.get("passeggeri")!= null){
                        Map<String,Object> passeggeri = (Map<String, Object>) map.get("passeggeri");
                        if(passeggeri.containsKey(user.getUid())){
                            Map<String,Object> map2 = new HashMap<>();
                            map2.put("idPassaggio",map.get("idPassaggio"));
                            map2.put("punti",passeggeri.get(user.getUid()));
                            resultPassaggi.add(map2);
                        }
                    }

                }
                history_adapter = new RankHistoryAdapter(resultPassaggi,getActivity());
                history_recycler.setAdapter(history_adapter);

            }
        });
        history_recycler.scrollToPosition(resultPassaggi.size()-1);


}
    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }



}
