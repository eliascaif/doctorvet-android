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
import com.xionce.doctorvetServices.data.Owner;
import com.xionce.doctorvetServices.data.Pet;
import com.xionce.doctorvetServices.data.Sell;
import com.xionce.doctorvetServices.data.VetPointsAdapter;
import com.xionce.doctorvetServices.data.Sell_itemAdapter;
import com.xionce.doctorvetServices.data.Vet_deposit;
import com.xionce.doctorvetServices.data.Vet_point;
import com.xionce.doctorvetServices.data.VetsDepositsAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class EditSellActivity_1 extends EditBaseActivity {

    private static final String TAG = "EditSellActivity_1";
    private TextView txt_total;
    private View list_item_sell_owner;
    private View list_item_sell_pet;
    private TextInputLayout txtDate;
    private TextInputLayout txtHour;
    private Spinner spinnerDeposit;
    private Spinner spinnerSellPoint;
    private VetPointsAdapter vetPointsAdapter;

    private ArrayList<Finance_payment_method> payments_types;
    private Sell sell = null;
    private Sell_itemAdapter sellItemsAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_sell_1);
        toolbar_title.setText("Venta");
        toolbar_subtitle.setText("Selecciona fecha, " + DoctorVetApp.get().getPetNaming().toLowerCase() + " y agrega productos");
        TextView txt_add_products_by_keyboard = findViewById(R.id.txt_add_products_by_keyboard);
        TextView txt_add_products_by_barcode = findViewById(R.id.txt_add_products_by_barcode);
        txt_total = findViewById(R.id.txt_total);
        txtDate = findViewById(R.id.txt_date);
        txtHour = findViewById(R.id.txt_hour);
        list_item_sell_pet = findViewById(R.id.list_item_pet_selector);
        list_item_sell_owner = findViewById(R.id.list_item_owner_selector);
        ImageView img_thumb_owner = list_item_sell_owner.findViewById(R.id.img_thumb);
        ImageView img_thumb_pet = list_item_sell_pet.findViewById(R.id.img_thumb);
        spinnerDeposit = findViewById(R.id.spinner_deposit);
        spinnerSellPoint = findViewById(R.id.spinner_sell_point);
        Glide.with(this).load(R.drawable.ic_account_circle_light).apply(RequestOptions.fitCenterTransform()).into(img_thumb_owner);
        Glide.with(this).load(R.drawable.ic_pets_light).apply(RequestOptions.fitCenterTransform()).into(img_thumb_pet);
        hideToolbarImage();
        hideFab();

        if (savedInstanceState == null) {
            initializeInitRequestNumber(1);
            DoctorVetApp.get().getSellsForInput(new DoctorVetApp.VolleyCallbackObject() {
                @Override
                public void onSuccess(Object resultObject) {
                    setRequestCompleted();
                    Sell.SellsForInput sellsForInput = (Sell.SellsForInput) resultObject;
                    setDepositAdapter(sellsForInput);
                    getObject().setDeposit(sellsForInput.getDeposits().get(0));

                    setPointsAdapter(sellsForInput);
                    getObject().setSell_point(sellsForInput.getSell_points().get(0));

                    //set default sell point
                    boolean must_set_default_point = sellsForInput.getVet_info().getIs_multi_point_vet() == 1
                                                        && vetPointsAdapter != null
                                                        && sellsForInput.getVet_info().getDefault_sell_point() != null;

                    if (must_set_default_point)
                        spinnerSellPoint.setSelection(vetPointsAdapter.getPositionByName(sellsForInput.getVet_info().getDefault_sell_point().getName()));

                    payments_types = sellsForInput.getFinance_types_payments();
                    getObject().setVet_info(sellsForInput.getVet_info());
                }
            });
        } else {
            restoreFromBundle(savedInstanceState);
        }

        Sell sell = getObject();
        setObjectToUI(sell);

        //setup recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EditSellActivity_1.this);
        RecyclerView recyclerView = findViewById(R.id.recycler_products);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        sellItemsAdapter = new Sell_itemAdapter(getObject().getItems(), Sell_itemAdapter.SellItemsAdapterTypes.FOR_SELL_INPUT);
        recyclerView.setAdapter(sellItemsAdapter);
        sellItemsAdapter.setOnRemoveItemHandler(new HelperClass.AdapterOnRemoveItemHandler() {
            @Override
            public void onRemoveItem(Object data, View view, int pos) {
                calculateTotal();
            }
        });

        calculateTotal();

        setup_sell_suggested_products();

        txt_add_products_by_keyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditSellActivity_1.this, EditSellActivity_1_1.class);
                intent.putExtra(DoctorVetApp.INTENT_VALUES.SELL_OBJ.name(), MySqlGson.postGson().toJson(getObjectFromUI()));
                startActivityForResult(intent, HelperClass.REQUEST_UPDATE);
            }
        });

        txt_add_products_by_barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (HelperClass.checkPermission(android.Manifest.permission.CAMERA, getApplicationContext())) {
                    Intent intent = new Intent(EditSellActivity_1.this, EditSellActivity_1_1_2.class);
                    intent.putExtra(DoctorVetApp.INTENT_VALUES.SELL_OBJ.name(), MySqlGson.postGson().toJson(getObjectFromUI()));
                    startActivityForResult(intent, HelperClass.REQUEST_UPDATE);
                } else {
                    HelperClass.requestPermission(EditSellActivity_1.this, Manifest.permission.CAMERA, REQUEST_READ_BARCODE);
                }
            }
        });

        list_item_sell_owner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditSellActivity_1.this, SearchOwnerActivity.class);
                intent.putExtra(DoctorVetApp.REQUEST_SEARCH_FOR, DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name());
                intent.putExtra(DoctorVetApp.INTENT_SEARCH_RETURN, true);
                startActivityForResult(intent, HelperClass.REQUEST_SEARCH);
            }
        });
        list_item_sell_owner.findViewById(R.id.img_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeOwnerInView();
                removePetInView();
            }
        });

        list_item_sell_pet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditSellActivity_1.this, SearchPetActivity.class);
                intent.putExtra(DoctorVetApp.REQUEST_SEARCH_FOR, DoctorVetApp.INTENT_VALUES.PET_OBJ.name());
                intent.putExtra(DoctorVetApp.INTENT_SEARCH_RETURN, true);
                startActivityForResult(intent, HelperClass.REQUEST_SEARCH);
            }
        });
        list_item_sell_pet.findViewById(R.id.img_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removePetInView();
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
        outState.putString(DoctorVetApp.INTENT_VALUES.SELL_OBJ.name(), MySqlGson.getGson().toJson(getObjectFromUI()));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString(DoctorVetApp.INTENT_VALUES.SELL_OBJ.name());
        sell = MySqlGson.getGson().fromJson(objectInString, Sell.class);

        String sellsForInputInString = DoctorVetApp.get().readFromDisk("sells_for_input");
        Sell.SellsForInput sellsForInput = MySqlGson.getGson().fromJson(sellsForInputInString, Sell.SellsForInput.class);
        setDepositAdapter(sellsForInput);
        setPointsAdapter(sellsForInput);
        payments_types = sellsForInput.getFinance_types_payments();
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

            if (searchFor.equals(DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name())) {
                Owner owner = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name()), Owner.class);
                getObject().setOwner(owner);
                setOwnerInView(owner);

                setup_sell_suggested_products();

                if (owner.getPets().size() > 0) {
                    getObject().setPet(owner.getPets().get(0));
                    setPetInView(owner.getPets().get(0));
                } else {
                    removePetInView();
                }
            } else if (searchFor.equals(DoctorVetApp.INTENT_VALUES.PET_OBJ.name())) {
                Pet pet = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.PET_OBJ.name()), Pet.class);
                getObject().setPet(pet);
                setPetInView(pet);
                Owner owner = pet.getFirstPrincipalOwner();
                getObject().setOwner(owner);
                setOwnerInView(owner);

                setup_sell_suggested_products(/*getObject()*/);
            }
        }

        //TODO: prevent hardwipeo
        if (data != null && data.hasExtra(DoctorVetApp.INTENT_VALUES.SELL_OBJ.name())) {
            String sellInString = data.getStringExtra(DoctorVetApp.INTENT_VALUES.SELL_OBJ.name());
            Sell sell = MySqlGson.getGson().fromJson(sellInString, Sell.class);
            sellItemsAdapter.updateList(sell.getItems());
            calculateTotal();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length == 0) return;

        boolean permissionToTakePhotoAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        if (permissionToTakePhotoAccepted) {
            Intent intent = new Intent(EditSellActivity_1.this, EditSellActivity_1_1_2.class);
            intent.putExtra(DoctorVetApp.INTENT_VALUES.SELL_OBJ.name(), MySqlGson.postGson().toJson(getObjectFromUI()));
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
    protected Sell getObjectFromUI() {
        Sell sell = getObject();

        //date
        String fecha_hora = txtDate.getEditText().getText().toString() + " " + txtHour.getEditText().getText().toString();
        sell.setDate(HelperClass.getShortDateTime(fecha_hora, getBaseContext()));

        return sell;
    }

    @Override
    protected Sell getObject() {
        if (sell == null) {
            sell = new Sell();
            sell.setDate(Calendar.getInstance().getTime());
            sell.setItems(new ArrayList<>());

            Intent intent = getIntent();
            if (intent.hasExtra(DoctorVetApp.INTENT_VALUES.PET_OBJ.name()))
                sell.setPet(MySqlGson.getGson().fromJson(intent.getStringExtra(DoctorVetApp.INTENT_VALUES.PET_OBJ.name()), Pet.class));

            if (intent.hasExtra(DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name()))
                sell.setOwner(MySqlGson.getGson().fromJson(intent.getStringExtra(DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name()), Owner.class));

            sell.setSuggested_values_established(0);
        }

        return sell;
    }

    @Override
    protected void setObjectToUI(Object object) {
        Sell sell = (Sell) object;

        txtDate.getEditText().setText(HelperClass.getDateInLocaleShort(sell.getDate()));
        txtHour.getEditText().setText(HelperClass.getTimeInLocale(sell.getDate(), getBaseContext()));

        Owner owner = sell.getOwner();
        if (owner != null)
            setOwnerInView(owner);

        Pet pet = sell.getPet();
        if (pet != null)
            setPetInView(pet);

    }

    @Override
    protected DoctorVetApp.IResourceObject getResourceObject() {
        return null;
    }

    @Override
    protected String getUploadTableName() {
        return null;
    }

    private void setDepositAdapter(Sell.SellsForInput sellsForInput) {
        if (sellsForInput.getVet_info().getIs_multi_deposit_vet() == 1) {
            findViewById(R.id.lyt_deposit).setVisibility(View.VISIBLE);
            VetsDepositsAdapter vetsDepositsAdapter = new VetsDepositsAdapter(sellsForInput.getDeposits());
            spinnerDeposit.setAdapter(vetsDepositsAdapter.getArrayAdapter(EditSellActivity_1.this));
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
    private void setPointsAdapter(Sell.SellsForInput sellsForInput) {
        if (sellsForInput.getVet_info().getIs_multi_point_vet() == 1) {
            findViewById(R.id.lyt_sell_point).setVisibility(View.VISIBLE);
            /*VetPointsAdapter*/ vetPointsAdapter = new VetPointsAdapter(sellsForInput.getSell_points());
            spinnerSellPoint.setAdapter(vetPointsAdapter.getArrayAdapter(EditSellActivity_1.this));
            spinnerSellPoint.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Vet_point vetPoint = (Vet_point) adapterView.getItemAtPosition(i);
                    getObject().setSell_point(vetPoint);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    getObject().setSell_point(null);
                }
            });
        }

    }

    private void next() {
        if (!validate_only_pet()) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), "Selecciona " + DoctorVetApp.get().getOwnerNaming(), Snackbar.LENGTH_LONG).show();
            return;
        }

        if (!validate_deposit()) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), "Selecciona almacen", Snackbar.LENGTH_LONG).show();
            return;
        }

        if (!validate_sell_point()) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), "Selecciona punto de venta", Snackbar.LENGTH_LONG).show();
            return;
        }

        if (!validate_something_to_register()) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), "Agrega productos presionando sobre 'agregar +'", Snackbar.LENGTH_LONG).show();
            return;
        }

        Sell sell = getObjectFromUI();

        Intent intent = new Intent(EditSellActivity_1.this, EditSellActivity_2.class);
        intent.putExtra(DoctorVetApp.INTENT_VALUES.SELL_OBJ.name(), MySqlGson.getGson().toJson(sell));
        intent.putExtra(DoctorVetApp.INTENT_VALUES.PAYMENTS_TYPES_LIST.name(), MySqlGson.getGson().toJson(payments_types));
        startActivityForResult(intent, 1);
    }
    private void setPetInView(Pet pet) {
        ImageView img_thumb = list_item_sell_pet.findViewById(R.id.img_thumb);
        TextView txt_name = list_item_sell_pet.findViewById(R.id.txt_item_name);
        DoctorVetApp.get().setThumb(pet.getThumb_url(), img_thumb, R.drawable.ic_pets_light);
        txt_name.setText(pet.getName());
    }
    private void removePetInView() {
        getObject().setPet(null);
        ImageView img_thumb = list_item_sell_pet.findViewById(R.id.img_thumb);
        TextView txt_name = list_item_sell_pet.findViewById(R.id.txt_item_name);
        Context ctx = img_thumb.getContext();
        Glide.with(ctx).load(R.drawable.ic_pets_light).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
        txt_name.setText("SELECCIONAR");
    }
    private void setOwnerInView(Owner owner) {
        ImageView img_thumb = list_item_sell_owner.findViewById(R.id.img_thumb);
        TextView txt_name = list_item_sell_owner.findViewById(R.id.txt_item_name);
        DoctorVetApp.get().setThumb(owner.getThumb_url(), img_thumb, R.drawable.ic_account_circle_light);
        txt_name.setText(owner.getName());
    }
    private void removeOwnerInView() {
        getObject().setOwner(null);
        ImageView img_thumb = list_item_sell_owner.findViewById(R.id.img_thumb);
        TextView txt_name = list_item_sell_owner.findViewById(R.id.txt_item_name);
        Context ctx = img_thumb.getContext();
        Glide.with(ctx).load(R.drawable.ic_account_circle_light).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
        txt_name.setText("SELECCIONAR");
    }
    private void calculateTotal() {
        txt_total.setText("Total: " + sellItemsAdapter.getTotal().toString());

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
    private void setup_sell_suggested_products(/*Sell sell*/) {
        if (DoctorVetApp.get().getVet().getSells_accept_suggested() == 0)
            return;

        //setup sell suggested products, call only once and only if products are not present
        Sell sell_aux = getObject();
        if (!sell_aux.getItems().isEmpty())
            return;

        boolean do_sell_suggested_products_call = sell_aux.getSuggested_values_established() == 0 && sell_aux.getOwner() != null;
        if (do_sell_suggested_products_call) {
            incrementRequestNumberInOne();
            DoctorVetApp.get().getSellSuggestedProducts(sell_aux.getOwner().getId(), new DoctorVetApp.VolleyCallbackArrayList() {
                @Override
                public void onSuccess(ArrayList resultList) {
                    setRequestCompleted();

                    if (resultList.size() > 0) {
                        Snackbar.make(DoctorVetApp.getRootForSnack(EditSellActivity_1.this), "Sugerencias agregadas", Snackbar.LENGTH_LONG).show();

                        sell_aux.setSuggested_values_established(1);
                        sell_aux.getItems().addAll(resultList);
                        sellItemsAdapter.notifyDataSetChanged();
                        calculateTotal();
                    }
                }
            });
        }
    }

    private boolean validate_something_to_register() {
        return getObject().getItems().size() != 0;
    }
    private boolean validate_deposit() {
        return getObject().getDeposit() != null;
    }
    private boolean validate_sell_point() {
        return getObject().getSell_point() != null;
    }
    private boolean validate_only_pet() {
        return !(getObject().getPet() != null && getObject().getOwner() == null);
    }

}
