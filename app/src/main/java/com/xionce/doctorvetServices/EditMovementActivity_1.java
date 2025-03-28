package com.xionce.doctorvetServices;

import static com.xionce.doctorvetServices.utilities.HelperClass.REQUEST_READ_BARCODE;

import android.Manifest;
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

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.xionce.doctorvetServices.data.Movement;
import com.xionce.doctorvetServices.data.Movement_itemAdapter;
import com.xionce.doctorvetServices.data.User;
import com.xionce.doctorvetServices.data.VetPointsAdapter;
import com.xionce.doctorvetServices.data.Vet_deposit;
import com.xionce.doctorvetServices.data.Vet_point;
import com.xionce.doctorvetServices.data.VetsDepositsAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class EditMovementActivity_1 extends EditBaseActivity {

    private static final String TAG = "EditMovementActivity_1";
    private TextInputLayout txtDate;
    private TextInputLayout txtHour;
    //private TextView txt_products_details;
    //private View view_products_details;
    private Spinner spinnerDepositOrigin;
    private Spinner spinnerDepositDestination;
    private Spinner spinnerMovementPoint;

    private Movement movement = null;
    private Movement_itemAdapter movementItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_movement_1);
        toolbar_title.setText("Nuevo movimiento");
        toolbar_subtitle.setText("Selecciona fecha y agrega productos");
        TextView txt_add_products_by_keyboard = findViewById(R.id.txt_add_products_by_keyboard);
        TextView txt_add_products_by_barcode = findViewById(R.id.txt_add_products_by_barcode);
        txtDate = findViewById(R.id.txt_date);
        txtHour = findViewById(R.id.txt_hour);
        //txt_products_details = findViewById(R.id.txt_products_details);
        //view_products_details = findViewById(R.id.view_products_details);
        spinnerDepositOrigin = findViewById(R.id.spinner_deposit_origin);
        spinnerDepositDestination = findViewById(R.id.spinner_deposit_destination);
        spinnerMovementPoint = findViewById(R.id.spinner_movement_point);
        hideToolbarImage();
        hideFab();

        if (savedInstanceState == null) {
            initializeInitRequestNumber(1);
            DoctorVetApp.get().getMovementsForInput(new DoctorVetApp.VolleyCallbackObject() {
                @Override
                public void onSuccess(Object resultObject) {
                    setRequestCompleted();
                    Movement.MovementsForInput movementsForInput = (Movement.MovementsForInput) resultObject;
                    setDepositOriginAdapter(movementsForInput.getDeposits());
                    setDepositDestinationAdapter(movementsForInput.getDeposits());
                    setMovementsPoints(movementsForInput);
                    getObject().setOrigin_deposit(movementsForInput.getDeposits().get(0));
                    getObject().setDestination_deposit(movementsForInput.getDeposits().get(0));
                    getObject().setMovement_point(movementsForInput.getMovement_points().get(0).getPolish());
                }
            });
        } else {
            restoreFromBundle(savedInstanceState);
        }

        Movement movement = getObject();
        setObjectToUI(movement);

        //setup recyclerview
        RecyclerView recyclerView = findViewById(R.id.recycler_products);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EditMovementActivity_1.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        movementItemAdapter = new Movement_itemAdapter(getObject().getItems(), Movement_itemAdapter.MovementAdapter_types.FOR_MOVEMENT_INPUT);
        recyclerView.setAdapter(movementItemAdapter);
        movementItemAdapter.setOnRemoveItemHandler(new HelperClass.AdapterOnRemoveItemHandler() {
            @Override
            public void onRemoveItem(Object data, View view, int pos) {
                //for products removal
//                calculateTotal();
            }
        });

