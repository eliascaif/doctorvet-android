package com.xionce.doctorvetServices;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.xionce.doctorvetServices.data.Owner;
import com.xionce.doctorvetServices.data.Pet;
import com.xionce.doctorvetServices.data.Product_provider;
import com.xionce.doctorvetServices.data.User;
import com.xionce.doctorvetServices.data.UsersAdapter;
import com.xionce.doctorvetServices.data.VetsStructuresAdapter;
import com.xionce.doctorvetServices.data.Waiting_room;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;

public class EditWaitingRoomActivity extends EditBaseActivity {

    private static final String TAG = "EditWaitingRoomActivity";
    private AutoCompleteTextView actv_attended_by_user;
    private UsersAdapter usersAdapter;
    private TextInputLayout txtSite;
    private View list_item_selected_pet;
    private View list_item_selected_owner;
    private View list_item_selected_user;
    private Spinner spinnerVetStructure;
    private VetsStructuresAdapter vetsStructuresAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_waiting_room);
        toolbar_title.setText("En sala de espera");
        toolbar_subtitle.setText("Ingresando mascota en sala de espera");
        list_item_selected_pet = findViewById(R.id.list_item_pet_selector);
        list_item_selected_owner = findViewById(R.id.list_item_owner_selector);
        list_item_selected_user = findViewById(R.id.list_item_selected_user);
        spinnerVetStructure = findViewById(R.id.spinner_site);
        ImageView img_thumb_pet = list_item_selected_pet.findViewById(R.id.img_thumb);
        Glide.with(this).load(R.drawable.ic_pets_light).apply(RequestOptions.fitCenterTransform()).into(img_thumb_pet);
        hideToolbarImage();

        //Data
        initializeInitRequestNumber(1);
        DoctorVetApp.get().getVetStructuresMinAdapter(new DoctorVetApp.VolleyCallbackAdapter() {
            @Override
            public void onSuccess(RecyclerView.Adapter resultAdapter) {
                setRequestCompleted();
                if (resultAdapter != null) {
                    vetsStructuresAdapter = (VetsStructuresAdapter)resultAdapter;
                    spinnerVetStructure.setAdapter(vetsStructuresAdapter.getArrayAdapter(EditWaitingRoomActivity.this));
                }
            }
        });

        Owner owner = getOwner();
        if (owner != null)
            setOwnerInView(owner);

        Pet pet = getPet();
        if (pet != null)
            setPetInView(pet);

        User user = getUser();
        if (user != null)
            setUserInView(user);

        list_item_selected_owner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditWaitingRoomActivity.this, SearchOwnerActivity.class);
                intent.putExtra(DoctorVetApp.REQUEST_SEARCH_FOR, DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name());
                intent.putExtra(DoctorVetApp.INTENT_SEARCH_RETURN, true);
                startActivityForResult(intent, HelperClass.REQUEST_SEARCH);
            }
        });
        list_item_selected_owner.findViewById(R.id.img_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeOwnerInView();
                removePetInView();
            }
        });

        list_item_selected_pet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditWaitingRoomActivity.this, SearchPetActivity.class);
                intent.putExtra(DoctorVetApp.REQUEST_SEARCH_FOR, DoctorVetApp.INTENT_VALUES.PET_OBJ.name());
                intent.putExtra(DoctorVetApp.INTENT_SEARCH_RETURN, true);
                startActivityForResult(intent, HelperClass.REQUEST_SEARCH);
            }
        });
        list_item_selected_pet.findViewById(R.id.img_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removePetInView();
            }
        });

        list_item_selected_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditWaitingRoomActivity.this, SearchUserActivity.class);
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
   }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;

        //busquedas
        if (requestCode == HelperClass.REQUEST_SEARCH && data != null) {
            String searchFor = data.getExtras().getString(DoctorVetApp.REQUEST_SEARCH_FOR, "");
            if (searchFor.equals(DoctorVetApp.INTENT_VALUES.USER_OBJ.name())) {
                User user = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.USER_OBJ.name()), User.class);
                setUser(user);
                setUserInView(user);
            } else if (searchFor.equals(DoctorVetApp.INTENT_VALUES.PET_OBJ.name())) {
                Pet pet = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.PET_OBJ.name()), Pet.class);
                setPet(pet);
                setPetInView(pet);
            } else if (searchFor.equals(DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name())) {
                Owner owner = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name()), Owner.class);
                setOwner(owner);
                setOwnerInView(owner);
                if (owner.getPets().size() > 0) {
                    setPet(owner.getPets().get(0));
                    setPetInView(owner.getPets().get(0));
                } else {
                    setPet(null);
                    removePetInView();
                }
            }
        }
    }

    @Override
    protected void save() {
        if (!validate_selected_pet()) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), "Selecciona " + DoctorVetApp.get().getPetNaming(), Snackbar.LENGTH_LONG).show();
            return;
        }

        if (!validate_selected_owner()) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), "Selecciona " + DoctorVetApp.get().getOwnerNaming(), Snackbar.LENGTH_LONG).show();
            return;
        }

        showWaitDialog();

        Waiting_room waiting_room = getObjectFromUI();
        final String waiting_room_json_object = MySqlGson.postGson().toJson(waiting_room);

        TokenStringRequest stringRequest = new TokenStringRequest(getMethod(), getUrl().toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String success = MySqlGson.getStatusFromResponse(response);
                    if (success.equalsIgnoreCase("success"))
                        finish();
                } catch (Exception ex) {
                    DoctorVetApp.get().handle_onResponse_error(ex, /*EditWaitingRoomActivity.this,*/ TAG, true, response);
                } finally {
                    hideWaitDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DoctorVetApp.get().handle_volley_error(error, /*EditWaitingRoomActivity.this,*/ TAG, true);
                hideWaitDialog();
            }
        })
        {
            @Override
            public byte[] getBody() {
                return waiting_room_json_object.getBytes();
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
        return NetworkUtils.buildWaitingRoomsUrl(null);
    }

    @Override
    protected Waiting_room getObjectFromUI() {
        Waiting_room waiting_room = new Waiting_room();
        waiting_room.setCreated_by_id_user(DoctorVetApp.get().getUser().getId());

        if (getOwner() != null)
            waiting_room.setId_owner(getOwner().getId());

        if (getPet() != null)
            waiting_room.setId_pet(getPet().getId());

        if (getUser() != null)
            waiting_room.setPre_attended_by_id_user(getUser().getId());

        waiting_room.setSite(spinnerVetStructure.getSelectedItem().toString());

        return waiting_room;
    }

    @Override
    protected Product_provider getObject() {
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

    private Owner getOwner() /*throws Exception*/ {
        Intent intent = getIntent();
        Owner owner = null;
        if (intent.hasExtra(DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name()))
            owner = MySqlGson.getGson().fromJson(intent.getStringExtra(DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name()), Owner.class);

        return owner;
    }
    private void setOwner(Owner owner) {
        Intent intent = getIntent();
        if (owner == null)
            intent.removeExtra(DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name());
        else
            intent.putExtra(DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name(), MySqlGson.getGson().toJson(owner));
    }

    private User getUser() {
        Intent intent = getIntent();
        User user = null;
        if (intent.hasExtra(DoctorVetApp.INTENT_VALUES.USER_OBJ.name()))
            user = MySqlGson.getGson().fromJson(intent.getStringExtra(DoctorVetApp.INTENT_VALUES.USER_OBJ.name()), User.class);

        return user;
    }
    private void setUser(User user) {
        Intent intent = getIntent();
        if (user == null)
            intent.removeExtra(DoctorVetApp.INTENT_VALUES.USER_OBJ.name());
        else
            intent.putExtra(DoctorVetApp.INTENT_VALUES.USER_OBJ.name(), MySqlGson.getGson().toJson(user));
    }
    private Pet getPet() {
        Intent intent = getIntent();
        Pet pet = null;
        if (intent.hasExtra(DoctorVetApp.INTENT_VALUES.PET_OBJ.name())) {
            pet = MySqlGson.getGson().fromJson(intent.getStringExtra(DoctorVetApp.INTENT_VALUES.PET_OBJ.name()), Pet.class);
        }

        return pet;
    }
    private void setPet(Pet pet) {
        Intent intent = getIntent();
        if (pet == null)
            intent.removeExtra(DoctorVetApp.INTENT_VALUES.PET_OBJ.name());
        else
            intent.putExtra(DoctorVetApp.INTENT_VALUES.PET_OBJ.name(), MySqlGson.getGson().toJson(pet));
    }

    private void setOwnerInView(Owner owner) {
        ImageView img_thumb = list_item_selected_owner.findViewById(R.id.img_thumb);
        TextView txt_name = list_item_selected_owner.findViewById(R.id.txt_item_name);

//        Context ctx = img_thumb.getContext();
//        String thumb_url = owner.getThumb_url();
//        if (thumb_url != null) {
//            Glide.with(ctx).load(thumb_url).apply(RequestOptions.fitCenterTransform()).apply(RequestOptions.circleCropTransform()).into(img_thumb);
//        } else {
//            Glide.with(ctx).load(R.drawable.ic_account_circle_light).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
//        }
        DoctorVetApp.get().setThumb(owner.getThumb_url(), img_thumb, R.drawable.ic_account_circle_light);

        txt_name.setText(owner.getName());
    }
    private void removeOwnerInView() {
        setOwner(null);
        ImageView img_thumb = list_item_selected_owner.findViewById(R.id.img_thumb);
        TextView txt_name = list_item_selected_owner.findViewById(R.id.txt_item_name);
        Context ctx = img_thumb.getContext();
        Glide.with(ctx).load(R.drawable.ic_account_circle_light).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
        txt_name.setText("SELECCIONAR");
    }

    private void setPetInView(Pet pet) {
        ImageView img_thumb = list_item_selected_pet.findViewById(R.id.img_thumb);
        TextView txt_name = list_item_selected_pet.findViewById(R.id.txt_item_name);
        DoctorVetApp.get().setThumb(pet.getThumb_url(), img_thumb, R.drawable.ic_pets_light);

        txt_name.setText(pet.getName());
    }
    private void removePetInView() {
        setPet(null);
        ImageView img_thumb = list_item_selected_pet.findViewById(R.id.img_thumb);
        TextView txt_name = list_item_selected_pet.findViewById(R.id.txt_item_name);
        Context ctx = img_thumb.getContext();
        Glide.with(ctx).load(R.drawable.ic_pets_light).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
        txt_name.setText("SELECCIONAR");
    }
    private void setUserInView(User user) {
        ImageView img_thumb = list_item_selected_user.findViewById(R.id.img_thumb);
        TextView txt_name = list_item_selected_user.findViewById(R.id.txt_item_name);

//        Context ctx = img_thumb.getContext();
//        String thumb_url = user.getThumb_url();
//        if (thumb_url != null) {
//            Glide.with(ctx).load(thumb_url).apply(RequestOptions.fitCenterTransform()).apply(RequestOptions.circleCropTransform()).into(img_thumb);
//        } else {
//            Glide.with(ctx).load(R.drawable.ic_account_circle_light).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
//        }
        DoctorVetApp.get().setThumb(user.getThumb_url(), img_thumb, R.drawable.ic_account_circle_light);

        txt_name.setText(user.getName());
    }
    private void removeUserInView() {
        setUser(null);
        ImageView img_thumb = list_item_selected_user.findViewById(R.id.img_thumb);
        TextView txt_name = list_item_selected_user.findViewById(R.id.txt_item_name);
        Context ctx = img_thumb.getContext();
        Glide.with(ctx).load(R.drawable.ic_account_circle_light).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
        txt_name.setText("SELECCIONAR");
    }

    private boolean validate_selected_pet() {
        if (getPet() == null)
            return false;

        return true;
    }
    private boolean validate_selected_owner() {
        if (getOwner() == null)
            return false;

        return true;
    }

}
