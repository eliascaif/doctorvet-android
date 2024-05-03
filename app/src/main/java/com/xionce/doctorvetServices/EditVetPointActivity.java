package com.xionce.doctorvetServices;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.xionce.doctorvetServices.data.FinanceTypesReceiptsAdapter;
import com.xionce.doctorvetServices.data.Finance_types_receipts;
import com.xionce.doctorvetServices.data.Vet_point;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;

public class EditVetPointActivity extends EditBaseActivity {

    private static final String TAG = "EditVetPointActivity";
    private TextInputLayout txtName;
    private Spinner cmbReceiptType;
    private TextInputLayout txtNumber;
    private TextInputLayout txtCounter;
    private Spinner cmbSubtype;

    private Vet_point vetPoint = null;
    private FinanceTypesReceiptsAdapter financeTypesReceiptsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_vet_sell_point);
        cmbReceiptType = findViewById(R.id.cmb_recipt_type);
        txtName = findViewById(R.id.txt_name);
        txtNumber = findViewById(R.id.txt_number);
        txtCounter = findViewById(R.id.txt_counter);
        cmbSubtype = findViewById(R.id.cmb_type);
        DoctorVetApp.get().markRequired(txtName);
        DoctorVetApp.get().markRequired(txtNumber);
        DoctorVetApp.get().markRequired(txtCounter);
        hideToolbarImage();
        toolbar_title.setText(R.string.new_point);
        toolbar_subtitle.setText(R.string.adding_new_point);

        initializeInitRequestNumber(1);
        DoctorVetApp.get().getFinanceTypesReceipts(new DoctorVetApp.VolleyCallbackAdapter() {
            @Override
            public void onSuccess(RecyclerView.Adapter resultAdapter) {
                setRequestCompleted();
                financeTypesReceiptsAdapter = (FinanceTypesReceiptsAdapter) resultAdapter;
                cmbReceiptType.setAdapter(financeTypesReceiptsAdapter.getArrayAdapter(EditVetPointActivity.this));
                cmbReceiptType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        getObject().setFinance_types_receipt((Finance_types_receipts) adapterView.getItemAtPosition(i));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        getObject().setFinance_types_receipt(null);
                    }
                });
            }
        });

        if (savedInstanceState != null)
            restoreFromBundle(savedInstanceState);

        ArrayAdapter<DoctorVetApp.Vet_points_types> spinnerSellPointsTypesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, DoctorVetApp.Vet_points_types.values());
        spinnerSellPointsTypesAdapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);
        cmbSubtype.setAdapter(spinnerSellPointsTypesAdapter);

        Vet_point vetPoint = getObject();
        setObjectToUI(vetPoint);

        txtName.requestFocus();
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString("vet_point");
        vetPoint = MySqlGson.getGson().fromJson(objectInString, Vet_point.class);
    }

    @Override
    protected void save() {
        if (!validateName() || !validateNumber() || !validateCounter())
            return;

        showWaitDialog();

        Vet_point vetSellPoint = getObjectFromUI();
        String sellPointJsonObject = MySqlGson.getPostJsonString(vetSellPoint);
        TokenStringRequest stringRequest = new TokenStringRequest(getMethod(), getUrl().toString(), response -> {
            try {
                String success = MySqlGson.getStatusFromResponse(response);
                if (success.equalsIgnoreCase("success"))
                    finish();
            } catch (Exception ex) {
                DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
            } finally {
                hideWaitDialog();
                finish();
            }
        }, error -> {
            DoctorVetApp.get().handle_volley_error(error, TAG, true);
            hideWaitDialog();
        })
        {
            @Override
            public byte[] getBody() {
                return sellPointJsonObject.getBytes();
            }
        };
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    @Override
    protected void update(){
        save();
    }

    @Override
    protected URL getUrl() {
        return NetworkUtils.buildVetUrl(NetworkUtils.vetsUrl.CREATE_POINT, null);
    }

    @Override
    protected Vet_point getObjectFromUI() {
        Vet_point vetPoint = getObject();
        vetPoint.setName(txtName.getEditText().getText().toString());
        vetPoint.setNumber(Integer.parseInt(txtNumber.getEditText().getText().toString()));
        vetPoint.setCounter(Integer.parseInt(txtCounter.getEditText().getText().toString()));
        vetPoint.setType(DoctorVetApp.Vet_points_types.getEnumVal(cmbSubtype.getSelectedItem().toString()).name());
        vetPoint.setFinance_types_receipt((Finance_types_receipts)cmbReceiptType.getSelectedItem());
        return vetPoint;
    }

    @Override
    protected Vet_point getObject() {
        if (vetPoint == null) {
            if (isUpdate()) {
                vetPoint = MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.VET_POINT_OBJ.name()), Vet_point.class);
            } else {
                vetPoint = new Vet_point();
            }
        }

        return vetPoint;
    }

    @Override
    protected void setObjectToUI(Object object) {
        Vet_point vet_point = (Vet_point) object;
        DoctorVetApp.ObjectToTextInputLayout(findViewById(R.id.lista), vet_point, "txt_");
    }

    @Override
    protected void remove_thumb(DoctorVetApp.IResourceObject photoObject) {
    }

    @Override
    protected DoctorVetApp.IResourceObject getResourceObject() {
        return null;
    }

    @Override
    protected String getUploadTableName() {
        return null;
    }

    private boolean validateName() {
        return HelperClass.validateEmpty(txtName);
    }
    private boolean validateNumber() {
        return HelperClass.validateEmpty(txtNumber);
    }
    private boolean validateCounter() {
        return HelperClass.validateEmpty(txtCounter);
    }

}
