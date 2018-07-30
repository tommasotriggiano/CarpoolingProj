package it.uniba.di.sms.carpooling;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;



public class OfferRideFragment extends Fragment {

    OnShowRideOfferedListener onShowRideOfferedListener;
    public interface OnShowRideOfferedListener{
       void onShowRideOffered();
    }


    TextView dateText,tvTime,dayOfWeek,mWork,mHome,posti,errorDate,errorTime;
    ImageButton btnInvert;
    String postiString;
    int postiIns;
    ImageView btnPlus,btnMinus,img_date,img_time;
    Button offer;


    CollectionReference passaggioRf;
    DocumentReference pass;
    DocumentReference userrf;
    FirebaseUser profile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.offer_ride, container,false);
        dateText=(TextView)view.findViewById(R.id.textData);
        tvTime = (TextView) view.findViewById(R.id.tvTime);
        btnInvert = (ImageButton) view.findViewById(R.id.Invert);
        errorDate = (TextView) view.findViewById(R.id.errorDate);
        errorTime = (TextView) view.findViewById(R.id.errorTime);
        mHome = (TextView) view.findViewById(R.id.casa);
        mWork = (TextView) view.findViewById(R.id.lavoro);
        posti=(TextView) view.findViewById(R.id.textPostiInseriti);
        postiIns= Integer.parseInt(posti.getText().toString());
        offer = (Button)view.findViewById(R.id.btnOffri);
        dayOfWeek=(TextView) view.findViewById(R.id.day) ;
        img_date=(ImageView)view.findViewById(R.id.img_error_date) ;
        img_time=(ImageView)view.findViewById(R.id.img_error_time) ;



        //creo la collezione dei passaggi
        passaggioRf = FirebaseFirestore.getInstance().collection("Rides");
        profile = FirebaseAuth.getInstance().getCurrentUser();
        userrf = FirebaseFirestore.getInstance().collection("Users").document(profile.getUid());

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
                postiString=(Integer.parseInt(posti.getText().toString())-1)+"";
                posti.setText(postiString);
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
                postiString=(Integer.parseInt(posti.getText().toString()) + 1) + "";
                posti.setText(postiString);
                postiIns = Integer.parseInt(posti.getText().toString());
                btnMinus.setVisibility(View.VISIBLE);
            } else if(postiIns>=8){
                btnPlus.setVisibility(View.INVISIBLE);
            }
        }
    };
    private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();
        //Set Up Current Date Into dialog
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        //Set Call back to capture selected date
        date.setCallBack(ondate);
        date.show(getFragmentManager(), "Date Picker");
    }

    SimpleDateFormat sdf= new SimpleDateFormat("EEEE");
    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            Date pick= new Date(year,monthOfYear,dayOfMonth-1);
            int month = Integer.parseInt(String.valueOf(monthOfYear+1));
            int day = Integer.parseInt(String.valueOf(dayOfMonth));
            String dateString;
            if (Locale.getDefault().getLanguage().equals("en") ){
                if(month<10){
                    if(day<10){
                        dateString="0"+String.valueOf(monthOfYear+1) + "-" + "0"+String.valueOf(dayOfMonth)
                                + "-" + String.valueOf(year);
                        dateText.setText(dateString);}
                    else{
                        dateString="0"+String.valueOf(monthOfYear+1) + "-" +String.valueOf(dayOfMonth)
                                + "-" + String.valueOf(year);
                        dateText.setText(dateString);}
                }
                else if(day<10){
                    dateString=String.valueOf(monthOfYear+1) + "-" + "0"+String.valueOf(dayOfMonth)
                            + "-" + String.valueOf(year);
                    dateText.setText(dateString);}
                else{
                    dateString=String.valueOf(monthOfYear+1) + "-" + String.valueOf(dayOfMonth)
                            + "-" + String.valueOf(year);
                dateText.setText(dateString);}
                dayOfWeek.setText(sdf.format(pick));


            }else{
                if(month<10){
                    if(day<10){
                        dateString="0"+String.valueOf(dayOfMonth) + "-" + "0"+String.valueOf(monthOfYear+1)
                                + "-" + String.valueOf(year);
                        dateText.setText(dateString);
                    }
                    else{
                        dateString=String.valueOf(dayOfMonth) + "-" +"0"+String.valueOf(monthOfYear+1)
                                + "-" + String.valueOf(year);
                        dateText.setText(dateString);}
                }
                else if(day<10){
                    dateString="0"+String.valueOf(dayOfMonth) + "-" +String.valueOf(monthOfYear+1)
                            + "-" + String.valueOf(year);
                    dateText.setText(dateString);}
                else{
                    dateString=String.valueOf(dayOfMonth) + "-" + String.valueOf(monthOfYear+1)
                            + "-" + String.valueOf(year);
                  dateText.setText(dateString);}
                dayOfWeek.setText(sdf.format(pick));
            }
        }
    };


    private void showTimePicker() {
        TimePickerFragment time = new TimePickerFragment();
        //Set Up Current Time Into dialog
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("hour", calender.get(Calendar.HOUR_OF_DAY));
        args.putInt("minute", calender.get(Calendar.MINUTE));
        time.setArguments(args);
        //Set Call back to capture selected time

        time.setCallBack(ontime);
        time.show(getFragmentManager(), "Time Picker");
    }


    TimePickerDialog.OnTimeSetListener ontime = new TimePickerDialog.OnTimeSetListener() {

        public void onTimeSet(TimePicker view, int hour, int minute) {
            String timeString;
            if (hour < 10) {
                if (minute < 10) {
                    timeString="0" + String.valueOf(hour) + ":0" + String.valueOf(minute);
                    tvTime.setText(timeString);
                } else {
                    timeString="0" + String.valueOf(hour) + ":" + String.valueOf(minute);
                    tvTime.setText(timeString);
                }
            } else if (minute<10) {
                timeString=String.valueOf(hour) + ":0" + String.valueOf(minute);
                tvTime.setText(timeString);
            }else{
                timeString=String.valueOf(hour) + ":" + String.valueOf(minute);
                tvTime.setText(timeString);
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



        //creo il documento per il passaggio
        final String id = profile.getUid()+"_"+dataPassaggio+"_"+ora;


        final String tipo;
        if(campo1.equals(getResources().getString(R.string.Home))){
            tipo = getActivity().getResources().getString(R.string.HomeWork);}
        else {
            tipo = getActivity().getResources().getString(R.string.WorkHome);}



        userrf.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String,Object> autista = documentSnapshot.getData();
                if(autista.get("car") != null){
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
                    onShowRideOfferedListener.onShowRideOffered();

            }
            else{
                    //da far apparire come un alert dialog con bottone modifica che porta alla sezione del profilo
                    //portare l'attività al fragment profilo per inserire la macchina
                    Toast.makeText(getActivity(),getResources().getString(R.string.EntCar),Toast.LENGTH_LONG).show();
                }
            }


            });
        }




    }














