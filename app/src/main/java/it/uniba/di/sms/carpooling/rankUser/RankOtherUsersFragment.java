package it.uniba.di.sms.carpooling.rankUser;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import it.uniba.di.sms.carpooling.R;
import it.uniba.di.sms.carpooling.RankAdapter;


public class RankOtherUsersFragment extends Fragment {
    RecyclerView rankRecycler;
    RankAdapter rankAdapter;
    FirebaseUser user;
    ArrayList resultUsers;
    private RecyclerView.LayoutManager layoutManager;

    public RankOtherUsersFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rank_other_users, container, false);
        ConstraintLayout rootLayout = (ConstraintLayout) view.findViewById(R.id.root);
        rankRecycler = (RecyclerView) view.findViewById(R.id.rvRank);
        rankRecycler.setNestedScrollingEnabled(false);
        rankRecycler.setHasFixedSize(true);
        user = FirebaseAuth.getInstance().getCurrentUser();
        initializeRank();

        LinearLayoutManager  classificalm = new LinearLayoutManager(getActivity());
        rankRecycler.setLayoutManager(classificalm);
        classificalm.setOrientation(LinearLayoutManager.VERTICAL);
        return view;
    }

    private void initializeRank() {
        resultUsers = new ArrayList<Map<String,Object>>();
        CollectionReference userCf = FirebaseFirestore.getInstance().collection("Users");
        Query userCfOrder = userCf.orderBy("punti", Query.Direction.DESCENDING);
        userCfOrder.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for(DocumentSnapshot d : documentSnapshots.getDocuments()){
                    Map<String,Object> users = d.getData();
                    if(users.get("punti")!= null){
                        resultUsers.add(users);

                    }
                }
                rankAdapter= new RankAdapter(resultUsers,getActivity());
                rankRecycler.setAdapter(rankAdapter);

            }
        });
        rankRecycler.scrollToPosition(resultUsers.size()-1);
    }
    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }



}
