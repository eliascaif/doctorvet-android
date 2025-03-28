package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.xionce.doctorvetServices.data.Region;
import com.xionce.doctorvetServices.data.User;
import com.xionce.doctorvetServices.data.Users_permissions;
import com.xionce.doctorvetServices.data.Users_permissions_crud;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EditUserPermissions extends EditBaseActivity {

    private static final String TAG = "EditUserPermissions";
    private Users_permissions userPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_user_permissions);
        hideToolbarImage();

        if (savedInstanceState == null) {
            initializeInitRequestNumber(1);
            DoctorVetApp.get().getUserPermissions(getUser().getId(), new DoctorVetApp.VolleyCallbackObject() {
                @Override
                public void onSuccess(Object result) {
                    setRequestCompleted();
                    userPermissions = (Users_permissions)result;
                    setObjectToUI(userPermissions);
                }
            });
        } else {
            restoreFromBundle(savedInstanceState);
        }

        toolbar_title.setText(R.string.editar_user);
        toolbar_subtitle.setText(R.string.edit_subtitle);

//        setObjectToUI(userPermissions);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("user_permissions", MySqlGson.getGson().toJson(getObjectFromUI()));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString("user_permissions");
        userPermissions = MySqlGson.getGson().fromJson(objectInString, Users_permissions.class);
    }

    @Override
    protected void save() {
    }

    @Override
    protected void update(){
        showWaitDialog();

        final Users_permissions userPermissions = getObjectFromUI();
        final String user_json_object = MySqlGson.postGson().toJson(userPermissions);
        TokenStringRequest stringRequest = new TokenStringRequest(getMethod(), getUrl().toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
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
        return NetworkUtils.buildUsersUrl(NetworkUtils.UsersUrlEnum.SET_PERMISSIONS, getUser().getId(), null, null);
    }

    @Override
    protected Users_permissions getObjectFromUI() {
        Users_permissions users_permissions = getObject();

        Field[] actionPermissionsFields = Users_permissions.ActionsPermissions.class.getDeclaredFields();
        for (Field actionPermissionField : actionPermissionsFields) {
            try {
                String field_name = actionPermissionField.getName();
                Users_permissions_crud field_value = new Users_permissions_crud();

                String chk_name = "chk_action_" + field_name + "_create";
                CheckBox chk = (CheckBox) DoctorVetApp.get().findViewByName(findViewById(R.id.lista), chk_name);
                if (chk == null) continue;
                if (chk.isChecked())
                    field_value.create = 1;

                chk_name = "chk_action_" + field_name + "_read";
                chk = (CheckBox) DoctorVetApp.get().findViewByName(findViewById(R.id.lista), chk_name);
                if (chk.isChecked())
                    field_value.read = 1;

                chk_name = "chk_action_" + field_name + "_update";
                chk = (CheckBox) DoctorVetApp.get().findViewByName(findViewById(R.id.lista), chk_name);
                if (chk.isChecked())
                    field_value.update = 1;

                chk_name = "chk_action_" + field_name + "_delete";
                chk = (CheckBox) DoctorVetApp.get().findViewByName(findViewById(R.id.lista), chk_name);
                if (chk.isChecked())
                    field_value.delete = 1;

                actionPermissionField.setAccessible(true);
                actionPermissionField.set(users_permissions.actions_permissions, field_value);
            } catch (IllegalAccessException e) {
                //throw new RuntimeException(e);
            }
        }

        //reports
        Field[] reportPermissionsFields = Users_permissions.ReportsPermissions.class.getDeclaredFields();
        for (Field reportPermissionsField : reportPermissionsFields) {
            try {
                String field_name = reportPermissionsField.getName();
                int field_value = 0;

                String chk_name = "chk_report_" + field_name + "_read";
                CheckBox chk = (CheckBox) DoctorVetApp.get().findViewByName(findViewById(R.id.lista), chk_name);
                if (chk == null) continue;
                if (chk.isChecked())
                    field_value = 1;

                reportPermissionsField.setAccessible(true);
                reportPermissionsField.set(users_permissions.reports_permissions, field_value);
            } catch (IllegalAccessException e) {
                //throw new RuntimeException(e);
            }
        }

        return userPermissions;
    }

    @Override
    protected Users_permissions getObject() {
        return userPermissions;
    }

    @Override
    protected void setObjectToUI(Object object) {
        Users_permissions users_permissions = (Users_permissions) object;

        Field[] actionPermissionsFields = Users_permissions.ActionsPermissions.class.getDeclaredFields();
        for (Field actionPermissionField : actionPermissionsFields) {
            try {
                String field_name = actionPermissionField.getName();
                Object field_value = actionPermissionField.get(users_permissions.actions_permissions);

                String chk_name = "chk_action_" + field_name + "_create";
                CheckBox chk = (CheckBox) DoctorVetApp.get().findViewByName(findViewById(R.id.lista), chk_name);
                if (((Users_permissions_crud)field_value).create == 1)
                    chk.setChecked(true);

                chk_name = "chk_action_" + field_name + "_read";
                chk = (CheckBox) DoctorVetApp.get().findViewByName(findViewById(R.id.lista), chk_name);
                if (((Users_permissions_crud)field_value).read == 1)
                    chk.setChecked(true);

                chk_name = "chk_action_" + field_name + "_update";
                chk = (CheckBox) DoctorVetApp.get().findViewByName(findViewById(R.id.lista), chk_name);
                if (((Users_permissions_crud)field_value).update == 1)
                    chk.setChecked(true);

                chk_name = "chk_action_" + field_name + "_delete";
                chk = (CheckBox) DoctorVetApp.get().findViewByName(findViewById(R.id.lista), chk_name);
                if (((Users_permissions_crud)field_value).delete == 1)
                    chk.setChecked(true);

            } catch (IllegalAccessException e) {
                //throw new RuntimeException(e);
            }
        }

        //reports
        Field[] reportPermissionsFields = Users_permissions.ReportsPermissions.class.getDeclaredFields();
        for (Field reportPermissionsField : reportPermissionsFields) {
            try {
                String field_name = reportPermissionsField.getName();
                Object field_value = reportPermissionsField.get(users_permissions.reports_permissions);

                String chk_name = "chk_report_" + field_name + "_read";
                android.util.Log.i("CHKREPORT", chk_name);
                CheckBox chk = (CheckBox) DoctorVetApp.get().findViewByName(findViewById(R.id.lista), chk_name);
                if ((Integer)field_value == 1)
                    chk.setChecked(true);
            } catch (IllegalAccessException e) {
                //throw new RuntimeException(e);
            }
        }

        User user = MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.USER_OBJ.name()), User.class);
        toolbar_title.append(" " + user.getName());
    }

    @Override
    protected DoctorVetApp.IResourceObject getResourceObject() {
        return null;
    }

    @Override
    protected String getUploadTableName() {
        return null;
    }

    private User getUser() {
        return MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.USER_OBJ.name()), User.class);
    }
}
