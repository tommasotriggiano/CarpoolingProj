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
    private int year, month, day;
    private EditText data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_ride);

        TextView tvTime = (TextView) findViewById(R.id.tvTime);

        dateView = (TextView) findViewById(R.id.textData);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month + 1, day);
        data= (EditText)findViewById(R.id.data);

        Button btnSetDate = (Button) findViewById(R.id.btnSetDate);
        btnSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            @SuppressWarnings("deprecation")
            public void onClick(View view) {
                showDialog(999);
            }
        });
        data.setOnClickListener(new View.OnClickListener() {
            @Override
            @SuppressWarnings("deprecation")
            public void onClick(View view) {
                showDialog(999);
            }
        });

        ImageButton btnInvert = (ImageButton) findViewById(R.id.Invert);
        btnInvert.setOnClickListener(btnInvertListner);
    }
    public View.OnClickListener btnInvertListner= new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            TextView mHome = (TextView) findViewById(R.id.casa);
            mHome.setText(R.string.lavoro);

            TextView mWork = (TextView) findViewById(R.id.lavoro);
            mWork.setText(R.string.casa);
        }
    };
    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
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


    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }
    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{
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
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user

        }
    }

}



