package com.xionce.doctorvetServices.utilities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private TextInputLayout textInputLayout;
    private RunnableWithArg onDateSet;
    private Date selectedDate = null;
    //private View view = null;
    //private int year, month, day;
    private Date init_date = null;

    private TextView txt_output;


    public DatePickerFragment() {
    }
    public DatePickerFragment(Date init_date) {
        this.init_date = init_date;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        int year, month, day;
        final Calendar calendar = Calendar.getInstance();

        if (this.init_date != null)
            calendar.setTime(init_date);

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0);
        SimpleDateFormat dateFormat = new SimpleDateFormat(HelperClass.getShortDatePattern(getContext()));
        this.selectedDate = calendar.getTime();
        this.init_date = calendar.getTime();
        String dateString = dateFormat.format(this.selectedDate);

        if (textInputLayout != null)
            textInputLayout.getEditText().setText(dateString);

        if (txt_output != null)
            txt_output.setText(dateString);

        if (onDateSet != null)
            onDateSet.run(this.selectedDate);
    }

    public void showDatePickerDialog(FragmentManager fragmentManager, TextInputLayout returnText) {
        DatePickerFragment newFragment = new DatePickerFragment(this.init_date);
        newFragment.textInputLayout = returnText;
        newFragment.show(fragmentManager, "datePicker");
    }
    public void showDatePickerDialog_textview(FragmentManager fragmentManager, TextView output) {
        DatePickerFragment newFragment = new DatePickerFragment(this.init_date);
        newFragment.txt_output = output;
        newFragment.show(fragmentManager, "datePicker");
    }

    //    public void showDatePickerDialog(FragmentManager fragmentManager, TextInputLayout returnText, Date init_date) { // int year, int month, int day) {
////        Calendar cal = Calendar.getInstance();
////        cal.setTime(init_date);
////        this.year = cal.get(Calendar.YEAR);
////        this.month = cal.get(Calendar.MONTH);
////        this.day = cal.get(Calendar.DAY_OF_MONTH);
//        DatePickerFragment newFragment = new DatePickerFragment();
//        newFragment.textInputLayout = returnText;
//        newFragment.show(fragmentManager, "datePicker");
//    }
    public void showDatePickerDialog(FragmentManager fragmentManager, RunnableWithArg<Date> onDateSet) {
        DatePickerFragment newFragment = new DatePickerFragment(this.init_date);
        newFragment.onDateSet = onDateSet;
        newFragment.show(fragmentManager, "datePicker");
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public interface RunnableWithArg<T> {

        /**
         * Similar to the runnable interface but it accepts an argument
         *
         * @param arg any type of argument
         */
        public void run(T arg);

    }

}

