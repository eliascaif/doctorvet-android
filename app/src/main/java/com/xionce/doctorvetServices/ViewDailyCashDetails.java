package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.xionce.doctorvetServices.data.DailyCashDetails;
import com.xionce.doctorvetServices.data.DailyCashDetailsAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.controls.PaginationRecyclerView;

public class ViewDailyCashDetails extends ViewBaseActivity {

    private static final String TAG = "ViewDailyCashDetails";
    private PaginationRecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_view_daily_cash_details);

        recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ViewDailyCashDetails.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        toolbar_title.setText("Caja diaria " + HelperClass.getDateInLocaleShort(HelperClass.getDate(getDate())));
        toolbar_image.setVisibility(View.GONE);
        toolbar_subtitle.setVisibility(View.GONE);

        hideFab();
        hideActivityContainer();
        setSwipeRefreshLayout();
    }

    private void showInfo() {
        showProgressBar();
        String date = getDate();
        DoctorVetApp.get().getDailyCashDetails(date, new DoctorVetApp.VolleyCallbackDailyCashDetails() {
            @Override
            public void onSuccess(DailyCashDetails dailyCashDetails) {
                try {
                    showActivityContainer();
                    if (dailyCashDetails != null) {
                        setUI(dailyCashDetails);
                        setLoadedFinished();
                    } else {
                        showErrorMessage();
                    }
                } catch (Exception ex) {
                    DoctorVetApp.get().handle_error(ex, /*ViewDailyCashDetails.this,*/ TAG, true);
                    showErrorMessage();
                } finally {
                    hideProgressBar();
                    hideSwipeRefreshLayoutProgressBar();
                }
            }
        });

    }

    private String getDate() {
        return getIntent().getStringExtra(DoctorVetApp.INTENT_DATE);
    }

    @Override
    protected void refreshView() {
        showInfo();
    }

    @Override
    protected void go_update() {

    }

    @Override
    protected void go_delete() {

    }

    @Override
    protected void on_update_complete(Intent data) {

    }

    @Override
    protected void on_delete_complete(Integer deleted_id) {

    }

    @Override
    protected void setUI(Object object) {
        DailyCashDetails dailyCashDetails = (DailyCashDetails) object;
        DailyCashDetailsAdapter adapter  = new DailyCashDetailsAdapter(dailyCashDetails.getDetails());
        recyclerView.setAdapter(adapter);

        TextView txt_total_sells = findViewById(R.id.txt_total_sells);
        txt_total_sells.setText("Ingresos: " + HelperClass.formatCurrency(dailyCashDetails.getTotal_sells()));

        TextView txt_total_manual_cash_in = findViewById(R.id.txt_total_manual_cash_in);
        txt_total_manual_cash_in.setText("Ingresos manuales: " + HelperClass.formatCurrency(dailyCashDetails.getTotal_manual_cash_in()));

        TextView txt_total_spendings = findViewById(R.id.txt_total_spendings);
        txt_total_spendings.setText("Gastos: " + HelperClass.formatCurrency(dailyCashDetails.getTotal_spendings()));

        TextView txt_total_manual_cash_out = findViewById(R.id.txt_total_manual_cash_out);
        txt_total_manual_cash_out.setText("Egresos manuales: " + HelperClass.formatCurrency(dailyCashDetails.getTotal_manual_cash_out()));

        TextView txt_total_buys = findViewById(R.id.txt_total_buys);
        txt_total_buys.setText("Egresos: " + HelperClass.formatCurrency(dailyCashDetails.getTotal_purchases()));
    }

    @Override
    protected void invisibilizeEmptyViews() {

    }
}