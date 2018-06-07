package it.uniba.di.sms.carpooling;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyRidesFragment extends Fragment {
    Switch tipo;

    public MyRidesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.my_rides,container,false);
        tipo=(Switch) view.findViewById(R.id.switch1);
        return view;
    }
    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
