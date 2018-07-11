package it.uniba.di.sms.carpooling.rankUser;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.uniba.di.sms.carpooling.R;


public class RankOtherUsersFragment extends Fragment {

    public RankOtherUsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rank_other_users, container, false);
    }


}
