package com.xionce.doctorvetServices.utilities;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private TextInputLayout textInputLayout;
    private Runnable onDateSet;
    private Date selectedTime = null;
    //private int hour;
    //private int minute;
    //private View view = null;
    private Date init_date = null;

    private TextView txt_output;

    public TimePickerFragment() {
    }
    public TimePickerFragment(Date init_date) {
        this.init_date = init_date;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        int hour, minute;
        final Calendar calendar = Calendar.getInstance();

        if (init_date != null)
            calendar.setTime(init_date);

        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);

        boolean is24HourSystem = DateFormat.is24HourFormat(getContext());

        // Create a new instance of DatePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute, is24HourSystem);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(0, 0, 0, hour, minute);
        SimpleDateFormat dateFormat = new SimpleDateFormat(HelperClass.getShortTimePattern(getContext()));
        this.selectedTime = calendar.getTime();
        String dateString = dateFormat.format(this.selectedTime);

        if (textInputLayout != null)
            textInputLayout.getEditText().setText(dateString);

        if (txt_output != null)
            txt_output.setText(dateString);

        if (onDateSet != null)
            onDateSet.run();
    }

    public void showTimePickerDialog(FragmentManager fragmentManager, TextInputLayout returnText) {
        TimePickerFragment newFragment = new TimePickerFragment(this.init_date);
        newFragment.textInputLayout = returnText;
        newFragment.show(fragmentManager, "datePicker");
    }

    public void showTimePickerDialog_textview(FragmentManager fragmentManager, TextView output) {
        TimePickerFragment newFragment = new TimePickerFragment(this.init_date);
        newFragment.txt_output = output;
        newFragment.show(fragmentManager, "datePicker");
    }

    public void showTimePickerDialog(FragmentManager fragmentManager, Runnable onDateSet) {
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.onDateSet = onDateSet;
        newFragment.show(fragmentManager, "datePicker");
    }

    public Date getSelectedTime() {
        return selectedTime;
    }

}

