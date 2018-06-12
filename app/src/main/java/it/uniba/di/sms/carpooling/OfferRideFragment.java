package it.uniba.di.sms.carpooling;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;


public class OfferRideFragment extends Fragment {
    TextView dateText,tvTime;
    ImageButton btnInvert;
    TextView mWork;
    TextView mHome;
    TextView posti;
    int postiIns;
    ImageButton btnMinus;
    ImageButton btnPlus;
    Button offer;

    //creazione del database
    DatabaseReference databasePassaggi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.offer_ride, container,false);
        dateText=(TextView)view.findViewById(R.id.textData);
        tvTime = (TextView) view.findViewById(R.id.tvTime);
        btnInvert = (ImageButton) view.findViewById(R.id.Invert);
        mHome = (TextView) view.findViewById(R.id.casa);
        mWork = (TextView) view.findViewById(R.id.lavoro);
        posti=(TextView) view.findViewById(R.id.textPostiInseriti);
        postiIns= Integer.parseInt(posti.getText().toString());
        offer = (Button)view.findViewById(R.id.btnOffri);

        //istanza del database riguardanti i passaggi
        databasePassaggi = FirebaseDatabase.getInstance().getReference("passaggi");

        btnMinus=(ImageButton) view.findViewById(R.id.btnMinus);

        btnPlus=(ImageButton) view.findViewById(R.id.btnPlus);
        return view;

    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        getActivity().setTitle(R.string.offeraride);
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

        btnInvert.setOnClickListener(btnInvertListener);
        btnMinus.setOnClickListener(btnMinusListener);
        btnPlus.setOnClickListener(btnPlusListener);
        offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPassaggio();


            }}

        );
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
    public View.OnClickListener btnMinusListener= new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            if(postiIns>1){
                posti.setText((Integer.parseInt(posti.getText().toString())-1)+"");
                postiIns= Integer.parseInt(posti.getText().toString());
            }

        }
    };
    public View.OnClickListener btnPlusListener= new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            if(postiIns<8) {
                posti.setText((Integer.parseInt(posti.getText().toString()) + 1) + "");
                postiIns = Integer.parseInt(posti.getText().toString());
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
            if (Locale.getDefault().getLanguage() == "en" ){
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
         * Set Up Current Date Into dialog
         */
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("hour", calender.get(Calendar.HOUR_OF_DAY));
        args.putInt("minute", calender.get(Calendar.MINUTE));
        time.setArguments(args);
        /**
         * Set Call back to capture selected date
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


    public void addPassaggio(){

        final String dataPassaggio = dateText.getText().toString().trim();
        final String ora = tvTime.getText().toString().trim();
        final int postiDisponibili = postiIns;

        if(dataPassaggio.isEmpty()){
            dateText.setError(getResources().getString(R.string.EntName));
            dateText.requestFocus();
            return;
        }
        if(ora.isEmpty()){
            tvTime.setError(getResources().getString(R.string.EntSurname));
            tvTime.requestFocus();
            return;
        }
        //ricavo l'user id per collegare il passaggio all'utente che lo ha offerto
        FirebaseUser profile = FirebaseAuth.getInstance().getCurrentUser();

        /*inserisco nel database passaggi il passaggio creato.
        Il passaggio avrà una chiave composta costituita da id utente,data e ora,
        in questo modo l'utente può offrire due passaggi nello stesso giorno ma ad orari diversi
         */

        Passaggio passaggio = new Passaggio(postiDisponibili);
        databasePassaggi.child(profile.getUid()).child(dataPassaggio).child(ora).setValue(passaggio);
    }
}


