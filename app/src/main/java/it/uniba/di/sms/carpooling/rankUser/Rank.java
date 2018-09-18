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
    }}

