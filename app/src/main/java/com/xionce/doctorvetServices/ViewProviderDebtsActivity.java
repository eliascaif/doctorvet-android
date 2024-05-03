package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xionce.doctorvetServices.data.Purchase;
import com.xionce.doctorvetServices.data.PurchasesAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;

public class ViewProviderDebtsActivity extends ViewBaseActivity {

    private static final String TAG = "ViewProviderDebtsActivi";
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_view_provider_debts);

        recyclerView = findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        toolbar_title.setText("Compras con saldo impago");
        toolbar_subtitle.setText("Distribuidor: " + getProviderName());
        hideToolbarImage();

        hideActivityContainer();
        setSwipeRefreshLayout();
    }

    private void showProviderDebts() {
        showProgressBar();
        Integer id_provider = getIdProvider();
        DoctorVetApp.get().getProviderDebts(id_provider,new DoctorVetApp.VolleyCallbackAdapter() {
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
                    DoctorVetApp.get().handle_error(ex, /*ViewProviderDebtsActivity.this,*/ TAG, true);
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
        PurchasesAdapter purchasesAdapter = (PurchasesAdapter) object;
        purchasesAdapter.setOnClickHandler(new HelperClass.AdapterOnClickHandler() {
            @Override
            public void onClick(Object data, View view, int pos) {
                Purchase purchase = (Purchase) data;
                Intent activity = new Intent(ViewProviderDebtsActivity.this, ViewPurchaseActivity.class);
                activity.putExtra(DoctorVetApp.INTENT_VALUES.PURCHASE_ID.name(), purchase.getId());
                startActivity(activity);
            }
        });
        recyclerView.setAdapter(purchasesAdapter);
    }

    @Override
    protected void invisibilizeEmptyViews() {

    }

    @Override
    protected void refreshView() {
        showProviderDebts();
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

    private Integer getIdProvider() {
        return getIntent().getIntExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_PROVIDER_ID.name(), 0);
    }
    private String getProviderName() {
        return getIntent().getStringExtra("provider_name");
    }

}
