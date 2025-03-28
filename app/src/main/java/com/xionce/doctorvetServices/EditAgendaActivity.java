package com.xionce.doctorvetServices;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.xionce.doctorvetServices.data.Agenda;
import com.xionce.doctorvetServices.data.Owner;
import com.xionce.doctorvetServices.data.Pet;
import com.xionce.doctorvetServices.data.Product;
import com.xionce.doctorvetServices.data.ProductsAdapter;
import com.xionce.doctorvetServices.data.User;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditAgendaActivity extends EditBaseActivity {

    private static final String TAG = "EditAgendaActivity";
    private TextInputLayout txtEventName;
    private AutoCompleteTextView actv_product;
    private TextInputLayout txtBeginTime;
    private TextInputLayout txtBeginTimeHora;
    private TextInputLayout txtEndTime;
    private TextInputLayout txtEndTimeHora;
    private TextInputLayout txtDescription;
    private CheckBox chk_private_task;
    private View list_item_sell_owner;
    private View list_item_sell_pet;
    private View list_item_selected_user;
    private Agenda agenda = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_agenda);
        txtEventName = findViewById(R.id.txt_event_name);
        actv_product = findViewById(R.id.actv_service);
        txtBeginTime = findViewById(R.id.txt_date);
        txtBeginTimeHora = findViewById(R.id.txt_hour);
        txtEndTime = findViewById(R.id.txt_end_time);
        txtEndTimeHora = findViewById(R.id.txt_end_time_hora);
        txtDescription = findViewById(R.id.txt_description);
        chk_private_task = findViewById(R.id.chk_private_task);
        list_item_sell_owner = findViewById(R.id.list_item_owner_selector);
        list_item_sell_pet = findViewById(R.id.list_item_pet_selector);
        ImageView img_thumb_owner = list_item_sell_owner.findViewById(R.id.img_thumb);
        ImageView img_thumb_pet = list_item_sell_pet.findViewById(R.id.img_thumb);
        Glide.with(this).load(R.drawable.ic_account_circle_light).apply(RequestOptions.fitCenterTransform()).into(img_thumb_owner);
        Glide.with(this).load(R.drawable.ic_pets_light).apply(RequestOptions.fitCenterTransform()).into(img_thumb_pet);
        list_item_selected_user = findViewById(R.id.list_item_selected_user);
        ImageView remove_user = list_item_selected_user.findViewById(R.id.img_remove);
        remove_user.setVisibility(View.INVISIBLE);
        DoctorVetApp.get().markRequired(txtEventName);
        DoctorVetApp.get().markRequired(txtBeginTime);
        DoctorVetApp.get().markRequired(txtBeginTimeHora);
        hideToolbarImage();

        if (savedInstanceState == null) {
            initializeInitRequestNumber(1);
            DoctorVetApp.get().getProductsVet(new DoctorVetApp.VolleyCallbackArrayList() {
                @Override
                public void onSuccess(ArrayList resultList) {
                    setRequestCompleted();
                    setProductsAdapter(resultList);
                }
            });
        } else {
            restoreFromBundle(savedInstanceState);
        }

        if (isUpdate()) {
            toolbar_title.setText(R.string.editar_agenda);
            toolbar_subtitle.setText(R.string.editando_agenda);
        } else {
            toolbar_title.setText(R.string.nueva_agenda);
            toolbar_subtitle.setText(R.string.Ingresando_nueva_agenda);
        }

        Agenda agenda = getObject();
        setObjectToUI(agenda);

        ImageView iconSearch_servicio = findViewById(R.id.img_search_servicio);
        iconSearch_servicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditAgendaActivity.this, SearchProductActivity.class);
                intent.putExtra(DoctorVetApp.REQUEST_SEARCH_FOR, DoctorVetApp.INTENT_VALUES.PRODUCT_OBJ.name());
                intent.putExtra(DoctorVetApp.INTENT_SEARCH_RETURN, true);
                startActivityForResult(intent, HelperClass.REQUEST_SEARCH);
            }
        });

        list_item_sell_owner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditAgendaActivity.this, SearchOwnerActivity.class);
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
                Intent intent = new Intent(EditAgendaActivity.this, SearchPetActivity.class);
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

        list_item_selected_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditAgendaActivity.this, SearchUserActivity.class);
                intent.putExtra(DoctorVetApp.REQUEST_SEARCH_FOR, DoctorVetApp.INTENT_VALUES.USER_OBJ.name());
                intent.putExtra(DoctorVetApp.INTENT_SEARCH_RETURN, true);
                startActivityForResult(intent, HelperClass.REQUEST_SEARCH);
            }
        });
        list_item_selected_user.findViewById(R.id.img_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeUserInView();
            }
        });
        if (DoctorVetApp.get().getUser().getRol().equalsIgnoreCase("USER")) {
            findViewById(R.id.txt_user_selector_text).setVisibility(View.GONE);
            list_item_selected_user.setVisibility(View.GONE);
        }

        //txtBeginTime.getEditText().setText(HelperClass.getDateInLocaleShort(Calendar.getInstance().getTime()));
        ImageView dateSearch = findViewById(R.id.img_search_date);
        dateSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDate();
            }
        });
        txtBeginTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDate();
            }
        });
        txtBeginTime.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDate();
            }
        });

        //txtBeginTimeHora.getEditText().setText(HelperClass.getTimeInLocale(Calendar.getInstance().getTime(), this));
        ImageView timeSearch = findViewById(R.id.img_search_time);
        timeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchTime();
            }
        });
        txtBeginTimeHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchTime();
            }
        });
        txtBeginTimeHora.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchTime();
            }
        });

        //end date and time
        ImageView dateSearchEnd = findViewById(R.id.img_search_date_fin);
        dateSearchEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDateEnd();
            }
        });
        txtEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDateEnd();
            }
        });
        txtEndTime.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDateEnd();
            }
        });

        ImageView timeSearchEnd = findViewById(R.id.img_search_time_fin);
        timeSearchEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchTimeEnd();
            }
        });
        txtEndTimeHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchTimeEnd();
            }
        });
        txtEndTimeHora.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchTimeEnd();
            }
        });

        //
        txtDescription.getEditText().setImeOptions(EditorInfo.IME_ACTION_DONE);
        txtDescription.getEditText().setRawInputType(InputType.TYPE_CLASS_TEXT);
        txtDescription.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    save();
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    protected void onAllRequestsCompleted() {
        if (isUpdate()) return;
        DoctorVetApp.get().showKeyboard();
        actv_product.requestFocus();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("agenda", MySqlGson.getGson().toJson(getObjectFromUI()));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString("agenda");
        agenda = MySqlGson.getGson().fromJson(objectInString, Agenda.class);

        String productsInString = DoctorVetApp.get().readFromDisk("products");
        ArrayList<Product> products = MySqlGson.getGson().fromJson(productsInString, new TypeToken<List<Product>>(){}.getType());
        setProductsAdapter(products);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        //busquedas
        if (requestCode == HelperClass.REQUEST_SEARCH && data != null) {
            String searchFor = data.getExtras().getString(DoctorVetApp.REQUEST_SEARCH_FOR, "");
            if (searchFor.equals(DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name())) {
                Owner owner = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name()), Owner.class);
                getObject().setOwner(owner);
                setOwnerInView(owner);
                if (owner.getPets().size() > 0) {
                    getObject().setPet(owner.getPets().get(0));
                    setPetInView(owner.getPets().get(0));
                } else {
                    getObject().setPet(null);
                    removePetInView();
                }
            } else if (searchFor.equals(DoctorVetApp.INTENT_VALUES.PET_OBJ.name())) {
                Pet pet = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.PET_OBJ.name()), Pet.class);
                getObject().setPet(pet);
                setPetInView(pet);
                Owner owner = pet.getFirstPrincipalOwner();
                getObject().setOwner(owner);
                setOwnerInView(owner);
            } else if (searchFor.equals(DoctorVetApp.INTENT_VALUES.PRODUCT_OBJ.name())) {
                Product product = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_OBJ.name()), Product.class);
                getObject().setProduct(product);
                txtEventName.getEditText().setText(product.getName());
            } else if (searchFor.equals(DoctorVetApp.INTENT_VALUES.USER_OBJ.name())) {
                User user = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.USER_OBJ.name()), User.class);
                getObject().setUser(user);
                setUserInView(user);
            }
        }
    }

    @Override
    protected void save() {
        if (!validate_selected_user()) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), "Selecciona un usuario", Snackbar.LENGTH_LONG).show();
            return;
        }

        if (!validateServicio() || !validateBeginDate() || !validateBeginHour() || !validateEndDate() || !validateEndHour())
            return;

        showWaitDialog();

        Agenda agenda = getObjectFromUI();
        final String agenda_json_object = MySqlGson.postGson().toJson(agenda);
        TokenStringRequest stringRequest = new TokenStringRequest(getMethod(), getUrl().toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String success = MySqlGson.getStatusFromResponse(response);
                    if (success.equalsIgnoreCase("success"))
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
                return agenda_json_object.getBytes();
            }
        };
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    @Override
    protected void update() {
        save();
    }

    @Override
    protected URL getUrl() {
        if (getMethod() == Request.Method.POST)
            return NetworkUtils.buildAgendaUrl(null);

        return NetworkUtils.buildAgendaUrl(getObject().getId());
    }

    @Override
    protected Agenda getObjectFromUI() {
        Agenda agenda = getObject();
        agenda.setEvent_name(txtEventName.getEditText().getText().toString());
        agenda.setDescription(txtDescription.getEditText().getText().toString());

        //fecha
        String fecha_hora = txtBeginTime.getEditText().getText().toString() + " " + txtBeginTimeHora.getEditText().getText().toString();
        agenda.setBegin_time(HelperClass.getShortDateTime(fecha_hora, getBaseContext()));

        //
        agenda.setEnd_time(null);
        boolean end_time_exists = !txtEndTime.getEditText().getText().toString().isEmpty()
                                    && !txtEndTimeHora.getEditText().getText().toString().isEmpty();

        if (end_time_exists) {
            String fecha_hora_fin = txtEndTime.getEditText().getText().toString() + " " + txtEndTimeHora.getEditText().getText().toString();
            agenda.setEnd_time(HelperClass.getShortDateTime(fecha_hora_fin, getBaseContext()));
        }

//        String fecha_hora_fin = txtEndTime.getEditText().getText().toString() + " " + txtEndTimeHora.getEditText().getText().toString();
//        agenda.setEnd_time(HelperClass.getShortDateTime(fecha_hora_fin, getBaseContext()));

        if (chk_private_task.isChecked())
            agenda.setPrivate_task(1);

        //polish objects
        User user = agenda.getUser();
        User polish_user = new User(user.getId(), user.getName(), user.getThumb_url());
        agenda.setUser(polish_user);

        Owner owner = agenda.getOwner();
        if (owner != null) {
            Owner polish_owner = new Owner(owner.getId(), owner.getName(), owner.getThumb_url());
            agenda.setOwner(polish_owner);
        }

        Pet pet = agenda.getPet();
        if (pet != null) {
            Pet polish_pet = new Pet(pet.getId(), pet.getName(), pet.getThumb_url());
            agenda.setPet(polish_pet);
        }

        Product service = agenda.getProduct();
        if (service != null) {
            Product polish_product = new Product(service.getId(), service.getName(), service.getThumb_url());
            agenda.setProduct(polish_product);
            agenda.setEvent_name(polish_product.getName());
        }

        return agenda;
    }

    @Override
    protected Agenda getObject() {
        if (agenda == null) {
            if (isUpdate()) {
                agenda = MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.AGENDA_OBJ.name()), Agenda.class);
            } else {
                agenda = new Agenda(DoctorVetApp.get().getUser(), Calendar.getInstance().getTime());
                if (getIntent().hasExtra(DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name()))
                    agenda.setOwner(MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name()), Owner.class));
                if (getIntent().hasExtra(DoctorVetApp.INTENT_VALUES.PET_OBJ.name()))
                    agenda.setPet(MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.PET_OBJ.name()), Pet.class));
            }
        }

        return agenda;
    }

    @Override
    protected void setObjectToUI(Object object) {
        Agenda agenda = (Agenda) object;

        DoctorVetApp.ObjectToTextInputLayout(findViewById(R.id.lista), agenda, "txt_");

        setUserInView(agenda.getUser());
        if (agenda.getOwner() != null) setOwnerInView(agenda.getOwner());
        if (agenda.getPet() != null) setPetInView(agenda.getPet());

        if (getObject().getProduct() != null) {
            actv_product.setText(getObject().getProduct().getName());
        }

        if (agenda.getBegin_time() != null) {
            txtBeginTime.getEditText().setText(HelperClass.getDateInLocale(agenda.getBegin_time(), EditAgendaActivity.this));
            txtBeginTimeHora.getEditText().setText(HelperClass.getTimeInLocale(agenda.getBegin_time(), EditAgendaActivity.this));
        }

        if (agenda.getEnd_time() != null) {
            txtEndTime.getEditText().setText(HelperClass.getDateInLocale(agenda.getEnd_time(), EditAgendaActivity.this));
            txtEndTimeHora.getEditText().setText(HelperClass.getTimeInLocale(agenda.getEnd_time(), EditAgendaActivity.this));
        }

        if (agenda.getDescription() != null)
            txtDescription.getEditText().setText(agenda.getDescription());

        if (agenda.getPrivate_task() != null && agenda.getPrivate_task() == 1)
            chk_private_task.setChecked(true);
    }

    @Override
    protected DoctorVetApp.IResourceObject getResourceObject() {
        return null;
    }

    @Override
    protected String getUploadTableName() {
        return "agenda";
    }

    private void setProductsAdapter(ArrayList resultList) {
        ProductsAdapter productsAdapter = new ProductsAdapter(resultList, ProductsAdapter.Products_types.NORMAL);
        actv_product.setAdapter(productsAdapter.getAutocompleteAdapter(EditAgendaActivity.this));
        actv_product.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Product service = (Product) adapterView.getItemAtPosition(i);
                getObject().setProduct(service);
                txtBeginTime.requestFocus();
            }
        });
        actv_product.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty())
                    getObject().setProduct(null);
            }
        });
        DoctorVetApp.get().setOnTouchToShowDropDown(actv_product);
        DoctorVetApp.get().setAllWidthToDropDown(actv_product, EditAgendaActivity.this);
    }

    private void searchTime() {
        DoctorVetApp.get().searchTimeSetInit(txtBeginTimeHora, getSupportFragmentManager());
    }
    private void searchDate() {
        DoctorVetApp.get().searchDateSetInit(txtBeginTime, getSupportFragmentManager());
    }
    private void searchTimeEnd() {
        DoctorVetApp.get().searchTimeSetInit(txtEndTimeHora, getSupportFragmentManager());
    }
    private void searchDateEnd() {
        DoctorVetApp.get().searchDateSetInit(txtEndTime, getSupportFragmentManager());
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
    private void setUserInView(User user) {
        ImageView img_thumb = list_item_selected_user.findViewById(R.id.img_thumb);
        TextView txt_name = list_item_selected_user.findViewById(R.id.txt_item_name);
        DoctorVetApp.get().setThumb(user.getThumb_url(), img_thumb, R.drawable.ic_account_circle_light);
        txt_name.setText(user.getName());
    }
    private void removeUserInView() {
        getObject().setUser(null);
        ImageView img_thumb = list_item_selected_user.findViewById(R.id.img_thumb);
        TextView txt_name = list_item_selected_user.findViewById(R.id.txt_item_name);
        Context ctx = img_thumb.getContext();
        Glide.with(ctx).load(R.drawable.ic_account_circle_light).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
        txt_name.setText("SELECCIONAR");
    }

    private boolean validate_selected_user() {
        if (getObject().getUser() == null)
            return false;

        return true;
    }
    private boolean validateServicio() {
        if (HelperClass.validateEmpty(txtEventName)) {
            return true;
        }

        return false;
    }
    private boolean validateBeginDate() {
        return HelperClass.validateDate(txtBeginTime, false);
    }
    private boolean validateBeginHour() {
        return HelperClass.validateHour(txtBeginTimeHora, false);
    }
    private boolean validateEndDate() {
        return HelperClass.validateDate(txtEndTime, true);
    }
    private boolean validateEndHour() {
        if (!txtEndTime.getEditText().getText().toString().isEmpty())
            return HelperClass.validateHour(txtEndTimeHora, false);

        return true;
    }

}