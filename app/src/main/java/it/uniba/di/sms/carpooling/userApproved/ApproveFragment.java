package it.uniba.di.sms.carpooling.userApproved;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import it.uniba.di.sms.carpooling.R;

public class ApproveFragment extends Fragment {
    private View view;

    FirebaseUser admin;
    CollectionReference dipendenti;
    DocumentReference adminrf;

    //definisco la recyclerView
    private RecyclerView userRecycler;
    //definisco l'adapter
    private ApproveAdapter approveAdapter;
    //definisco il layout manager
    private RecyclerView.LayoutManager approveLayoutManager;

    private ArrayList users;
    public ApproveFragment() {
        // Required empty public constructor
    }
    private String urlImageProfile;
    private ImageView imgProfile;







    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.approved_affiliation,container,false);
        getActivity().setTitle(R.string.approved);
        admin = FirebaseAuth.getInstance().getCurrentUser();

        userRecycler = (RecyclerView) view.findViewById(R.id.rvApprovation);
        userRecycler.setNestedScrollingEnabled(false);
        userRecycler.setHasFixedSize(true);
        //invoco il metodo per aggiungere i dati presi dal database nell'arraylist resultpassaggi
        initializeData();

        approveLayoutManager = new LinearLayoutManager(getActivity());
        userRecycler.setLayoutManager(approveLayoutManager);
        approveAdapter = new ApproveAdapter(users,getActivity());
        userRecycler.setAdapter(approveAdapter);

        return view;

    }

    private void initializeData() {
        users = new ArrayList<Map<String,Object>>();


        dipendenti = FirebaseFirestore.getInstance().collection("Users");
        adminrf = FirebaseFirestore.getInstance().collection("Admin").document(admin.getUid());
        Query sameCompany = dipendenti.whereEqualTo("userCompany.idAdmin",adminrf.getId());
        sameCompany.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot doc : task.getResult()){
                    Map<String,Object> dipendenti = doc.getData();
                    String idDipendenti = (String) dipendenti.get("id");
                    boolean approved = (boolean) dipendenti.get("approved");
                    if(!(idDipendenti.equals(admin.getUid())) && !(approved)){
                        users.add(dipendenti);
                }}
                userRecycler.scrollToPosition(users.size()-1);
                approveAdapter.notifyItemInserted(users.size() - 1);

            }
        });

    }


    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);}

}
