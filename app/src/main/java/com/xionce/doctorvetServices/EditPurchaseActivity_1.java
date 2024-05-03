package com.xionce.doctorvetServices;

import static com.xionce.doctorvetServices.utilities.HelperClass.REQUEST_READ_BARCODE;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.xionce.doctorvetServices.data.Finance_payment_method;
import com.xionce.doctorvetServices.data.Product_provider;
import com.xionce.doctorvetServices.data.Purchase;
import com.xionce.doctorvetServices.data.Purchase_itemAdapter;
import com.xionce.doctorvetServices.data.Vet_deposit;
import com.xionce.doctorvetServices.data.VetsDepositsAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class EditPurchaseActivity_1 extends EditBaseActivity {

    private static final String TAG = "EditPurchaseActivity_1";
    private TextView txt_total;
    //private TextView txt_products_details;
    private View list_item_provider;
    private TextInputLayout txtDate;
    private TextInputLayout txtHour;
    private TextInputLayout txtReceipt;
    private Spinner spinnerDeposit;

    private ArrayList<Finance_payment_method> payments_types;
    private Purchase purchase = null;
    private Purchase_itemAdapter purchaseItemsAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_purchase_1);
        toolbar_title.setText("Compra");
        toolbar_subtitle.setText("Selecciona fecha, proveedor y agrega productos");
        TextView txt_add_products_by_keyboard = findViewById(R.id.txt_add_products_by_keyboard);
        TextView txt_add_products_by_barcode = findViewById(R.id.txt_add_products_by_barcode);
        txt_total = findViewById(R.id.txt_total);
        //txt_products_details = findViewById(R.id.txt_products_details);
        txtDate = findViewById(R.id.txt_date);
        txtHour = findViewById(R.id.txt_hour);
        txtReceipt = findViewById(R.id.txt_receipt);
        list_item_provider = findViewById(R.id.list_item_provider);
        spinnerDeposit = findViewById(R.id.spinner_deposit);
        hideToolbarImage();
        hideFab();

        if (savedInstanceState == null) {
            initializeInitRequestNumber(1);
            DoctorVetApp.get().getPurchasesForInput(new DoctorVetApp.VolleyCallbackObject() {
                @Override
                public void onSuccess(Object resultObject) {
                    setRequestCompleted();
                    Purchase.PurchaseForInput purchasesForInput = (Purchase.PurchaseForInput) resultObject;
                    setDepositAdapter(purchasesForInput);
                    getObject().setDeposit(purchasesForInput.getDeposits().get(0));
                    payments_types = purchasesForInput.getFinance_types_payments();
                }
            });
        } else {
            restoreFromBundle(savedInstanceState);
        }

        Purchase purchase = getObject();
        setObjectToUI(purchase);

        //setup recyclerview
        RecyclerView recyclerView = findViewById(R.id.recycler_products);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EditPurchaseActivity_1.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        purchaseItemsAdapter = new Purchase_itemAdapter(getObject().getItems(), Purchase_itemAdapter.PurchaseDetailAdapter_types.FOR_PURCHASE_INPUT);
        recyclerView.setAdapter(purchaseItemsAdapter);
        purchaseItemsAdapter.setOnRemoveItemHandler(new HelperClass.AdapterOnRemoveItemHandler() {
            @Override
            public void onRemoveItem(Object data, View view, int pos) {
                calculateTotal();
            }
        });

        calculateTotal();

        txt_add_products_by_keyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditPurchaseActivity_1.this, EditPurchaseActivity_1_1.class);
                intent.putExtra(DoctorVetApp.INTENT_VALUES.PURCHASE_OBJ.name(), MySqlGson.getGson().toJson(getObjectFromUI()));
                startActivityForResult(intent, HelperClass.REQUEST_UPDATE);
            }
        });

        txt_add_products_by_barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (HelperClass.checkPermission(android.Manifest.permission.CAMERA, getApplicationContext())) {
                    Intent intent = new Intent(EditPurchaseActivity_1.this, EditPurchaseActivity_1_1_2.class);
                    intent.putExtra(DoctorVetApp.INTENT_VALUES.PURCHASE_OBJ.name(), MySqlGson.postGson().toJson(getObjectFromUI()));
                    startActivityForResult(intent, HelperClass.REQUEST_UPDATE);
                } else {
                    HelperClass.requestPermission(EditPurchaseActivity_1.this, Manifest.permission.CAMERA, REQUEST_READ_BARCODE);
                }
            }
        });

        list_item_provider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditPurchaseActivity_1.this, SearchProviderActivity.class);
                intent.putExtra(DoctorVetApp.REQUEST_SEARCH_FOR, DoctorVetApp.INTENT_VALUES.PRODUCT_PROVIDER_OBJ.name());
                intent.putExtra(DoctorVetApp.INTENT_SEARCH_RETURN, true);
                startActivityForResult(intent, HelperClass.REQUEST_SEARCH);
            }
        });
        list_item_provider.findViewById(R.id.img_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeProviderInView();
            }
        });

        ImageView dateSearch = findViewById(R.id.img_search_date);
        dateSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDate();
            }
        });
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDate();
            }
        });
        txtDate.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDate();
            }
        });

        ImageView timeSearch = findViewById(R.id.img_search_time);
        timeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchTime();
            }
        });
        txtHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchTime();
            }
        });
        txtHour.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchTime();
            }
        });

        Button btn_continue = findViewById(R.id.btn_continue);
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
        });

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(DoctorVetApp.INTENT_VALUES.PURCHASE_OBJ.name(), MySqlGson.getGson().toJson(getObjectFromUI()));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString(DoctorVetApp.INTENT_VALUES.PURCHASE_OBJ.name());
        purchase = MySqlGson.getGson().fromJson(objectInString, Purchase.class);

        String purchasesForInputInString = DoctorVetApp.get().readFromDisk("purchases_for_input");
        Purchase.PurchaseForInput purchaseForInput = MySqlGson.getGson().fromJson(purchasesForInputInString, Purchase.PurchaseForInput.class);
        setDepositAdapter(purchaseForInput);
        payments_types = purchaseForInput.getFinance_types_payments();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == HelperClass.REQUEST_FINISH)
            finish();

        if (resultCode != RESULT_OK) return;

        //busquedas
        if (requestCode == HelperClass.REQUEST_SEARCH && data != null) {
            String searchFor = data.getExtras().getString(DoctorVetApp.REQUEST_SEARCH_FOR, "");
            if (searchFor.equals(DoctorVetApp.INTENT_VALUES.PRODUCT_PROVIDER_OBJ.name())) {
                Product_provider productProvider = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_PROVIDER_OBJ.name()), Product_provider.class);
                getObject().setProvider(productProvider.getPolish());
                setProviderInView(productProvider);
            }
        }

        //TODO: prevent hardwipeo
        if (data != null && data.hasExtra(DoctorVetApp.INTENT_VALUES.PURCHASE_OBJ.name())) {
            String purchaseInString = data.getStringExtra(DoctorVetApp.INTENT_VALUES.PURCHASE_OBJ.name());
            Purchase purchase = MySqlGson.getGson().fromJson(purchaseInString, Purchase.class);
            purchaseItemsAdapter.updateList(purchase.getItems());
            calculateTotal();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length == 0) return;

        boolean permissionToTakePhotoAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        if (permissionToTakePhotoAccepted) {
            Intent intent = new Intent(EditPurchaseActivity_1.this, EditPurchaseActivity_1_1_2.class);
            intent.putExtra(DoctorVetApp.INTENT_VALUES.PURCHASE_OBJ.name(), MySqlGson.postGson().toJson(getObjectFromUI()));
            startActivityForResult(intent, HelperClass.REQUEST_UPDATE);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
    protected Purchase getObjectFromUI() {
        Purchase purchase = getObject();

        //date
        String datetime = txtDate.getEditText().getText().toString() + " " + txtHour.getEditText().getText().toString();
        purchase.setDate(HelperClass.getShortDateTime(datetime, getBaseContext()));

        //receipt
        purchase.setReceipt(txtReceipt.getEditText().getText().toString());

        return purchase;
    }

    @Override
    protected Purchase getObject() {
        if (purchase == null) {
            purchase = new Purchase();
            purchase.setDate(Calendar.getInstance().getTime());
            purchase.setItems(new ArrayList<>());

            Intent intent = getIntent();
            if (intent.hasExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_PROVIDER_OBJ.name()))
                purchase.setProvider(MySqlGson.getGson().fromJson(intent.getStringExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_PROVIDER_OBJ.name()), Product_provider.class));
        }

        return purchase;
    }

    @Override
    protected void setObjectToUI(Object object) {
        Purchase purchase = (Purchase) object;

        txtDate.getEditText().setText(HelperClass.getDateInLocaleShort(purchase.getDate()));
        txtHour.getEditText().setText(HelperClass.getTimeInLocale(purchase.getDate(), getBaseContext()));

        Product_provider provider = purchase.getProvider();
        if (provider != null)
            setProviderInView(provider);

        if (purchase.getReceipt() != null)
            txtReceipt.getEditText().setText(purchase.getReceipt());

    }

    @Override
    protected DoctorVetApp.IResourceObject getResourceObject() {
        return null;
    }

    @Override
    protected String getUploadTableName() {
        return null;
    }

    private void setDepositAdapter(Purchase.PurchaseForInput purchasesForInput) {
        if (purchasesForInput.getVet_info().getIs_multi_deposit_vet() == 1) {
            findViewById(R.id.lyt_deposit).setVisibility(View.VISIBLE);
            VetsDepositsAdapter vetsDepositsAdapter = new VetsDepositsAdapter(purchasesForInput.getDeposits());
            spinnerDeposit.setAdapter(vetsDepositsAdapter.getArrayAdapter(EditPurchaseActivity_1.this));
            spinnerDeposit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Vet_deposit vetDeposit = (Vet_deposit) adapterView.getItemAtPosition(i);
                    getObject().setDeposit(vetDeposit);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    getObject().setDeposit(null);
                }
            });
        }
    }

    private void next() {
        if (!validate_deposit()) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), "Selecciona un almacen", Snackbar.LENGTH_LONG).show();
            return;
        }

        if (!validate_something_to_register()) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), "Agrega productos", Snackbar.LENGTH_LONG).show();
            return;
        }

        Purchase purchase = getObjectFromUI();

        Intent intent = new Intent(EditPurchaseActivity_1.this, EditPurchaseActivity_2.class);
        intent.putExtra(DoctorVetApp.INTENT_VALUES.PURCHASE_OBJ.name(), MySqlGson.getGson().toJson(purchase));
        intent.putExtra(DoctorVetApp.INTENT_VALUES.PAYMENTS_TYPES_LIST.name(), MySqlGson.getGson().toJson(payments_types));
        startActivityForResult(intent, 1);
    }
    private void setProviderInView(Product_provider provider) {
        ImageView img_thumb = list_item_provider.findViewById(R.id.img_thumb);
        TextView txt_name = list_item_provider.findViewById(R.id.txt_item_name);
        DoctorVetApp.get().setThumb(provider.getThumb_url(), img_thumb, R.drawable.ic_provider);
        txt_name.setText(provider.getName());
    }
    private void removeProviderInView() {
        getObject().setProvider(null);
        ImageView img_thumb = list_item_provider.findViewById(R.id.img_thumb);
        TextView txt_name = list_item_provider.findViewById(R.id.txt_item_name);
        Context ctx = img_thumb.getContext();
        Glide.with(ctx).load(R.drawable.ic_provider).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
        txt_name.setText("SELECCIONAR");
    }
    private void calculateTotal() {
        txt_total.setText("Total: " + purchaseItemsAdapter.getTotal().toString());

        //products details
//        String details = getObject().getProductsDetails();
//        txt_products_details.setText("");
//        txt_products_details.setVisibility(View.GONE);
//        if (!details.isEmpty()) {
//            txt_products_details.setVisibility(View.VISIBLE);
//            txt_products_details.setText(details);
//        }

    }
    private void searchDate() {
        DoctorVetApp.get().searchDateSetInit(txtDate, getSupportFragmentManager());
    }
    private void searchTime() {
        DoctorVetApp.get().searchTimeSetInit(txtHour, getSupportFragmentManager());
    }

    private boolean validate_something_to_register() {
        return getObject().getItems().size() != 0;
    }
    private boolean validate_deposit() {
        return getObject().getDeposit() != null;
    }

}
