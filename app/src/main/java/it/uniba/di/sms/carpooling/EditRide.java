package it.uniba.di.sms.carpooling;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by loiodice on 06/09/2018.
 */

public class EditRide  extends AppCompatActivity {
    private HashMap<String,Object> passaggio;
    private TextView dateText,tvTime,dayOfWeek,mWork,mHome,posti,errorDate,errorTime;
    private ImageButton btnInvert;
    private String postiString,direzione,idPassaggio;
    private  int postiIns;
    private ImageView btnPlus,btnMinus,img_date,img_time;
    private Button edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_ride);

        //getActionBar().setTitle(R.string.editRide);
        dateText=(TextView)findViewById(R.id.textData);
        tvTime = (TextView) findViewById(R.id.tvTime);
        btnInvert = (ImageButton)findViewById(R.id.Invert);
        errorDate = (TextView) findViewById(R.id.errorDate);
        errorTime = (TextView)findViewById(R.id.errorTime);
        mHome = (TextView) findViewById(R.id.casa);
        mWork = (TextView)findViewById(R.id.lavoro);
        posti=(TextView)findViewById(R.id.textPostiInseriti);
        postiIns= Integer.parseInt(posti.getText().toString());
        edit = (Button)findViewById(R.id.btnEdit);
        dayOfWeek=(TextView)findViewById(R.id.day) ;
        img_date=(ImageView)findViewById(R.id.img_error_date) ;
        img_time=(ImageView)findViewById(R.id.img_error_time) ;
        btnMinus=(ImageView) findViewById(R.id.btnMinus);
        btnPlus=(ImageView) findViewById(R.id.btnPlus);

        Intent receive = getIntent();
        passaggio= (HashMap<String, Object>) receive.getSerializableExtra("passaggio");
        idPassaggio = passaggio.get("idPassaggio").toString();

        tvTime.setText((String)passaggio.get("ora"));
        //dayOfWeek.setText((String)passaggio.get("giorno"));
        posti.setText((String)passaggio.get("postiDisponibili"));
        direzione= passaggio.get("tipoViaggio").toString();
         if (direzione.equals("Home-Work")||direzione.equals("Casa-Lavoro")){
             mHome.setText(getResources().getString(R.string.Home));
             mWork.setText(getResources().getString(R.string.Work));
         }else if( direzione.equals("Work-Home")|| direzione.equals("Lavoro-Casa") ){
             mHome.setText(getResources().getString(R.string.Work));
             mWork.setText(getResources().getString(R.string.Home));
         }
         if (Locale.getDefault().getLanguage().equals("en")){
             String dp = passaggio.get("dataPassaggio").toString();
             String d[] = dp.split("-");
             String dataPassaggio = d[1]+"-"+d[0]+"-"+d[2];
             dateText.setText(dataPassaggio);
         }else {
             dateText.setText((String)passaggio.get("dataPassaggio"));
         }



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
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //implementa il metodo tommaso
                //editRide();
                Intent back= new Intent(EditRide.this,OfferedMapActivity.class);
                startActivity(back);
                finish();
            }
        });

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
        date.show(date.getFragmentManager(), "Date Picker");
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
        time.show(time.getFragmentManager(), "Time Picker");
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
            Calendar cal=Calendar.getInstance();
            String [] date= dateText.getText().toString().split("-");
            if ((Integer.parseInt(date[0])==(cal.get(Calendar.DAY_OF_MONTH))|| (Integer.parseInt(date[0])-1) ==(cal.get(Calendar.MONTH)) ) &&
                    (((Integer.parseInt(date[1]))-1) ==(cal.get(Calendar.MONTH))|| Integer.parseInt(date[1])==(cal.get(Calendar.DAY_OF_MONTH)))&&
                    Integer.parseInt(date[2]) ==(cal.get(Calendar.YEAR))){
                if (hour < cal.get(Calendar.HOUR_OF_DAY)){
                    errorTime.setVisibility(View.VISIBLE);
                    errorTime.setText(getResources().getString(R.string.hourNotValid));
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
                } else if(hour==cal.get(Calendar.HOUR_OF_DAY) && minute < cal.get(Calendar.MINUTE)) {
                    errorTime.setVisibility(View.VISIBLE);
                    errorTime.setText(getResources().getString(R.string.hourNotValid));
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
                }
            }
        }

    };


}
