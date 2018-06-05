package it.uniba.di.sms.carpooling;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchRideFragment extends Fragment {
    private TextView dateText;
    private TextView tvTime;
    ImageButton btnInvert;
    TextView mWork;
    TextView mHome;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.search_ride, container,false);
        dateText=(TextView)view.findViewById(R.id.textData);
        tvTime = (TextView) view.findViewById(R.id.tvTime);
        btnInvert = (ImageButton) view.findViewById(R.id.Invert);
        mHome = (TextView) view.findViewById(R.id.casa);
        mWork = (TextView) view.findViewById(R.id.lavoro);


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
    }


    public View.OnClickListener btnInvertListener= new View.OnClickListener(){
        @Override
        public void onClick(View view) {

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
                dateText.setText(String.valueOf(monthOfYear+1) + "/" + String.valueOf(dayOfMonth)
                        + "/" + String.valueOf(year));
            }else{
                dateText.setText(String.valueOf(dayOfMonth) + "/" + String.valueOf(monthOfYear+1)
                        + "/" + String.valueOf(year));}
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

}
