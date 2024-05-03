package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xionce.doctorvetServices.data.Sell;
import com.xionce.doctorvetServices.data.SellsAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;

public class ViewOwnerDebtsActivity extends ViewBaseActivity {

    private static final String TAG = "ViewOwnerDebtsActivity";
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_view_owner_debts);

        recyclerView = findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        toolbar_title.setText("Ventas con saldo impago");
        toolbar_subtitle.setText(/*"Propietario: " +*/ getOwnerName());
        hideToolbarImage();
        hideFab();

        hideActivityContainer();
        setSwipeRefreshLayout();
    }

    private void showOwnerDebts() {
        showProgressBar();
        Integer id_owner = getIdOwner();
        DoctorVetApp.get().getOwnerDebts(id_owner,new DoctorVetApp.VolleyCallbackAdapter() {
            @Override
            public void onSuccess(RecyclerView.Adapter resultAdapter) {
                try {
                    showActivityContainer();
                    if (resultAdapter != null) {
                        setUI(resultAdapter);
                        setLoadedFinished();
                    } else {
                        showErrorMessage();
                    }
                } catch (Exception ex) {
                    DoctorVetApp.get().handle_error(ex, TAG, true);
                    showErrorMessage();
                } finally {
                    hideProgressBar();
                    hideSwipeRefreshLayoutProgressBar();
                }
            }
        });
    }
    @Override
    protected void setUI(Object object) {
        SellsAdapter sellsAdapter = (SellsAdapter)object;
        sellsAdapter.setOnClickHandler(new HelperClass.AdapterOnClickHandler() {
            @Override
            public void onClick(Object data, View view, int pos) {
                Sell sell = (Sell) data;
                Intent activity = new Intent(ViewOwnerDebtsActivity.this, ViewSellActivity.class);
                activity.putExtra(DoctorVetApp.INTENT_VALUES.SELL_ID.name(), sell.getId());
                startActivity(activity);
            }
        });
        recyclerView.setAdapter(sellsAdapter);
    }

    @Override
    protected void invisibilizeEmptyViews() {

    }

    @Override
    protected void refreshView() {
        showOwnerDebts();
    }

    @Override
    protected void go_update() {
    }

    private void go_insert_pet() {
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

    private Integer getIdOwner() {
        return getIntent().getIntExtra(DoctorVetApp.INTENT_VALUES.OWNER_ID.name(), 0);
    }
    private String getOwnerName() {
        return getIntent().getStringExtra("owner_name");
    }

}
