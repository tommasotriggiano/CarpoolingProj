package it.uniba.di.sms.carpooling;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.ContentValues.TAG;


public class OfferRideFragment extends Fragment {

    OnShowRideOfferedListener onShowRideOfferedListener;
    public interface OnShowRideOfferedListener{
       void onShowRideOffered();
    }


    TextView dateText,tvTime,dayOfWeek;
    ImageButton btnInvert;
    TextView mWork;
    TextView mHome;
    TextView posti;
    int postiIns;
    ImageView btnPlus,btnMinus;
    Button offer;

    //creazione del database
    DatabaseReference databasePassaggi;
    CollectionReference passaggioRf;
    DocumentReference pass;

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
        dayOfWeek=(TextView) view.findViewById(R.id.day) ;

        //istanza del database riguardanti i passaggi
        databasePassaggi = FirebaseDatabase.getInstance().getReference("passaggi");
        //creo la collezione dei passaggi
        passaggioRf = FirebaseFirestore.getInstance().collection("Rides");

        btnMinus=(ImageView) view.findViewById(R.id.btnMinus);

        btnPlus=(ImageView) view.findViewById(R.id.btnPlus);
        return view;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity=(Activity)context;
        try{
            onShowRideOfferedListener= (OnShowRideOfferedListener) activity;
        }
        catch(ClassCastException e){
            throw new ClassCastException(activity.toString()+"must implement onShowRideOfferedListener");
        }

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
               btnPlus.setVisibility(View.VISIBLE);
            }else if (postiIns<=1 ) {
                btnMinus.setVisibility(View.INVISIBLE);
             }

        }
    };
    public View.OnClickListener btnPlusListener= new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            if(postiIns<8) {
                posti.setText((Integer.parseInt(posti.getText().toString()) + 1) + "");
                postiIns = Integer.parseInt(posti.getText().toString());
                btnMinus.setVisibility(View.VISIBLE);
            } else if(postiIns>=8){
                btnPlus.setVisibility(View.INVISIBLE);
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

    SimpleDateFormat sdf= new SimpleDateFormat("EEEE");
    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            Date pick= new Date(year,monthOfYear,dayOfMonth-1);
            if (Locale.getDefault().getLanguage().equals("en") ){
                dateText.setText(String.valueOf(monthOfYear+1) + "-" + String.valueOf(dayOfMonth)
                        + "-" + String.valueOf(year));
                dayOfWeek.setText(sdf.format(pick));


            }else{
                  dateText.setText(String.valueOf(dayOfMonth) + "-" + String.valueOf(monthOfYear+1)
                    + "-" + String.valueOf(year));
                dayOfWeek.setText(sdf.format(pick));
            }
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


    public void addPassaggio(){

        final String dataPassaggio = dateText.getText().toString().trim();
        final String ora = tvTime.getText().toString().trim();
        final int postiDisponibili = postiIns;
        final String giorno = dayOfWeek.getText().toString().trim();
        String campo1 = mHome.getText().toString().trim();

        if(dataPassaggio.isEmpty()){
            dateText.setError(getResources().getString(R.string.EntDate));
            dateText.requestFocus();
            return;
        }
        if(ora.isEmpty()){
            tvTime.setError(getResources().getString(R.string.EntTime));
            tvTime.requestFocus();
            return;
        }

        //ricavo l'user id per collegare l'istanza del passaggio all'utente
        final FirebaseUser profile = FirebaseAuth.getInstance().getCurrentUser();
        //creo il documento per il passaggio
        final String id = profile.getUid()+"_"+dataPassaggio+"_"+ora;


        final String tipo;
        if(campo1.equals(getResources().getString(R.string.Home))){
            tipo = getActivity().getResources().getString(R.string.HomeWork);}
        else {
            tipo = getActivity().getResources().getString(R.string.WorkHome);}

        DocumentReference userrf = FirebaseFirestore.getInstance().collection("Users").document(profile.getUid());


        userrf.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String,Object> autista = documentSnapshot.getData();
                Map<String,Object> passaggio = new HashMap<>();
                passaggio.put("autista",autista);

                if(Locale.getDefault().getLanguage().equals("en")){
                    if(tipo.equals("Work-Home")){
                        passaggio.put("tipoViaggio","Lavoro-Casa");
                    }
                    else if(tipo.equals("Home-Work")){
                        passaggio.put("tipoViaggio","Casa-Lavoro");
                    }
                    String dP = dateText.getText().toString().trim();
                    String d[] = dP.split("-");
                    String dataPassaggio2 = d[1]+"-"+d[0]+"-"+d[2];
                    String id2 = profile.getUid()+"_"+dataPassaggio2+"_"+ora;
                    passaggio.put("idPassaggio",id2);
                    passaggio.put("dataPassaggio",dataPassaggio2);
                    pass = passaggioRf.document(id2);
                }
                else{
                passaggio.put("dataPassaggio",dataPassaggio);
                passaggio.put("idPassaggio",id);
                passaggio.put("tipoViaggio",tipo);
                pass = passaggioRf.document(id);
                }
                passaggio.put("ora",ora);
                passaggio.put("postiDisponibili",postiDisponibili);
                passaggio.put("giorno",giorno);
                passaggio.put("postiOccupati",0);

                pass.set(passaggio);

            }
        });


        onShowRideOfferedListener.onShowRideOffered();


    }











}


