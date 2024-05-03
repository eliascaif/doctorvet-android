package com.xionce.doctorvetServices;

import static com.xionce.doctorvetServices.utilities.HelperClass.getDateForMySQL;
import static com.xionce.doctorvetServices.utilities.HelperClass.getLongDatePattern;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xionce.doctorvetServices.data.DailyCashAdapter;
import com.xionce.doctorvetServices.data.Daily_cash;
import com.xionce.doctorvetServices.data.Get_pagination;
import com.xionce.doctorvetServices.utilities.DatePickerFragment;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainTabDailyCashFragment extends FragmentBase
        implements HelperClass.AdapterOnClickHandler, HelperClass.RecyclerOnPaginationHandler {

    private static final String TAG = "MainTabDailyCashFragment";
    private Button btnDate;
    //private PaginationRecyclerView recyclerView;
    private DailyCashAdapter dailyCashAdapter;
    private TextView txt_cash_in;
    private TextView txt_cash_out;
    private TextView txt_balance;
    private TextView txt_details;
    private TextView txtEmptySelection;

    public MainTabDailyCashFragment() {
    }

    @Override
    public View provideYourFragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_daily_cash, parent, false);
        setSwipeRefreshLayout(rootView);

        txt_cash_in = rootView.findViewById(R.id.txt_cash_in);
        txt_cash_out = rootView.findViewById(R.id.txt_cash_out);
        txt_balance = rootView.findViewById(R.id.txt_balance);
        txt_details = rootView.findViewById(R.id.txt_details);
        txtEmptySelection = rootView.findViewById(R.id.txt_empty_daily_cash);

        btnDate = rootView.findViewById(R.id.btn_date);
        setSelectedDate(Calendar.getInstance().getTime());

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment datePickerFragment = new DatePickerFragment(getSelectedDate(btnDate.getText().toString()));
                datePickerFragment.showDatePickerDialog(getActivity().getSupportFragmentManager(),
                        arg -> {
                            setSelectedDate(arg);
                            refreshView();
                        });
            }
        });

        txt_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = getDateForMySQL(getSelectedDate(btnDate.getText().toString()));
                Intent activity = new Intent(getActivity(), ViewDailyCashDetails.class);
                activity.putExtra(DoctorVetApp.INTENT_DATE, date);
                startActivity(activity);
            }
        });

        recyclerView = rootView.findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshView();
    }

    @Override
    public void refreshView() {
        if (created) {
            showProgressBar();
            hideDisplayMessage();
            recyclerView.setVisibility(View.INVISIBLE);

            String dateStr = btnDate.getText().toString();
            Date selectedDate = getSelectedDate(dateStr);

            DoctorVetApp.get().getDailyCashPagination(selectedDate, 1, new DoctorVetApp.VolleyCallbackPagination() {
                @Override
                public void onSuccess(Get_pagination pagination) {
                    try {
                        Daily_cash.Get_pagination_daily_cash daily_cash_pagination = (Daily_cash.Get_pagination_daily_cash)pagination;

                        ArrayList<Daily_cash> daily_cashes = daily_cash_pagination.getContent();
                        dailyCashAdapter = new DailyCashAdapter(daily_cashes, MainTabDailyCashFragment.this);

                        //resume
                        BigDecimal in = daily_cash_pagination.getTotal_in();
                        BigDecimal out = daily_cash_pagination.getTotal_out();
                        BigDecimal balance = in.subtract(out);
                        txt_cash_in.setText("Ingresos: " + HelperClass.formatCurrency(in));
                        txt_cash_out.setText("Egresos: " + HelperClass.formatCurrency(out));
                        txt_balance.setText("Balance: " + HelperClass.formatCurrency(balance));

                        if (dailyCashAdapter.getItemCount() != 0) {

                            recyclerView.setAdapter(dailyCashAdapter);

                            //pagination
                            recyclerView.resetPage();
                            recyclerView.setOnPaginationHandler(MainTabDailyCashFragment.this);

                            recyclerView.setVisibility(View.VISIBLE);
                            txtEmptySelection.setVisibility(View.GONE);
                        } else {
                            txtEmptySelection.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception ex) {
                        DoctorVetApp.get().handle_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                    } finally {
                        hideProgressBar();
                        hideSwipeRefreshLayoutProgressBar();
                    }
                }
            });
        }
    }

    private Date getSelectedDate(String dateStr) {
        Date selectedDate = null;

        SimpleDateFormat format = new SimpleDateFormat(getLongDatePattern(DoctorVetApp.get().getApplicationContext()));
        try {
            selectedDate = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return selectedDate;
    }
    private void setSelectedDate(Date selectedDate) {
        btnDate.setText(HelperClass.getDateInLocaleLong(selectedDate));
    }

    @Override
    public void onClick(Object data, View view, int pos) {
        Daily_cash daily_cash = (Daily_cash) data;
        String type = daily_cash.getType();
        switch (type) {
            case "SELL":
                Intent sell_intent = new Intent(getActivity(), ViewSellActivity.class);
                sell_intent.putExtra(DoctorVetApp.INTENT_VALUES.SELL_ID.name(), daily_cash.getId());
                startActivity(sell_intent);
                break;
            case "MANUAL_CASH_IN":
            case "MANUAL_CASH_OUT":
                Intent manual_cash_intent = new Intent(getActivity(), ViewCashMovementActivity.class);
                manual_cash_intent.putExtra(DoctorVetApp.INTENT_VALUES.CASH_MOVEMENT_ID.name(), daily_cash.getId());
                startActivity(manual_cash_intent);
                break;
            case "PURCHASE":
                Intent purchase_intent = new Intent(getActivity(), ViewPurchaseActivity.class);
                purchase_intent.putExtra(DoctorVetApp.INTENT_VALUES.PURCHASE_ID.name(), daily_cash.getId());
                startActivity(purchase_intent);
                break;
            case "SPENDING":
                Intent spending_intent = new Intent(getActivity(), ViewSpendingActivity.class);
                spending_intent.putExtra(DoctorVetApp.INTENT_VALUES.SPENDING_ID.name(), daily_cash.getId());
                startActivity(spending_intent);
                break;
        }
    }

    @Override
    public void onPagination() {
        recyclerView.startLoading();
        showProgressBar();
        recyclerView.addPage();

        Date date = getSelectedDate(btnDate.getText().toString());

        DoctorVetApp.get().getDailyCashPagination(date, recyclerView.getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();
                if (pagination != null) {
                    ArrayList<Daily_cash> daily_cashes = ((Daily_cash.Get_pagination_daily_cash)pagination).getContent();
                    dailyCashAdapter.addItems(daily_cashes);
                    recyclerView.finishLoading();
                } else {
                    DoctorVetApp.get().handle_null_adapter("Daily_cash", TAG, true);
                    showErrorMessage();
                }
            }
        });
    }
}
