package com.xionce.doctorvetServices;

import static android.view.View.GONE;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.xionce.doctorvetServices.data.Finance_payment_methodsAdapter;
import com.xionce.doctorvetServices.data.Product_provider;
import com.xionce.doctorvetServices.data.Purchase;
import com.xionce.doctorvetServices.data.Purchase_itemAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.math.BigDecimal;
import java.net.URL;

public class EditPurchaseActivity_3 extends EditBaseActivity {

    private static final String TAG = "EditSellActivity_3";
    private View list_item_purchase_provider;
    private Purchase purchase = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_purchase_3);
        toolbar_title.setText("Compra");
        toolbar_subtitle.setText("Revisa que este todo bien");
        list_item_purchase_provider = findViewById(R.id.list_item_provider);
        list_item_purchase_provider.findViewById(R.id.img_remove).setVisibility(GONE);
        hideToolbarImage();
        hideFab();

        Purchase purchase = getObject();
        setObjectToUI(purchase);

        RecyclerView recyclerView_payments = findViewById(R.id.recycler_payments);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EditPurchaseActivity_3.this);
        recyclerView_payments.setLayoutManager(layoutManager);
        recyclerView_payments.setHasFixedSize(true);
        recyclerView_payments.setAdapter(new Finance_payment_methodsAdapter(purchase.getPayments(), Finance_payment_methodsAdapter.Finance_payment_methodsAdapter_types.NONE));
        if (purchase.getPayments().size() == 0) {
            findViewById(R.id.txt_payments_caption).setVisibility(GONE);
            recyclerView_payments.setVisibility(GONE);
        }

        RecyclerView recyclerView_sell_products = findViewById(R.id.recycler_products);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(EditPurchaseActivity_3.this);
        recyclerView_sell_products.setLayoutManager(layoutManager2);
        recyclerView_sell_products.setHasFixedSize(true);
        recyclerView_sell_products.setAdapter(new Purchase_itemAdapter(purchase.getItems(), Purchase_itemAdapter.PurchaseDetailAdapter_types.FOR_PURCHASE_INPUT, false));

        Button btn_end = findViewById(R.id.btn_end);
        btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_purchase_request();
            }
        });

    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {

    }

    @Override
    protected void save() {

    }

    @Override
    protected void update() {

    }

    @Override
    protected URL getUrl() {
        return null;
    }

    @Override
    protected Object getObjectFromUI() {
        return null;
    }

    @Override
    protected Purchase getObject() {
        if (purchase == null)
            purchase = MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.PURCHASE_OBJ.name()), Purchase.class);

        return purchase;
    }

    @Override
    protected void setObjectToUI(Object object) {
        Purchase purchase = (Purchase) object;

        Product_provider provider = purchase.getProvider();
        list_item_purchase_provider.setVisibility(GONE);
        if (provider != null)
            setProviderInView(provider);

        TextView txt_total = findViewById(R.id.txt_total);
        txt_total.setText("Total: " + HelperClass.formatCurrency(purchase.calculateTotal()));

        TextView txt_balance = findViewById(R.id.txt_balance);
        BigDecimal balance = purchase.calculateBalance();
        txt_balance.setVisibility(View.VISIBLE);
        if (balance.compareTo(BigDecimal.ZERO) == 0) {
            txt_balance.setVisibility(View.GONE);
        } else if (balance.compareTo(BigDecimal.ZERO) < 0) {
            txt_balance.setText("Cambio: " + HelperClass.formatCurrency(balance.abs()));
        } else if (balance.compareTo(BigDecimal.ZERO) >= 0) {
            txt_balance.setText("Deuda: " + HelperClass.formatCurrency(balance));
        }

        //products and services bulk count
        TextView txt_products_details = findViewById(R.id.txt_products_details);
        String details = getObject().getProductsDetails();
//        txt_products_details.setText("");
//        txt_products_details.setVisibility(View.GONE);
//        if (!details.isEmpty()) {
            txt_products_details.setVisibility(View.VISIBLE);
            txt_products_details.setText(details);
//        }
    }

    @Override
    protected DoctorVetApp.IResourceObject getResourceObject() {
        return null;
    }

    @Override
    protected String getUploadTableName() {
        return null;
    }

    private void send_purchase_request() {
        showWaitDialog();

        Purchase purchase = getObject();
        final String purchase_json_object = MySqlGson.postGson().toJson(purchase);
        URL purchaseEndPoint = NetworkUtils.buildGetPurchasesUrl();
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.POST, purchaseEndPoint.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    Purchase purchase_response = MySqlGson.getGson().fromJson(data, Purchase.class);
                    setResult(HelperClass.REQUEST_FINISH);

                    finish();
                } catch (Exception ex) {
                    DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                } finally {
                    hideWaitDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DoctorVetApp.get().handle_volley_error(error, TAG, true);
                hideWaitDialog();
            }
        })
        {
            @Override
            public byte[] getBody() {
                return purchase_json_object.getBytes();
            }
        };
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    private void setProviderInView(Product_provider provider) {
        list_item_purchase_provider.setVisibility(View.VISIBLE);
        ImageView img_thumb = list_item_purchase_provider.findViewById(R.id.img_thumb);
        TextView txt_name = list_item_purchase_provider.findViewById(R.id.txt_item_name);
        DoctorVetApp.get().setThumb(provider.getThumb_url(), img_thumb, R.drawable.ic_provider);
        txt_name.setText(provider.getName());
    }

}
