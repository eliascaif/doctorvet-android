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
import com.xionce.doctorvetServices.data.Movement;
import com.xionce.doctorvetServices.data.Movement_item;
import com.xionce.doctorvetServices.data.Movement_itemAdapter;
import com.xionce.doctorvetServices.data.Product;
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

public class EditMovementActivity_1_1_2 extends EditBaseActivity {

    private static final String TAG = "EditSellActivity_1_1_2";

    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;

    private Movement movement = null;
    private Movement_itemAdapter movementItemsAdapter = null;
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
                        Movement_item movementItem = new Movement_item();
                        movementItem.setProduct(product);
                        movementItem.setSelected_unit(product.getUnit().getFirst_unit_string());
                        movementItem.setQuantity(BigDecimal.ONE);
                        movementItemsAdapter.addItem(movementItem);
                        HelperClass.focusLastRow(recyclerView);
                   } else {
                        Snackbar.make(DoctorVetApp.getRootForSnack(EditMovementActivity_1_1_2.this), "Producto no encontrado", Snackbar.LENGTH_SHORT).show();
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
        setContentView(R.layout.content_edit_movement_1_1_2);
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
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EditMovementActivity_1_1_2.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        movementItemsAdapter = new Movement_itemAdapter(getMovement().getItems(), Movement_itemAdapter.MovementAdapter_types.FOR_MOVEMENT_INPUT);
        recyclerView.setAdapter(movementItemsAdapter);
        movementItemsAdapter.setOnRemoveItemHandler(new HelperClass.AdapterOnRemoveItemHandler() {
            @Override
            public void onRemoveItem(Object data, View view, int pos) {
                //for products removal
            }
        });

        btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dataBackIntent = new Intent();
                dataBackIntent.putExtra(DoctorVetApp.INTENT_VALUES.MOVEMENT_OBJ.name(), MySqlGson.getGson().toJson(getMovement()));
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
        outState.putString(DoctorVetApp.INTENT_VALUES.MOVEMENT_OBJ.name(), MySqlGson.getGson().toJson(getMovement()));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String movementInStr = savedInstanceState.getString(DoctorVetApp.INTENT_VALUES.MOVEMENT_OBJ.name());
        movement = MySqlGson.getGson().fromJson(movementInStr, Movement.class);
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
    protected Sell_item getObject() {
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

    private Movement getMovement() {
        if (movement == null)
            movement = MySqlGson.postGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.MOVEMENT_OBJ.name()), Movement.class);

        return movement;
    }

}
