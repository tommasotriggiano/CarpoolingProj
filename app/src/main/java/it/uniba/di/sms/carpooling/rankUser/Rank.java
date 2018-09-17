package it.uniba.di.sms.carpooling.rankUser;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms.carpooling.R;

/**
 * Created by loiodice on 11/07/2018.
 */

public class Rank extends Fragment {
    TextView puntiTot;
    DocumentReference userRf;
    FirebaseUser user;

    public Rank() {
        // Required empty public constructor
    }
    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(R.string.Rank);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View  view= inflater.inflate(R.layout.rank, container, false);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(getActivity().getResources().getString(R.string.detail)));
        tabLayout.addTab(tabLayout.newTab().setText(getActivity().getResources().getString(R.string.chart)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        puntiTot = (TextView)view.findViewById(R.id.puntiTot);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userRf = FirebaseFirestore.getInstance().collection("Users").document(user.getUid());
        userRf.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String,Object> user = documentSnapshot.getData();
                if(user.get("punti") != null){
                puntiTot.setText(user.get("punti").toString());}

            }
        });

        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        final PagerAdapter adapter = new PagerAdapter
                (getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        //tabLayout.setupWithViewPager(viewPager);
        //viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }
        });
        return  view;
    }

    public void assegnaPunteggio(){
        final String idPassaggio = "";
        DocumentReference passaggioRf = FirebaseFirestore.getInstance().collection("Rides").document(idPassaggio);
        passaggioRf.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                int count = 0;
                Map<String,Object> map = documentSnapshot.getData();
                if(map.get("passeggeri")!= null){
                HashMap<String,Object> passeggeri = (HashMap<String, Object>) map.get("passeggeri");
                count = passeggeri.size();
                final int puntiPasseggero = 5*count;
                final int puntiAutista = puntiPasseggero +10;
                //creo il database per il tracking dove inserisco anche lo storico dei punteggi
                    CollectionReference tracking = FirebaseFirestore.getInstance().collection("Tracking");
                    DocumentReference pass = tracking.document(idPassaggio);
                    HashMap<String,Object> autista = (HashMap<String, Object>) map.get("autista");
                    HashMap<String,Object> autistaTracking = new HashMap<>();
                    autistaTracking.put("id",autista.get("id"));
                    autistaTracking.put("punti",puntiAutista);
                    pass.update("autista",autistaTracking);
                    final DocumentReference userAutista = FirebaseFirestore.getInstance().collection("Users").document(autista.get("id").toString());
                    userAutista.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Map<String,Object> userMap = documentSnapshot.getData();
                            if(userMap.get("punti") != null){
                                int punti = (int) userMap.get("punti");
                                punti += puntiAutista;
                                userAutista.update("punti",punti);
                            }
                            else{
                                userAutista.update("punti",puntiAutista);
                            }

                        }
                    });

                    //aggiungo i punti dei passeggeri
                    HashMap<String,Object> passeggeriTracking = new HashMap<>();
                    for(String id : passeggeri.keySet()){
                        passeggeriTracking.put(id,puntiPasseggero);
                        final DocumentReference user = FirebaseFirestore.getInstance().collection("Users").document(id);
                        //aggiungo il totale dei punti passeggero al database degli user
                        user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Map<String,Object> userMap = documentSnapshot.getData();
                                    if(userMap.get("punti") != null){
                                        int punti = (int) userMap.get("punti");
                                        punti += puntiPasseggero;
                                        user.update("punti",punti);
                                    }
                                    else{
                                        user.update("punti",puntiPasseggero);
                                    }

                            }
                        });
                    }
                    pass.update("passeggeri",passeggeriTracking);




                }

            }
        });

    }

}
