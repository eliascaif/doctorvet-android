package com.xionce.doctorvetServices;

import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.xionce.doctorvetServices.data.Product;
import com.xionce.doctorvetServices.data.Purchase;
import com.xionce.doctorvetServices.data.Purchase_item;
import com.xionce.doctorvetServices.data.Purchase_itemAdapter;
import com.xionce.doctorvetServices.data.Sell;
import com.xionce.doctorvetServices.data.Sell_item;
import com.xionce.doctorvetServices.data.Sell_itemAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class EditPurchaseActivity_1_1_2 extends EditBaseActivity {

    private static final String TAG = "EditPurchaseActivity_1_";

    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;

    private Purchase purchase = null;
    private Purchase_itemAdapter purchaseItemsAdapter = null;
    private RecyclerView recyclerView;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() == null) return;

            String barcode = result.getText();
            barcodeView.setStatusText(result.getText());

            beepManager.playBeepSoundAndVibrate();

            //Added preview of scanned barcode
//            ImageView imageView = findViewById(R.id.barcodePreview);
//            imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));

            barcodeView.pause();

            //post request
            showWaitDialog();
            DoctorVetApp.get().getProductByBarcode(barcode, new DoctorVetApp.VolleyCallbackObject() {
                @Override
                public void onSuccess(Object resultObject) {
                    if (resultObject != null) {
                        Product product = (Product)resultObject;

                        if (product.getCost() == null) {
                            Snackbar.make(DoctorVetApp.getRootForSnack(EditPurchaseActivity_1_1_2.this), "Producto sin costo", Snackbar.LENGTH_SHORT).show();
                            ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                            toneGen1.startTone(ToneGenerator.TONE_PROP_ACK, 500);
                            hideWaitDialog();
                            return;
                        }

                        Purchase_item purchaseItem = new Purchase_item();
                        purchaseItem.setProduct(product);
                        purchaseItem.setSelected_unit(product.getUnit().getFirst_unit_string());
                        purchaseItem.setTax(product.getTax());
                        purchaseItem.setQuantity(BigDecimal.ONE);
                        purchaseItem.setPrice(product.getCost());
                        purchaseItem.setDiscount_surcharge(BigDecimal.ZERO);
                        purchaseItemsAdapter.addItem(purchaseItem);
                        HelperClass.focusLastRow(recyclerView);
                   } else {
                        Snackbar.make(DoctorVetApp.getRootForSnack(EditPurchaseActivity_1_1_2.this), "Producto no encontrado", Snackbar.LENGTH_SHORT).show();
                        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                        toneGen1.startTone(ToneGenerator.TONE_PROP_ACK, 500);
                    }
                    hideWaitDialog();
                }
            });

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    barcodeView.resume();
                }
            }, 700);

        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_purchase_1_1_2);
        Button btn_end = findViewById(R.id.btn_end);
        hideFab();
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        barcodeView = findViewById(R.id.barcode_scanner);
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.initializeFromIntent(getIntent());
        barcodeView.decodeContinuous(callback);

        beepManager = new BeepManager(this);

        if (savedInstanceState != null) {
            restoreFromBundle(savedInstanceState);
        }

        //setup recyclerview
        /*RecyclerView*/ recyclerView = findViewById(R.id.recycler_products);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EditPurchaseActivity_1_1_2.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        purchaseItemsAdapter = new Purchase_itemAdapter(getPurchase().getItems(), Purchase_itemAdapter.PurchaseDetailAdapter_types.FOR_PURCHASE_INPUT);
        recyclerView.setAdapter(purchaseItemsAdapter);
        purchaseItemsAdapter.setOnRemoveItemHandler(new HelperClass.AdapterOnRemoveItemHandler() {
            @Override
            public void onRemoveItem(Object data, View view, int pos) {
                //for products removal
            }
        });

        btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dataBackIntent = new Intent();
                dataBackIntent.putExtra(DoctorVetApp.INTENT_VALUES.PURCHASE_OBJ.name(), MySqlGson.getGson().toJson(getPurchase()));
                setResult(RESULT_OK, dataBackIntent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(DoctorVetApp.INTENT_VALUES.PURCHASE_OBJ.name(), MySqlGson.getGson().toJson(getPurchase()));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String purchaseInStr = savedInstanceState.getString(DoctorVetApp.INTENT_VALUES.PURCHASE_OBJ.name());
        purchase = MySqlGson.getGson().fromJson(purchaseInStr, Purchase.class);
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
    protected Sell_item getObjectFromUI() {
        return null;
    }

    @Override
    protected Purchase_item getObject() {
        return null;
    }

    @Override
    protected void setObjectToUI(Object object) {
    }

    @Override
    protected DoctorVetApp.IResourceObject getResourceObject() {
        return null;
    }

    @Override
    protected String getUploadTableName() {
        return null;
    }

    private Purchase getPurchase() {
        if (purchase == null)
            purchase = MySqlGson.postGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.PURCHASE_OBJ.name()), Purchase.class);

        return purchase;
    }

}
