package it.uniba.di.sms.carpooling;


import java.util.Calendar;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.app.Dialog;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.format.DateFormat;
import android.view.View;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;


public class OfferRideActivity extends AppCompatActivity {
    private DatePicker datePicker;
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day,hour,minute,dayOfWeek;
    private TextView tvTime;
    private EditText posti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_ride);

        tvTime = (TextView) findViewById(R.id.tvTime);

        dateView = (TextView) findViewById(R.id.textData);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute=calendar.get(Calendar.MINUTE);
        dayOfWeek= calendar.get(Calendar.DAY_OF_WEEK);



       /* Button btnSetDate = (Button) findViewById(R.id.btnSetDate);
        btnSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            @SuppressWarnings("deprecation")
            public void onClick(View view) {
                showDialog(999);
            }
        });*/
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            @SuppressWarnings("deprecation")
            public void onClick(View view) {
                showDialog(999);
            }
        });
        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            @SuppressWarnings("deprecation")
            public void onClick(View view) {
                showDialog(100);
            }
        });


        ImageButton btnInvert = (ImageButton) findViewById(R.id.Invert);
        btnInvert.setOnClickListener(btnInvertListner);

        ImageButton btnMinus=(ImageButton) findViewById(R.id.btnMinus);
        btnMinus.setOnClickListener(btnMinusListner);

        ImageButton btnPlus=(ImageButton) findViewById(R.id.btnPlus);
        btnMinus.setOnClickListener(btnPlusListner);
    }
    public View.OnClickListener btnMinusListner= new View.OnClickListener(){
        @Override
        public void onClick(View view) {

            posti=(EditText) findViewById(R.id.textPostiInseriti);
            posti.setText((Integer.parseInt(posti.getText().toString())-1)+"");

        }
    };
    public View.OnClickListener btnPlusListner= new View.OnClickListener(){
        @Override
        public void onClick(View view) {

            posti=(EditText) findViewById(R.id.textPostiInseriti);
            posti.setText((Integer.parseInt(posti.getText().toString())+1)+"");

        }
    };
    public View.OnClickListener btnInvertListner= new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            TextView mHome = (TextView) findViewById(R.id.casa);
            TextView mWork = (TextView) findViewById(R.id.lavoro);
            if(mHome.getText().toString().equals(getResources().getString(R.string.casa))){
                mHome.setText(R.string.lavoro);
                mWork.setText(R.string.casa);
            }else
            {
                mHome.setText(R.string.casa);
                mWork.setText(R.string.lavoro);
            }




        }
    };
    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return new TimePickerDialog(this,myTimeListener, hour, minute, true);
    }


    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year   // arg2 = month     // arg3 = day
                    showDate(arg1, arg2+1, arg3);
                }
            };
    private TimePickerDialog.OnTimeSetListener myTimeListener = new
            TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker arg0,
                                      int arg1, int arg2) {
                    // TODO Auto-generated method stub
                    // arg1 = year   // arg2 = month     // arg3 = day
                    showTime(arg1, arg2);
                }
            };

    private void showTime(int hour,int minute){
        tvTime.setText(new StringBuilder().append(hour).append(":").append(minute));
    }

    private void showDate(int year, int month, int day) {
        if (Locale.getDefault().getLanguage() == "en" ){
            dateView.setText(new StringBuilder().append(month).append("/")
                    .append(day).append("/").append(year));
        }
        else {
            dateView.setText(new StringBuilder().append(day).append("/")
                    .append(month).append("/").append(year));

        }
    }


    /*public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }
    /*public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{
                @Override
                public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
                    final Calendar c = Calendar.getInstance();
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);
        // Create a new instance of TimePickerDialog and return it
                    return new TimePickerDialog(getActivity(), this, hour, minute,
                            DateFormat.is24HourFormat(getActivity()));
                }


        @Override
        public void onTimeSet(TimePicker timePicker, int i, int i1) {

        }
    }*/

}



