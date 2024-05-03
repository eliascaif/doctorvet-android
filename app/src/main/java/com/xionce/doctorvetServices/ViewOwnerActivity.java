package com.xionce.doctorvetServices;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.xionce.doctorvetServices.data.Owner;
import com.xionce.doctorvetServices.data.Pet;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;

public class ViewOwnerActivity extends ViewBaseActivity
        implements BottomSheetDialog.BottomSheetListener, DoctorVetApp.IProgressBarActivity {

    private static final String TAG = "ViewOwnerActivity";
    private Owner owner = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_view_owner);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheet = new BottomSheetDialog();
                Bundle b = new Bundle();
                b.putString("propietario_especifico", "true");
                bottomSheet.setArguments(b);
                bottomSheet.show(getSupportFragmentManager(), "bottomSheetDialog");
            }
        });

        hideActivityContainer();
        setSwipeRefreshLayout();
    }

    private void showOwner() {
        showProgressBar();
        Integer id_owner = getIdOwner();

        Integer update_last_view = getIntent().getIntExtra(DoctorVetApp.INTENT_UPDATE_LAST_VIEW, 0);
        if (update_last_view == 1)
            getIntent().putExtra(DoctorVetApp.INTENT_UPDATE_LAST_VIEW, 0);

        DoctorVetApp.get().getOwner(id_owner, update_last_view, new DoctorVetApp.VolleyCallbackOwner() {
            @Override
            public void onSuccess(Owner resultOwner) {
                try {
                    showActivityContainer();
                    if (resultOwner != null) {
                        owner = resultOwner;
                        setUI(owner);
                        setLoadedFinished();
                    } else {
                        showErrorMessage();
                    }
                } catch (Exception ex) {
                    DoctorVetApp.get().handle_error(ex, /*ViewOwnerActivity.this,*/ TAG, true);
                    showErrorMessage();
                } finally {
                    hideProgressBar();
                    hideSwipeRefreshLayoutProgressBar();
                }
            }
        });
    }

    @Override
    protected void setUI(Object object) {
        Owner owner = (Owner) object;

        Glide.with(getApplicationContext()).load(R.drawable.ic_account_circle_dark).apply(RequestOptions.fitCenterTransform()).into(toolbar_image);

        toolbar_title.setText(owner.getName());
        String email = owner.getEmail();
        if (email != null)
            toolbar_subtitle.setText(email);

        ((TextView)findViewById(R.id.titulo)).setText(DoctorVetApp.get().getPetNamingPlural());

        DoctorVetApp.ObjectToTextView(findViewById(R.id.lista_datos_owner), owner, "txt_");

        TextView txt_email = findViewById(R.id.txt_email);
        txt_email.setTextColor(Color.BLACK);
        if (owner.getEmail() == null || owner.getEmail().isEmpty()) {
            txt_email.setText(R.string.agregar_email);
            txt_email.setTextColor(Color.BLUE);
            txt_email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DoctorVetApp.get().showAddEmailModal(owner, ViewOwnerActivity.this, new DoctorVetApp.VolleyCallbackOwner() {
                        @Override
                        public void onSuccess(Owner resultOwner) {
                            if (resultOwner != null) {
                                txt_email.setTextColor(Color.BLACK);
                                txt_email.setClickable(false);
                                refreshView();
                            }
                        }
                    });
                }
            });
        }

        TextView txt_phone = findViewById(R.id.txt_phone);
        txt_phone.setTextColor(Color.BLACK);
        if (owner.getPhone() == null || owner.getPhone().isEmpty()) {
            txt_phone.setText(R.string.agregar_movil);
            txt_phone.setTextColor(Color.BLUE);
            txt_phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DoctorVetApp.get().showAddTelefonoModal(owner, ViewOwnerActivity.this, new DoctorVetApp.VolleyCallbackOwner() {
                        @Override
                        public void onSuccess(Owner resultOwner) {
                            if (resultOwner != null) {
                                txt_phone.setTextColor(Color.BLACK);
                                txt_phone.setClickable(false);
                                refreshView();
                            }
                        }
                    });
                }
            });
        }

        TextView txt_region = findViewById(R.id.txt_region);
        if (owner.getRegion() != null)
            txt_region.setText(owner.getRegion().getFriendly_name());

        super.setPhoto(owner.getThumb_url(), owner.getPhoto_url());

        //llenar linear de mascotas
        final LinearLayout listaPets = findViewById(R.id.linear_pets);
        final Context ctx = this;
        listaPets.removeAllViews();
        ArrayList<Pet> mascotas_del_owner = owner.getPets();
        if (!mascotas_del_owner.isEmpty()) {
            //listaPets.setVisibility(View.VISIBLE);
            for (final Pet pet : mascotas_del_owner) {
                View newMascotaRectangle = getLayoutInflater().inflate(R.layout.list_item_rectangle, null);

                ImageView thumb = newMascotaRectangle.findViewById(R.id.scuare_thumb);
                TextView name = newMascotaRectangle.findViewById(R.id.txt_scuare);
                TextView info = newMascotaRectangle.findViewById(R.id.txt_info);
                name.setText(pet.getName());

                DoctorVetApp.get().setThumb(pet.getThumb_url(), thumb, R.drawable.ic_dog);

                newMascotaRectangle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ctx, ViewPetActivity.class);
                        intent.putExtra(DoctorVetApp.INTENT_VALUES.PET_ID.name(), pet.getId());

                        ctx.startActivity(intent);
                        if (DoctorVetApp.views_abiertos > 1)
                            finish();
                    }
                });

                listaPets.addView(newMascotaRectangle);
            }
        } else {
            //sin mascotas asociadas
            View list_item_sin_relaciones = getLayoutInflater().inflate(R.layout.list_item_no_relations, null);
            listaPets.addView(list_item_sin_relaciones);
        }

        //el telefono en clase base
        setPhone(owner.getPhone());

        //owner info/states
        final LinearLayout linear_owner_info = findViewById(R.id.linear_owner_info);
        linear_owner_info.removeAllViews();

        //debt
        if (owner.getBalance() != null && !owner.getBalance().equals(BigDecimal.ZERO)) {
            linear_owner_info.addView(DoctorVetApp.get().getOwnerDebtUI(owner, getLayoutInflater(), ViewOwnerActivity.this));
        }

        invisibilizeEmptyViews();
    }

    @Override
    protected void invisibilizeEmptyViews() {
        LinearLayout linearOwners = findViewById(R.id.lista_datos_owner);
        DoctorVetApp.visibilizeNonEmptyTextView(linearOwners);
    }

    @Override
    protected void refreshView() {
        showOwner();
    }

    @Override
    protected void go_update() {
        Intent intent = new Intent(this, EditOwnerActivity.class);
        intent.putExtra(DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name(), MySqlGson.getGson().toJson(owner));
        startActivityForResult(intent, HelperClass.REQUEST_UPDATE);
    }

    private void go_insert_pet() {
        Intent intent = new Intent(ViewOwnerActivity.this, EditPetActivity.class);
        intent.putExtra(DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name(), MySqlGson.getGson().toJson(owner));
        startActivity(intent);
    }

    private void go_insert_sell() {
        Intent intent = new Intent(ViewOwnerActivity.this, EditSellActivity_1.class);
        intent.putExtra(DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name(), MySqlGson.getGson().toJson(owner));
        startActivity(intent);
    }

    @Override
    protected void go_delete() {
        HelperClass.getOKCancelDialog(this, getString(R.string.action_delete_pregunta), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showWaitDialog();
                final Integer id_owner = getIdOwner();
                URL delete_ownerUrl = NetworkUtils.buildDeletePropietarioUrl(id_owner);
                TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.DELETE, delete_ownerUrl.toString(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                String success = MySqlGson.getStatusFromResponse(response);
                                if (success.equalsIgnoreCase("success")) {
                                    on_delete_complete(id_owner);
                                    finish();
                                } else {
                                    showErrorToast(getString(R.string.error_borrando_registro), TAG);
                                }
                            } catch (Exception ex) {
                                DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                            } finally {
                                hideWaitDialog();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            DoctorVetApp.get().handle_volley_error(error, TAG, true);
                            hideWaitDialog();
                        }
                    }
                );
                DoctorVetApp.get().addToRequestQueque(stringRequest);
            }
        });
    }

    @Override
    protected void on_update_complete(Intent data) {
//        owner = MySqlGson.getGson(true, false).fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name()), Owner.class);
//        setUI(owner);
        //DoctorVetApp.getInstance().updateToOwnersAdapter_cache(propietario);
    }

    @Override
    protected void on_delete_complete(Integer deleted_id) {
        //DoctorVetApp.getInstance().deleteToOwnersAdapter_cache(deleted_id);
    }

    private Integer getIdOwner() {
        return getIntent().getIntExtra(DoctorVetApp.INTENT_VALUES.OWNER_ID.name(), 0);
    }

    @Override
    public void onButtonClicked(BottomSheetDialog.BottomSheetButtonClicked buttonClicked) {
        if (!loadedFinished) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), R.string.error_cargando_registro, Snackbar.LENGTH_SHORT).show();
            return;
        }

        switch (buttonClicked) {
            case OWNER_ADD_PET:
                go_insert_pet();
                break;
            case OWNER_UPDATE:
                go_update();
                break;
            case OWNER_DELETE:
                go_delete();
                break;
            case SELLS_TO_OWNER_NEW:
                go_insert_sell();
                break;
            case COMUNICATION_CALL:
                sendCall(owner.getPhone());
                break;
            case COMUNICATION_WHATSAPP:
                sendWhatsAppMessage(owner.getPhone());
                break;
            case COMUNICATION_EMAIL:
                try {
                    HelperClass.sendEmail(ViewOwnerActivity.this, owner.getEmail());
                } catch (HelperClass.EnterPhoneException ex) {
                    Snackbar.make(DoctorVetApp.getRootForSnack(this), getString(R.string.error_falta_email), Snackbar.LENGTH_SHORT).show();
                }
                break;
            case COMUNICATION_SMS:
                try {
                    HelperClass.sendSMS(ViewOwnerActivity.this, owner.getPhone());
                } catch (HelperClass.EnterPhoneException ex) {
                    Snackbar.make(DoctorVetApp.getRootForSnack(this), getString(R.string.error_falta_telefono), Snackbar.LENGTH_SHORT).show();
                }
                break;
            default:
                DoctorVetApp.get().handleGeneralBottomSheetClick(buttonClicked, ViewOwnerActivity.this);
        }
    }

    @Override
    public Context getContext() {
        return ViewOwnerActivity.this;
    }

}
