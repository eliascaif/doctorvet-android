package com.xionce.doctorvetServices;

import static android.view.View.GONE;

import android.content.Intent;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.xionce.doctorvetServices.data.Finance_payment_methodsAdapter;
import com.xionce.doctorvetServices.data.Owner;
import com.xionce.doctorvetServices.data.Pet;
import com.xionce.doctorvetServices.data.Pet_supply;
import com.xionce.doctorvetServices.data.Pet_supplyAdapter;
import com.xionce.doctorvetServices.data.Sell;
import com.xionce.doctorvetServices.data.Sell_itemAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;

public class EditSellActivity_4 extends EditBaseActivity {

    private static final String TAG = "EditSellActivity_4";
    private View list_item_sell_owner;
    private View list_item_sell_pet;
    private Sell sell = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_sell_4);
        toolbar_title.setText("Venta");
        toolbar_subtitle.setText("Revisa que este todo bien");
        list_item_sell_owner = findViewById(R.id.list_item_owner_selector);
        list_item_sell_pet = findViewById(R.id.list_item_pet_selector);
        list_item_sell_owner.findViewById(R.id.img_remove).setVisibility(GONE);
        list_item_sell_pet.findViewById(R.id.img_remove).setVisibility(GONE);
        ImageView img_thumb_owner = list_item_sell_owner.findViewById(R.id.img_thumb);
        ImageView img_thumb_pet = list_item_sell_pet.findViewById(R.id.img_thumb);
        Glide.with(this).load(R.drawable.ic_account_circle_light).apply(RequestOptions.fitCenterTransform()).into(img_thumb_owner);
        Glide.with(this).load(R.drawable.ic_pets_light).apply(RequestOptions.fitCenterTransform()).into(img_thumb_pet);
        hideToolbarImage();
        hideFab();

        Sell sell = getObject();
        setObjectToUI(sell);

        RecyclerView recyclerView_payments = findViewById(R.id.recycler_payments);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EditSellActivity_4.this);
        recyclerView_payments.setLayoutManager(layoutManager);
        recyclerView_payments.setHasFixedSize(true);
        recyclerView_payments.setAdapter(new Finance_payment_methodsAdapter(sell.getPayments(), Finance_payment_methodsAdapter.Finance_payment_methodsAdapter_types.NONE));
        if (sell.getPayments().size() == 0) {
            findViewById(R.id.txt_payments_caption).setVisibility(GONE);
            recyclerView_payments.setVisibility(GONE);
        }

        RecyclerView recyclerView_sell_products = findViewById(R.id.recycler_products);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(EditSellActivity_4.this);
        recyclerView_sell_products.setLayoutManager(layoutManager2);
        recyclerView_sell_products.setHasFixedSize(true);
        recyclerView_sell_products.setAdapter(new Sell_itemAdapter(sell.getItems(), Sell_itemAdapter.SellItemsAdapterTypes.FOR_SELL_INPUT, false));

        //upcoming supply
        if (sell.getUpcoming_supplies() != null && !sell.getUpcoming_supplies().isEmpty()) {
            ArrayList<Pet_supply> upcoming_new_and_updated = sell.getUpcoming_supplies_new_and_updated();
            if (!upcoming_new_and_updated.isEmpty()) {
                RecyclerView recyclerView_upcoming_supply = findViewById(R.id.recycler_supply);
                RecyclerView.LayoutManager layoutManager3 = new LinearLayoutManager(EditSellActivity_4.this);
                recyclerView_upcoming_supply.setLayoutManager(layoutManager3);
                recyclerView_upcoming_supply.setHasFixedSize(true);
                recyclerView_upcoming_supply.setAdapter(new Pet_supplyAdapter(upcoming_new_and_updated, Pet_supplyAdapter.Pet_supplyAdapter_types.PLANNING_ACTIVITY_NO_EDIT));
                findViewById(R.id.txt_supply).setVisibility(View.VISIBLE);
                recyclerView_upcoming_supply.setVisibility(View.VISIBLE);
            }
        }

        Button btn_end = findViewById(R.id.btn_end);
        btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_sell_request();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == HelperClass.REQUEST_FINISH) {
            setResult(HelperClass.REQUEST_FINISH);
            finish();
        }
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
    protected Sell getObject() {
        if (sell == null)
            sell = MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.SELL_OBJ.name()), Sell.class);

        return sell;
    }

    @Override
    protected void setObjectToUI(Object object) {
        Sell sell = (Sell) object;
                
        Owner owner = sell.getOwner();
        list_item_sell_owner.setVisibility(GONE);
        if (owner != null)
            setOwnerInView(owner);

        list_item_sell_pet.setVisibility(GONE);
        Pet pet = sell.getPet();
        if (pet != null)
            setPetInView(pet);

        TextView txt_total = findViewById(R.id.txt_total);
        txt_total.setText("Total: " + HelperClass.formatCurrency(sell.calculateTotal()));

        TextView txt_balance = findViewById(R.id.txt_balance);
        BigDecimal balance = sell.calculateBalance();
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

        //deposit
        if (sell.getVet_info().getIs_multi_deposit_vet() == 1) {
            TextView txt_deposit = findViewById(R.id.txt_deposit);
            txt_deposit.setText("Almacén: " + sell.getDeposit().getName());
            txt_deposit.setVisibility(View.VISIBLE);
        }

        //sell point
        if (sell.getVet_info().getIs_multi_point_vet() == 1) {
            TextView txt_sell_point = findViewById(R.id.txt_sell_point);
//            txt_sell_point.setText("Emite: " + sell.getSell_point().getFinance_types_receipt().getDenomination());
            txt_sell_point.setText("Emite: " + sell.getSell_point().getName());
            txt_sell_point.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected DoctorVetApp.IResourceObject getResourceObject() {
        return null;
    }

    @Override
    protected String getUploadTableName() {
        return null;
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {

    }

    private void send_sell_request() {
        showProgressBar();

        Sell sell = getObject();
        final String sell_json_object = MySqlGson.postGson().toJson(sell);

        android.util.Log.e("tag", sell_json_object);

        URL url = NetworkUtils.buildGetSellsUrl();
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.POST, url.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    Sell sell_response = MySqlGson.getGson().fromJson(data, Sell.class);
                    setResult(HelperClass.REQUEST_FINISH);

                    Intent intent = new Intent(EditSellActivity_4.this, EditSellActivity_5.class);
                    intent.putExtra("id_sell", sell_response.getId());
                    startActivity(intent);

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
                return sell_json_object.getBytes();
            }
        };
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    private void setOwnerInView(Owner owner) {
        list_item_sell_owner.setVisibility(View.VISIBLE);
        ImageView img_thumb = list_item_sell_owner.findViewById(R.id.img_thumb);
        TextView txt_name = list_item_sell_owner.findViewById(R.id.txt_item_name);
        DoctorVetApp.get().setThumb(owner.getThumb_url(), img_thumb, R.drawable.ic_account_circle_light);
        txt_name.setText(owner.getName());
    }
    private void setPetInView(Pet pet) {
        list_item_sell_pet.setVisibility(View.VISIBLE);
        ImageView img_thumb = list_item_sell_pet.findViewById(R.id.img_thumb);
        TextView txt_name = list_item_sell_pet.findViewById(R.id.txt_item_name);
        DoctorVetApp.get().setThumb(pet.getThumb_url(), img_thumb, R.drawable.ic_pets_light);
        txt_name.setText(pet.getName());
    }

}
