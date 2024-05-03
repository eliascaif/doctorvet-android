package com.xionce.doctorvetServices;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.xionce.doctorvetServices.data.Xlsx_identifier;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;

public class EditImportActivity_2 extends EditBaseActivity {

    private static final String TAG = "EditImportActivity_1";
    private Xlsx_identifier xlsx_identifier = null;

    private TextInputLayout txtStartRow;
    private TextInputLayout txtEndRow;
    private Spinner spinner_name_column;
    private Spinner spinner_bar_code_column;
    private Spinner spinner_category_column;
    private Spinner spinner_unit_column;
    private Spinner spinner_complex_quantity_column;
    private Spinner spinner_cost_column;
    private Spinner spinner_tax_column;
    private Spinner spinner_reset_quantity_column;
    private Spinner spinner_min_quantity_column;
    private Spinner spinner_expires_column;
    private Spinner spinner_fixed_price_column_1;
    private Spinner spinner_price_format_column_1;
    private Spinner spinner_margin_column_1;
    private Spinner spinner_fixed_price_column_2;
    private Spinner spinner_price_format_column_2;
    private Spinner spinner_margin_column_2;
    private Spinner spinner_fixed_price_column_3;
    private Spinner spinner_price_format_column_3;
    private Spinner spinner_margin_column_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_import_2);

        hideToolbarImage();
        hideFab();

        toolbar_title.setText("Importar productos");
        toolbar_subtitle.setText("Realiza correspondencias de columnas");

        txtStartRow = findViewById(R.id.txt_start_row);
        txtStartRow.getEditText().setText("3");
        txtEndRow = findViewById(R.id.txt_end_row);

        spinner_bar_code_column = findViewById(R.id.lyt_bar_code_column).findViewById(R.id.spinner_column);
        spinner_name_column = findViewById(R.id.lyt_name_column).findViewById(R.id.spinner_column);
        spinner_category_column = findViewById(R.id.lyt_category_column).findViewById(R.id.spinner_column);
        spinner_unit_column = findViewById(R.id.lyt_unit_column).findViewById(R.id.spinner_column);
        spinner_complex_quantity_column = findViewById(R.id.lyt_complex_quantity_column).findViewById(R.id.spinner_column);
        spinner_cost_column = findViewById(R.id.lyt_cost_column).findViewById(R.id.spinner_column);
        spinner_tax_column = findViewById(R.id.lyt_tax_column).findViewById(R.id.spinner_column);
        spinner_min_quantity_column = findViewById(R.id.lyt_min_quantity_column).findViewById(R.id.spinner_column);
        spinner_expires_column = findViewById(R.id.lyt_expires_column).findViewById(R.id.spinner_column);
        spinner_fixed_price_column_1 = findViewById(R.id.lyt_fixed_price_column_1).findViewById(R.id.spinner_column);
        spinner_price_format_column_1 = findViewById(R.id.lyt_price_format_column_1).findViewById(R.id.spinner_column);
        spinner_margin_column_1 = findViewById(R.id.lyt_margin_column_1).findViewById(R.id.spinner_column);
        spinner_fixed_price_column_2 = findViewById(R.id.lyt_fixed_price_column_2).findViewById(R.id.spinner_column);
        spinner_price_format_column_2 = findViewById(R.id.lyt_price_format_column_2).findViewById(R.id.spinner_column);
        spinner_margin_column_2 = findViewById(R.id.lyt_margin_column_2).findViewById(R.id.spinner_column);
        spinner_fixed_price_column_3 = findViewById(R.id.lyt_fixed_price_column_3).findViewById(R.id.spinner_column);
        spinner_price_format_column_3 = findViewById(R.id.lyt_price_format_column_3).findViewById(R.id.spinner_column);
        spinner_margin_column_3 = findViewById(R.id.lyt_margin_column_3).findViewById(R.id.spinner_column);
        spinner_reset_quantity_column = findViewById(R.id.lyt_reset_quantity_column).findViewById(R.id.spinner_column);

        TextView txt_name_column = findViewById(R.id.lyt_name_column).findViewById(R.id.txt_title);
        TextView txt_bar_code_column = findViewById(R.id.lyt_bar_code_column).findViewById(R.id.txt_title);
        TextView txt_category_column = findViewById(R.id.lyt_category_column).findViewById(R.id.txt_title);
        TextView txt_unit_column = findViewById(R.id.lyt_unit_column).findViewById(R.id.txt_title);
        TextView txt_complex_quantity_column = findViewById(R.id.lyt_complex_quantity_column).findViewById(R.id.txt_title);
        TextView txt_cost_column = findViewById(R.id.lyt_cost_column).findViewById(R.id.txt_title);
        TextView txt_tax_column = findViewById(R.id.lyt_tax_column).findViewById(R.id.txt_title);
        TextView txt_reset_quantity_column = findViewById(R.id.lyt_reset_quantity_column).findViewById(R.id.txt_title);
        TextView txt_min_quantity_column = findViewById(R.id.lyt_min_quantity_column).findViewById(R.id.txt_title);
        TextView txt_expires_column = findViewById(R.id.lyt_expires_column).findViewById(R.id.txt_title);
        TextView txt_fixed_price_column_1 = findViewById(R.id.lyt_fixed_price_column_1).findViewById(R.id.txt_title);
        TextView txt_price_format_column_1 = findViewById(R.id.lyt_price_format_column_1).findViewById(R.id.txt_title);
        TextView txt_margin_column_1 = findViewById(R.id.lyt_margin_column_1).findViewById(R.id.txt_title);
        TextView txt_fixed_price_column_2 = findViewById(R.id.lyt_fixed_price_column_2).findViewById(R.id.txt_title);
        TextView txt_price_format_column_2 = findViewById(R.id.lyt_price_format_column_2).findViewById(R.id.txt_title);
        TextView txt_margin_column_2 = findViewById(R.id.lyt_margin_column_2).findViewById(R.id.txt_title);
        TextView txt_fixed_price_column_3 = findViewById(R.id.lyt_fixed_price_column_3).findViewById(R.id.txt_title);
        TextView txt_price_format_column_3 = findViewById(R.id.lyt_price_format_column_3).findViewById(R.id.txt_title);
        TextView txt_margin_column_3 = findViewById(R.id.lyt_margin_column_3).findViewById(R.id.txt_title);

        txt_name_column.setText("¿Qué columna representa el nombre del producto?");
        txt_bar_code_column.setText("¿Qué columna representa el código de barras?");
        txt_category_column.setText("¿Qué columna representa la categoría?");
        txt_unit_column.setText("¿Qué columna representa la unidad de medida?");
        txt_complex_quantity_column.setText("¿Qué columna representa la cantidad para unidades complejas?");
        txt_cost_column.setText("¿Qué columna representa el costo?");
        txt_tax_column.setText("¿Qué columna representa el impuesto a la venta?");
        txt_min_quantity_column.setText("¿Qué columna representa la cantidad mínima?");
        txt_expires_column.setText("¿Qué columna representa si el producto tiene vencimiento?");
        txt_fixed_price_column_1.setText("¿Qué columna representa el precio fijo 1?");
        txt_price_format_column_1.setText("¿Qué columna representa el cálculo para el precio 1?");
        txt_margin_column_1.setText("¿Qué columna representa el margen de ganancia 1?");
        txt_fixed_price_column_2.setText("¿Qué columna representa el precio fijo 2?");
        txt_price_format_column_2.setText("¿Qué columna representa el cálculo para el precio 2?");
        txt_margin_column_2.setText("¿Qué columna representa el margen de ganancia 2?");
        txt_fixed_price_column_3.setText("¿Qué columna representa el precio fijo 3?");
        txt_price_format_column_3.setText("¿Qué columna representa el cálculo para el precio 3?");
        txt_margin_column_3.setText("¿Qué columna representa el margen de ganancia 3?");
        txt_reset_quantity_column.setText("¿Qué columna representa la cantidad (sobreescribe cantidad actual)?");

        ArrayAdapter<DoctorVetApp.products_columns_xlsx> letterColumnsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, DoctorVetApp.products_columns_xlsx.values());
        letterColumnsAdapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);

        spinner_bar_code_column.setAdapter(letterColumnsAdapter);
        spinner_name_column.setAdapter(letterColumnsAdapter);
        spinner_category_column.setAdapter(letterColumnsAdapter);
        spinner_unit_column.setAdapter(letterColumnsAdapter);
        spinner_complex_quantity_column.setAdapter(letterColumnsAdapter);
        spinner_cost_column.setAdapter(letterColumnsAdapter);
        spinner_tax_column.setAdapter(letterColumnsAdapter);
        spinner_min_quantity_column.setAdapter(letterColumnsAdapter);
        spinner_expires_column.setAdapter(letterColumnsAdapter);
        spinner_fixed_price_column_1.setAdapter(letterColumnsAdapter);
        spinner_price_format_column_1.setAdapter(letterColumnsAdapter);
        spinner_margin_column_1.setAdapter(letterColumnsAdapter);
        spinner_fixed_price_column_2.setAdapter(letterColumnsAdapter);
        spinner_price_format_column_2.setAdapter(letterColumnsAdapter);
        spinner_margin_column_2.setAdapter(letterColumnsAdapter);
        spinner_fixed_price_column_3.setAdapter(letterColumnsAdapter);
        spinner_price_format_column_3.setAdapter(letterColumnsAdapter);
        spinner_margin_column_3.setAdapter(letterColumnsAdapter);
        spinner_reset_quantity_column.setAdapter(letterColumnsAdapter);

        spinner_bar_code_column.setSelection(1);
        spinner_name_column.setSelection(2);
        spinner_category_column.setSelection(3);
        spinner_unit_column.setSelection(4);
        spinner_complex_quantity_column.setSelection(5);
        spinner_cost_column.setSelection(6);
        spinner_tax_column.setSelection(7);
        spinner_min_quantity_column.setSelection(8);
        spinner_expires_column.setSelection(9);
        spinner_fixed_price_column_1.setSelection(10);
        spinner_price_format_column_1.setSelection(11);
        spinner_margin_column_1.setSelection(12);
        spinner_fixed_price_column_2.setSelection(13);
        spinner_price_format_column_2.setSelection(14);
        spinner_margin_column_2.setSelection(15);
        spinner_fixed_price_column_3.setSelection(16);
        spinner_price_format_column_3.setSelection(17);
        spinner_margin_column_3.setSelection(18);
        spinner_reset_quantity_column.setSelection(20);

        Button btn_continue = findViewById(R.id.btn_continue);
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
        });

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
    protected Object getObject() {
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

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {

    }

    private String getFileIdentifier() {
        return getIntent().getStringExtra("xlsx_identifier");
    }
    private void next() {
        if (!validateStartRow() || !validateEndRow() || !validateNameColumn())
            return;

        try {
            showWaitDialog();

            Xlsx_identifier xlsx_identifier = new Xlsx_identifier();
            xlsx_identifier.setStart_row(Integer.parseInt(txtStartRow.getEditText().getText().toString()));
            xlsx_identifier.setEnd_row(Integer.parseInt(txtEndRow.getEditText().getText().toString()));
            xlsx_identifier.setFile_identifier(getFileIdentifier());

            xlsx_identifier.setName_column((DoctorVetApp.products_columns_xlsx)spinner_name_column.getSelectedItem());

            if (!spinner_bar_code_column.getSelectedItem().toString().equalsIgnoreCase("na"))
                xlsx_identifier.setBar_code_column((DoctorVetApp.products_columns_xlsx)spinner_bar_code_column.getSelectedItem());

            if (!spinner_category_column.getSelectedItem().toString().equalsIgnoreCase("na"))
                xlsx_identifier.setCategory_column((DoctorVetApp.products_columns_xlsx)spinner_category_column.getSelectedItem());

            if (!spinner_unit_column.getSelectedItem().toString().equalsIgnoreCase("na"))
                xlsx_identifier.setUnit_column((DoctorVetApp.products_columns_xlsx)spinner_unit_column.getSelectedItem());

            if (!spinner_complex_quantity_column.getSelectedItem().toString().equalsIgnoreCase("na"))
                xlsx_identifier.setComplex_quantity_column((DoctorVetApp.products_columns_xlsx)spinner_complex_quantity_column.getSelectedItem());

            if (!spinner_cost_column.getSelectedItem().toString().equalsIgnoreCase("na"))
                xlsx_identifier.setCost_column((DoctorVetApp.products_columns_xlsx)spinner_cost_column.getSelectedItem());

            if (!spinner_tax_column.getSelectedItem().toString().equalsIgnoreCase("na"))
                xlsx_identifier.setTax_column((DoctorVetApp.products_columns_xlsx)spinner_tax_column.getSelectedItem());

            if (!spinner_min_quantity_column.getSelectedItem().toString().equalsIgnoreCase("na"))
                xlsx_identifier.setMin_quantity_column((DoctorVetApp.products_columns_xlsx)spinner_min_quantity_column.getSelectedItem());

            if (!spinner_expires_column.getSelectedItem().toString().equalsIgnoreCase("na"))
                xlsx_identifier.setExpires_column((DoctorVetApp.products_columns_xlsx)spinner_expires_column.getSelectedItem());

            if (!spinner_fixed_price_column_1.getSelectedItem().toString().equalsIgnoreCase("na"))
                xlsx_identifier.setFixed_price_1_column((DoctorVetApp.products_columns_xlsx) spinner_fixed_price_column_1.getSelectedItem());

            if (!spinner_price_format_column_1.getSelectedItem().toString().equalsIgnoreCase("na"))
                xlsx_identifier.setFormat_price_1_column((DoctorVetApp.products_columns_xlsx) spinner_price_format_column_1.getSelectedItem());

            if (!spinner_margin_column_1.getSelectedItem().toString().equalsIgnoreCase("na"))
                xlsx_identifier.setMargin_price_1_column((DoctorVetApp.products_columns_xlsx) spinner_margin_column_1.getSelectedItem());

            if (!spinner_fixed_price_column_2.getSelectedItem().toString().equalsIgnoreCase("na"))
                xlsx_identifier.setFixed_price_2_column((DoctorVetApp.products_columns_xlsx) spinner_fixed_price_column_2.getSelectedItem());

            if (!spinner_price_format_column_2.getSelectedItem().toString().equalsIgnoreCase("na"))
                xlsx_identifier.setFormat_price_2_column((DoctorVetApp.products_columns_xlsx) spinner_price_format_column_2.getSelectedItem());

            if (!spinner_margin_column_2.getSelectedItem().toString().equalsIgnoreCase("na"))
                xlsx_identifier.setMargin_price_2_column((DoctorVetApp.products_columns_xlsx) spinner_margin_column_2.getSelectedItem());

            if (!spinner_fixed_price_column_3.getSelectedItem().toString().equalsIgnoreCase("na"))
                xlsx_identifier.setFixed_price_3_column((DoctorVetApp.products_columns_xlsx) spinner_fixed_price_column_3.getSelectedItem());

            if (!spinner_price_format_column_3.getSelectedItem().toString().equalsIgnoreCase("na"))
                xlsx_identifier.setFormat_price_3_column((DoctorVetApp.products_columns_xlsx) spinner_price_format_column_3.getSelectedItem());

            if (!spinner_margin_column_3.getSelectedItem().toString().equalsIgnoreCase("na"))
                xlsx_identifier.setMargin_price_3_column((DoctorVetApp.products_columns_xlsx) spinner_margin_column_3.getSelectedItem());

            if (!spinner_reset_quantity_column.getSelectedItem().toString().equalsIgnoreCase("na"))
                xlsx_identifier.setReset_quantity_column((DoctorVetApp.products_columns_xlsx)spinner_reset_quantity_column.getSelectedItem());

            final String xlsx_json_object = MySqlGson.postGson().toJson(xlsx_identifier);
            URL url = NetworkUtils.buildGetXlsx2Url();
            TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.POST, url.toString(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        String success = MySqlGson.getStatusFromResponse(response);
                        if (success.equalsIgnoreCase("success")){
                            Toast.makeText(EditImportActivity_2.this, "Importación ok", Toast.LENGTH_SHORT).show();
                            setResult(HelperClass.REQUEST_FINISH);
                            finish();
                        } else {
                            Snackbar.make(DoctorVetApp.getRootForSnack(EditImportActivity_2.this), "No se puede realizar la importación", Snackbar.LENGTH_SHORT).show();
                        }
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
                    return xlsx_json_object.getBytes();
                }
            };
            DoctorVetApp.get().addToRequestQueque(stringRequest);
        } catch (Exception e) {
            DoctorVetApp.get().handle_error(e, TAG, true);
        }

    }

    private boolean validateNameColumn() {
        DoctorVetApp.products_columns_xlsx selected_column = (DoctorVetApp.products_columns_xlsx) spinner_name_column.getSelectedItem();
        if (selected_column == DoctorVetApp.products_columns_xlsx.NA) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), "Selecciona la columna referida al nombre del producto", Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
    private boolean validateStartRow() {
        return HelperClass.validateNumberNotZero(txtStartRow, false);
    }
    private boolean validateEndRow() {
        return HelperClass.validateNumberNotZero(txtEndRow, false);
    }

}
