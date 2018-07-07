package it.uniba.di.sms.carpooling;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchRideFragment extends Fragment {
    private TextView dateText,tvTime,mWork,mHome,errorDate,errorTime;
    private ImageButton btnInvert;
    private Button search;
    private AutoCompleteTextView driver ;
    private ImageView img_date,img_time;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.search_ride, container,false);
        dateText=(TextView)view.findViewById(R.id.textData);
        tvTime = (TextView) view.findViewById(R.id.tvTime);
        btnInvert = (ImageButton) view.findViewById(R.id.Invert);
        mHome = (TextView) view.findViewById(R.id.casa);
        mWork = (TextView) view.findViewById(R.id.lavoro);
        search = (Button) view.findViewById(R.id.btnSearch);
        errorDate = (TextView) view.findViewById(R.id.errorDate);
        errorTime = (TextView) view.findViewById(R.id.errorTime);
        img_date=(ImageView)view.findViewById(R.id.img_error_date) ;
        img_time=(ImageView)view.findViewById(R.id.img_error_time) ;
        String []autisti= new String[]{"Terenzia Loiodice","Tommaso Triggiano","Andrea Loizzo", "Davide Bufo"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line, autisti);
        driver= (AutoCompleteTextView) view.findViewById(R.id.autista);
        driver.setAdapter(adapter);


        return view;
    }
    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        getActivity().setTitle(R.string.searchride);
        btnInvert.setOnClickListener(btnInvertListener);

        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            @SuppressWarnings("deprecation")
            public void onClick(View view) {
                showTimePicker();
            }
        });



        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchRide();
            }
        });
    }

    private void searchRide() {

        String tipo;
        String campo1 = mHome.getText().toString().trim();
        String data = dateText.getText().toString().trim();
        String ora = tvTime.getText().toString().trim();
        String nome = driver.getText().toString().trim();

        if(campo1.equals(getResources().getString(R.string.Home))){
            tipo = getResources().getString(R.string.HomeWork);}
        else {
            tipo = getResources().getString(R.string.WorkHome);;}

        if(data.isEmpty()){
            errorDate.setVisibility(View.VISIBLE);
            img_date.setVisibility(View.VISIBLE);
            dateText.requestFocus();
            dateText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    errorDate.setVisibility(View.GONE);
                    img_date.setVisibility(View.GONE);
                    showDatePicker();
                }
            });
            return;
        }
        if(ora.isEmpty()){
            errorTime.setVisibility(View.VISIBLE);
            img_time.setVisibility(View.VISIBLE);
            tvTime.requestFocus();
            tvTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        errorTime.setVisibility(View.GONE);
                        img_time.setVisibility(View.GONE);
                        showTimePicker();
                }
            });
            return;
        }

        Intent map = new Intent(getActivity(),MapsActivity.class);
        map.putExtra("tipoViaggio",tipo);
        map.putExtra("data",data);
        map.putExtra("ora",ora);
        map.putExtra("nome",nome);
        startActivity(map);

    }


    public View.OnClickListener btnInvertListener= new View.OnClickListener(){
        @Override
        public void onClick(View view) {

            if(mHome.getText().toString().equals(getResources().getString(R.string.Home))){
                mHome.setText(R.string.Work);
                mWork.setText(R.string.Home);
            }else
            {
                mHome.setText(R.string.Home);
                mWork.setText(R.string.Work);
            }
        }
    };



    private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(ondate);
        date.show(getFragmentManager(), "Date Picker");
    }


    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            if (Locale.getDefault().getLanguage().equals("en") ){
                dateText.setText(String.valueOf(monthOfYear+1) + "-" + String.valueOf(dayOfMonth)
                        + "-" + String.valueOf(year));
            }else{
                dateText.setText(String.valueOf(dayOfMonth) + "-" + String.valueOf(monthOfYear+1)
                        + "-" + String.valueOf(year));}
        }
    };
    private void showTimePicker() {
        TimePickerFragment time = new TimePickerFragment();
        /**
         * Set Up Current Time Into dialog
         */
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("hour", calender.get(Calendar.HOUR_OF_DAY));
        args.putInt("minute", calender.get(Calendar.MINUTE));
        time.setArguments(args);
        /**
         * Set Call back to capture selected time
         */
        time.setCallBack(ontime);
        time.show(getFragmentManager(), "Time Picker");
    }
    TimePickerDialog.OnTimeSetListener ontime = new TimePickerDialog.OnTimeSetListener() {

        public void onTimeSet(TimePicker view, int hour, int minute) {
            if (hour < 10) {
                if (minute < 10) {
                    tvTime.setText("0" + String.valueOf(hour) + ":0" + String.valueOf(minute));
                } else {
                    tvTime.setText("0" + String.valueOf(hour) + ":" + String.valueOf(minute));
                }
            } else if (minute<10) {
                tvTime.setText(String.valueOf(hour) + ":0" + String.valueOf(minute));
            }else{
                tvTime.setText(String.valueOf(hour) + ":" + String.valueOf(minute));
            }
        }

    };

}
