package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.xionce.doctorvetServices.data.Region;
import com.xionce.doctorvetServices.data.RegionsAdapter;
import com.xionce.doctorvetServices.data.User;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EditUserActivity extends EditBaseActivity {

    private static final String TAG = "EditUserActivity";
    private TextInputLayout txtName;
    private TextInputLayout txtRegion;
    private AutoCompleteTextView actv_region;
    private TextInputLayout txtPhone;

    private User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_user);
        txtName = findViewById(R.id.txt_name);
        TextInputLayout txtAddress = findViewById(R.id.txt_address);
        actv_region = findViewById(R.id.actv_region);
        txtRegion = findViewById(R.id.txt_region);
        txtPhone = findViewById(R.id.txt_phone);
        TextInputLayout txtNotes = findViewById(R.id.txt_notes);
        DoctorVetApp.get().markRequired(txtName);

        if (savedInstanceState == null) {
            initializeInitRequestNumber(1);
            DoctorVetApp.get().getRegions(new DoctorVetApp.VolleyCallbackArrayList() {
                @Override
                public void onSuccess(ArrayList resultArrayList) {
                    setRequestCompleted();
                    setRegionsAdapter(resultArrayList);
                }
            });
        } else {
            restoreFromBundle(savedInstanceState);
        }

        if (isUpdate()){
            toolbar_title.setText(R.string.editar_user);
            toolbar_subtitle.setText(R.string.edit_subtitle);
        }

        User user = getObject();
        setObjectToUI(user);
        
        //Usuarios con loginType email pueden agregar sus fotos
        if (user.getLogin_type().equals(DoctorVetApp.login_types.EMAIL.toString())) {
            implementTakePhoto(getResourceObject());
        } else {
            implementShowPhoto(getResourceObject());
        }

//        ImageView iconSearch_region = findViewById(R.id.img_search_region);
//        iconSearch_region.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(EditUserActivity.this, SearchRegionActivity.class);
//                startActivityForResult(intent, HelperClass.REQUEST_SEARCH);
//            }
//        });
        
        //
        txtAddress.getEditText().setImeOptions(EditorInfo.IME_ACTION_NEXT);
        txtAddress.getEditText().setRawInputType(InputType.TYPE_CLASS_TEXT);

        txtNotes.getEditText().setImeOptions(EditorInfo.IME_ACTION_DONE);
        txtNotes.getEditText().setRawInputType(InputType.TYPE_CLASS_TEXT);
        txtNotes.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    save();
                    return true;
                }
                return false;
            }
        });

        if (!isUpdate())
            txtName.requestFocus();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("user", MySqlGson.getGson().toJson(getObjectFromUI()));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString("user");
        user = MySqlGson.getGson().fromJson(objectInString, User.class);

        String regionsInString = DoctorVetApp.get().readFromDisk("regions");
        ArrayList<Region> regions = MySqlGson.getGson().fromJson(regionsInString, new TypeToken<List<Region>>(){}.getType());
        setRegionsAdapter(regions);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode != RESULT_OK) return;
//
//        //busqueda regiones
//        if (requestCode == HelperClass.REQUEST_SEARCH && data != null) {
//            Region region = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.REGION_OBJ.name()), Region.class);
//            getObject().setRegion(region);
//            txtRegion.getEditText().setText(region.getFriendly_name());
//            findViewById(R.id.txt_phone).requestFocus();
//        }
//    }
    
    @Override
    protected void save() {
        //only update
    }

    @Override
    protected void update(){
        if (!validateName() || !validateRegion())
            return;

        showWaitDialog();

        final User user = getObjectFromUI();
        final String user_json_object = MySqlGson.postGson().toJson(user);
        TokenStringRequest stringRequest = new TokenStringRequest(getMethod(), getUrl().toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    User response_user = MySqlGson.getGson().fromJson(data, User.class);
                    DoctorVetApp.get().linkTempAndFinalFiles(user.getResources(), response_user.getResources());

                    Intent intent = new Intent(EditUserActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception ex) {
                    DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                } finally {
                    hideWaitDialog();
                    finish();
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
            public byte[] getBody() throws AuthFailureError {
                return user_json_object.getBytes();
            }
        };
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    @Override
    protected URL getUrl() {
        return NetworkUtils.buildUsersUrl(NetworkUtils.UsersUrlEnum.UPDATE, getObject().getId(), null, "RETURN_OBJECT");
    }

    @Override
    protected User getObjectFromUI() {
        User user = getObject();
        DoctorVetApp.TextInputLayoutToObject(findViewById(R.id.lista), user, true, "txt_", true, getApplicationContext());
        return user;
    }

    @Override
    protected User getObject() {
        if (user == null)
            user = MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.USER_OBJ.name()), User.class);

        return user;
    }

    @Override
    protected void setObjectToUI(Object object) {
        User user = (User) object;
        loadThumb(user.getThumb_url());
        DoctorVetApp.ObjectToTextInputLayout(findViewById(R.id.lista), user, "txt_");

        if (user.getRegion() != null)
            txtRegion.getEditText().setText(user.getRegion().getFriendly_name());
    }

    @Override
    protected void remove_thumb(DoctorVetApp.IResourceObject photoObject) {
        super.remove_thumb(getResourceObject());
    }

    @Override
    protected DoctorVetApp.IResourceObject getResourceObject() {
        return getObject();
    }

    @Override
    protected String getUploadTableName() {
        return "users";
    }

    private void setRegionsAdapter(ArrayList<Region> regions) {
        RegionsAdapter regionsAdapter = new RegionsAdapter(regions);
        actv_region.setAdapter(regionsAdapter.getArrayAdapter(EditUserActivity.this));
        actv_region.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getObject().setRegion((Region)adapterView.getItemAtPosition(i));
                txtPhone.requestFocus();
            }
        });
        actv_region.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty())
                    getObject().setRegion(null);
            }
        });
        DoctorVetApp.get().setOnTouchToShowDropDown(actv_region);
        DoctorVetApp.get().setAllWidthToDropDown(actv_region, EditUserActivity.this);
    }

    private boolean validateName() {
        return HelperClass.validateEmpty(txtName);
    }
    private boolean validateRegion() {
        return DoctorVetApp.get().validateExistence(txtRegion, getObject().getRegion(), "friendly_name", true);
    }

}