//        calculateTotal();

        txt_add_products_by_keyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditMovementActivity_1.this, EditMovementActivity_1_1.class);
                intent.putExtra(DoctorVetApp.INTENT_VALUES.MOVEMENT_OBJ.name(), MySqlGson.getGson().toJson(getObjectFromUI()));
                startActivityForResult(intent, HelperClass.REQUEST_UPDATE);
            }
        });

        txt_add_products_by_barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (HelperClass.checkPermission(android.Manifest.permission.CAMERA, getApplicationContext())) {
                    Intent intent = new Intent(EditMovementActivity_1.this, EditMovementActivity_1_1_2.class);
                    intent.putExtra(DoctorVetApp.INTENT_VALUES.MOVEMENT_OBJ.name(), MySqlGson.postGson().toJson(getObjectFromUI()));
                    startActivityForResult(intent, HelperClass.REQUEST_UPDATE);
                } else {
                    HelperClass.requestPermission(EditMovementActivity_1.this, Manifest.permission.CAMERA, REQUEST_READ_BARCODE);
               }
            }
        });

        txtDate.getEditText().setText(HelperClass.getDateInLocaleShort(Calendar.getInstance().getTime()));
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

        txtHour.getEditText().setText(HelperClass.getTimeInLocale(Calendar.getInstance().getTime(), this));
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
        outState.putString(DoctorVetApp.INTENT_VALUES.MOVEMENT_OBJ.name(), MySqlGson.getGson().toJson(getObjectFromUI()));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString(DoctorVetApp.INTENT_VALUES.MOVEMENT_OBJ.name());
        movement = MySqlGson.getGson().fromJson(objectInString, Movement.class);

        String movementsForInputInString = DoctorVetApp.get().readFromDisk("movements_for_input");
        Movement.MovementsForInput movementsForInput = MySqlGson.getGson().fromJson(movementsForInputInString, Movement.MovementsForInput.class);
        setDepositOriginAdapter(movementsForInput.getDeposits());
        setDepositDestinationAdapter(movementsForInput.getDeposits());
        setMovementsPoints(movementsForInput);

        int indexOfSelectedOriginDeposit = movementsForInput.getDeposits().indexOf(getObject().getOrigin_deposit());
        spinnerDepositOrigin.setSelection(indexOfSelectedOriginDeposit);

        int indexOfSelectedDestinationDeposit = movementsForInput.getDeposits().indexOf(getObject().getDestination_deposit());
        spinnerDepositOrigin.setSelection(indexOfSelectedDestinationDeposit);

        int indexOfSelectedMovementPoint = movementsForInput.getMovement_points().indexOf(getObject().getMovement_point());
        spinnerDepositOrigin.setSelection(indexOfSelectedMovementPoint);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == HelperClass.REQUEST_FINISH)
            finish();

        if (resultCode != RESULT_OK) return;

        //TODO: prevent hardwipeo
        if (data != null && data.hasExtra(DoctorVetApp.INTENT_VALUES.MOVEMENT_OBJ.name())) {
            String movementInString = data.getStringExtra(DoctorVetApp.INTENT_VALUES.MOVEMENT_OBJ.name());
            Movement movement = MySqlGson.getGson().fromJson(movementInString, Movement.class);
            movementItemAdapter.updateList(movement.getItems());
            //calculateTotal();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length == 0) return;

        boolean permissionToTakePhotoAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        if (permissionToTakePhotoAccepted) {
            Intent intent = new Intent(EditMovementActivity_1.this, EditMovementActivity_1_1_2.class);
            intent.putExtra(DoctorVetApp.INTENT_VALUES.MOVEMENT_OBJ.name(), MySqlGson.postGson().toJson(getObjectFromUI()));
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
    protected Movement getObjectFromUI() {
        Movement movement = getObject();

        //date
        String fecha_hora = txtDate.getEditText().getText().toString() + " " + txtHour.getEditText().getText().toString();
        movement.setDate(HelperClass.getShortDateTime(fecha_hora, getBaseContext()));

        return movement;
    }

    @Override
    protected Movement getObject() {
        if (movement == null) {
            movement = new Movement();
            movement.setDate(Calendar.getInstance().getTime());
            movement.setUser(new User(DoctorVetApp.get().getUser().getId(), DoctorVetApp.get().getUser().getName()));
            movement.setItems(new ArrayList<>());
        }

        return movement;
    }

    @Override
    protected void setObjectToUI(Object object) {
        Movement movement = (Movement) object;
    }

    @Override
    protected DoctorVetApp.IResourceObject getResourceObject() {
        return null;
    }

    @Override
    protected String getUploadTableName() {
        return null;
    }

    private void setDepositDestinationAdapter(ArrayList<Vet_deposit> deposits) {
        VetsDepositsAdapter vetsDepositsAdapterDestination = new VetsDepositsAdapter(deposits);
        spinnerDepositDestination.setAdapter(vetsDepositsAdapterDestination.getArrayAdapter(EditMovementActivity_1.this));
        spinnerDepositDestination.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getObject().setDestination_deposit((Vet_deposit) adapterView.getItemAtPosition(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                getObject().setDestination_deposit(null);
            }
        });
    }
    private void setDepositOriginAdapter(ArrayList<Vet_deposit> deposits) {
        VetsDepositsAdapter vetsDepositsAdapterOrigin = new VetsDepositsAdapter(deposits);
        spinnerDepositOrigin.setAdapter(vetsDepositsAdapterOrigin.getArrayAdapter(EditMovementActivity_1.this));
        spinnerDepositOrigin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getObject().setOrigin_deposit((Vet_deposit) adapterView.getItemAtPosition(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                getObject().setOrigin_deposit(null);
            }
        });
    }
    private void setMovementsPoints(Movement.MovementsForInput movementsForInput) {
        if (movementsForInput.getVet_info().getIs_multi_point_vet() == 1) {
            findViewById(R.id.lyt_movement_point).setVisibility(View.VISIBLE);
            VetPointsAdapter vetPointsAdapter = new VetPointsAdapter(movementsForInput.getMovement_points());
            spinnerMovementPoint.setAdapter(vetPointsAdapter.getArrayAdapter(EditMovementActivity_1.this));
            spinnerMovementPoint.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Vet_point vetPoint = (Vet_point) adapterView.getItemAtPosition(i);
                    getObject().setMovement_point(vetPoint.getPolish());
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    getObject().setMovement_point(null);
                }
            });
        }
    }

    private void next() {
        if (!validate_equal_deposits()) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), "Los almacenes de origen y destino deben ser diferentes", Snackbar.LENGTH_LONG).show();
            return;
        }

        if (!validate_something_to_register()) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), "Agrega productos presionando sobre 'agregar +'", Snackbar.LENGTH_LONG).show();
            return;
        }

        Movement movement = getObjectFromUI();

        Intent intent = new Intent(EditMovementActivity_1.this, EditMovementActivity_2.class);
        intent.putExtra(DoctorVetApp.INTENT_VALUES.MOVEMENT_OBJ.name(), MySqlGson.getGson().toJson(movement));
        startActivityForResult(intent, 1);
    }
    private void searchTime() {
        DoctorVetApp.get().searchTimeSetInit(txtHour, getSupportFragmentManager());
    }
    private void searchDate() {
        DoctorVetApp.get().searchDateSetInit(txtDate, getSupportFragmentManager());
    }
    private void calculateTotal() {
        //products details
//        String details = getObject().getProductsDetails();
//        txt_products_details.setText("");
//        txt_products_details.setVisibility(View.GONE);
//        if (!details.isEmpty()) {
//            txt_products_details.setVisibility(View.VISIBLE);
//            txt_products_details.setText(details);
//        }
    }

    private boolean validate_something_to_register() {
        return getObject().getItems().size() != 0;
    }
    private boolean validate_equal_deposits() {
        return !getObject().getOrigin_deposit().getId().equals(getObject().getDestination_deposit().getId());
    }

}
