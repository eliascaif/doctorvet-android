package com.xionce.doctorvetServices;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.reflect.TypeToken;
import com.xionce.doctorvetServices.data.User;
import com.xionce.doctorvetServices.data.UsersAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ViewVetUsersActivity extends ViewBaseActivity {

    private static final String TAG = "ViewVetUsersActivity";
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_view_vet_users);

        recyclerView = findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        toolbar_title.setText("Usuarios y roles");
        hideSubtitle();
        hideToolbarImage();
        hideFab();

        hideActivityContainer();
        setSwipeRefreshLayout();
    }

    private void showVetUsers() {
        showProgressBar();

        URL url = NetworkUtils.buildUsersMinUrl();
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            ArrayList<User> users = MySqlGson.getGson().fromJson(data, new TypeToken<List<User>>(){}.getType());
                            UsersAdapter usersAdapter = new UsersAdapter(users, UsersAdapter.Adapter_types.PROMOTE);
                            usersAdapter.setOnUpgradeClickHandler(new HelperClass.AdapterOnClickHandler() {
                                @Override
                                public void onClick(Object data, View view, int pos) {
                                    upgradeUser((User)data);
                                }
                            });
                            usersAdapter.setOnDowngradeClickHandler(new HelperClass.AdapterOnClickHandler() {
                                @Override
                                public void onClick(Object data, View view, int pos) {
                                    downgradeUser((User)data);
                                }
                            });
                            usersAdapter.setOnRemoveClickHandler(new HelperClass.AdapterOnClickHandler() {
                                @Override
                                public void onClick(Object data, View view, int pos) {
                                    removeUser((User)data);
                                }
                            });
                            usersAdapter.setOnEditClickHandler(new HelperClass.AdapterOnClickHandler() {
                                @Override
                                public void onClick(Object data, View view, int pos) {
                                    editUser((User)data);
                                }
                            });

                            recyclerView.setAdapter(usersAdapter);
                            setLoadedFinished();
                            showActivityContainer();
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                        } finally {
                            hideProgressBar();
                            hideSwipeRefreshLayoutProgressBar();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    @Override
    protected void setUI(Object object) {
    }

    @Override
    protected void invisibilizeEmptyViews() {
    }

    @Override
    protected void refreshView() {
        showVetUsers();
    }

    @Override
    protected void go_update() {
    }

    private void go_insert_pet() {
    }

    @Override
    protected void go_delete() {
    }

    @Override
    protected void on_update_complete(Intent data) {
    }

    @Override
    protected void on_delete_complete(Integer deleted_id) {
    }

    private void upgradeUser(User user) {
        if (DoctorVetApp.get().getUser().getRol().equalsIgnoreCase("user")) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), "Privilegios insuficientes para realizar la acción", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (DoctorVetApp.get().getUser().getRol().equalsIgnoreCase("admin")
            && user.getRol().equalsIgnoreCase("admin")) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), "Privilegios insuficientes para realizar la acción", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (user.getRol().equalsIgnoreCase("owner")) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), "Usuario tiene el privilegio más alto", Snackbar.LENGTH_SHORT).show();
            return;
        }

        HelperClass.getOKCancelDialog(ViewVetUsersActivity.this, "¿Promover usuario?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showWaitDialog();

                User promoteUser = new User();
                promoteUser.setId(user.getId());
                promoteUser.setRol(getUpgradeRol(user.getRol()));
                String promoteUser_json = MySqlGson.postGson().toJson(promoteUser);

                URL promoteUserUrl = NetworkUtils.buildUsersUrl(NetworkUtils.UsersUrlEnum.PROMOTE, user.getId(), null, null);
                TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.PUT, promoteUserUrl.toString(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    String data = MySqlGson.getStatusFromResponse(response);
                                    if (data.equalsIgnoreCase("success")) {
                                        refreshView();
                                    } else {
                                        showErrorToast(getString(R.string.error_update), TAG);
                                    }
                                } catch (Exception ex) {
                                    DoctorVetApp.get().handle_onResponse_error(ex, /*ViewVetUsersActivity.this,*/ TAG, true, response);
                                } finally {
                                    hideWaitDialog();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                DoctorVetApp.get().handle_volley_error(error, /*ViewVetUsersActivity.this,*/ TAG, true);
                                hideWaitDialog();
                            }
                        }
                )
                {
                    @Override
                    public byte[] getBody() {
                        return promoteUser_json.getBytes();
                    }
                };
                DoctorVetApp.get().addToRequestQueque(stringRequest);
            }
        });

    }
    private void downgradeUser(User user) {
        if (DoctorVetApp.get().getUser().getRol().equalsIgnoreCase("user")) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), "Privilegios insuficientes para realizar la acción", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (DoctorVetApp.get().getUser().getRol().equalsIgnoreCase("admin")
                && user.getRol().equalsIgnoreCase("admin")) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), "Privilegios insuficientes para realizar la acción", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (user.getRol().equalsIgnoreCase("user")) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), "Usuario tiene el privilegio más bajo", Snackbar.LENGTH_SHORT).show();
            return;
        }

        HelperClass.getOKCancelDialog(ViewVetUsersActivity.this, "¿Quitar privilegios?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showWaitDialog();

                User downgradeUser = new User();
                downgradeUser.setId(user.getId());
                downgradeUser.setRol(getDowngradeRol(user.getRol()));
                String promoteUser_json = MySqlGson.postGson().toJson(downgradeUser);

                URL promoteUserUrl = NetworkUtils.buildUsersUrl(NetworkUtils.UsersUrlEnum.PROMOTE, user.getId(), null, null);
                TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.PUT, promoteUserUrl.toString(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    String data = MySqlGson.getStatusFromResponse(response);
                                    if (data.equalsIgnoreCase("success")) {
                                        refreshView();
                                    } else {
                                        showErrorToast(getString(R.string.error_update), TAG);
                                    }
                                } catch (Exception ex) {
                                    DoctorVetApp.get().handle_onResponse_error(ex, /*ViewVetUsersActivity.this,*/ TAG, true, response);
                                } finally {
                                    hideWaitDialog();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                DoctorVetApp.get().handle_volley_error(error, /*ViewVetUsersActivity.this,*/ TAG, true);
                                hideWaitDialog();
                            }
                        }
                )
                {
                    @Override
                    public byte[] getBody() {
                        return promoteUser_json.getBytes();
                    }
                };
                DoctorVetApp.get().addToRequestQueque(stringRequest);
            }
        });

    }
    private void removeUser(User user) {
        if (!DoctorVetApp.get().getUser().getRol().equalsIgnoreCase("owner")) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), "Privilegios insuficientes para realizar la acción", Snackbar.LENGTH_SHORT).show();
            return;
        }

        HelperClass.getOKCancelDialog(ViewVetUsersActivity.this, "¿Eliminar?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showWaitDialog();

                User removeUser = new User();
                removeUser.setId(user.getId());
                String promoteUser_json = MySqlGson.postGson().toJson(removeUser);

                URL promoteUserUrl = NetworkUtils.buildUsersUrl(NetworkUtils.UsersUrlEnum.REMOVE_FROM_VET, user.getId(), null, null);
                TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.PUT, promoteUserUrl.toString(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    String data = MySqlGson.getStatusFromResponse(response);
                                    if (data.equalsIgnoreCase("success")) {
                                        refreshView();
                                    } else {
                                        showErrorToast(getString(R.string.error_update), TAG);
                                    }
                                } catch (Exception ex) {
                                    DoctorVetApp.get().handle_onResponse_error(ex, /*ViewVetUsersActivity.this,*/ TAG, true, response);
                                } finally {
                                    hideWaitDialog();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                DoctorVetApp.get().handle_volley_error(error, /*ViewVetUsersActivity.this,*/ TAG, true);
                                hideWaitDialog();
                            }
                        }
                )
                {
                    @Override
                    public byte[] getBody() {
                        return promoteUser_json.getBytes();
                    }
                };
                DoctorVetApp.get().addToRequestQueque(stringRequest);
            }
        });

    }
    private void editUser(User user) {
        if (!DoctorVetApp.get().getUser().getRol().equalsIgnoreCase("owner")) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), "Privilegios insuficientes para realizar la acción", Snackbar.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(ViewVetUsersActivity.this, EditUserPermissions.class);
        intent.putExtra(DoctorVetApp.INTENT_VALUES.USER_OBJ.name(), MySqlGson.getGson().toJson(user));
        startActivityForResult(intent, HelperClass.REQUEST_UPDATE);
    }

    private String getUpgradeRol(String rol) {
        if (rol.equalsIgnoreCase("user"))
            return "ADMIN";

        if (rol.equalsIgnoreCase("admin"))
            return "OWNER";

        return "";
    }
    private String getDowngradeRol(String rol) {
        if (rol.equalsIgnoreCase("owner"))
            return "ADMIN";

        if (rol.equalsIgnoreCase("admin"))
            return "USER";

        return "";
    }

}
