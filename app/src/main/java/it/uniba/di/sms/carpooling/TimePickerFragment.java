package it.uniba.di.sms.carpooling;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimePickerFragment extends DialogFragment {


    public TimePickerFragment() {
        // Required empty public constructor
    }

    TimePickerDialog.OnTimeSetListener onTimeSet;
    private int hour,minute;

    public void setCallBack(TimePickerDialog.OnTimeSetListener ontime) {
        onTimeSet = ontime;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        hour= args.getInt("hour");
        minute= args.getInt("minute");
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        if (Locale.getDefault().getLanguage() == "en" ) {
            return new TimePickerDialog(getActivity(), onTimeSet, hour, minute, false);
        }else{
            return new TimePickerDialog(getActivity(),onTimeSet,hour,minute,true);
            }
    }


}
