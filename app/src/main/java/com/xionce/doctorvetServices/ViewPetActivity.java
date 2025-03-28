package com.xionce.doctorvetServices;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xionce.doctorvetServices.data.Pet;
import com.xionce.doctorvetServices.data.Owner;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;

//No hereda de viewBase, se complico con los tabs
public class ViewPetActivity extends AppCompatActivity
        implements DoctorVetApp.PlayableContent, BottomSheetDialog.BottomSheetListener2 {

    private static final String TAG = "ViewPetActivity";
    public Toolbar toolbar;
    public TextView toolbar_title;
    public ImageView toolbar_image;
    public TextView toolbar_subtitle;
    public TabLayout tabLayout;
    private MediaPlayer mediaPlayer;
    private ProgressBar mLoadingIndicator;
    private Pet pet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pet);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar_image = findViewById(R.id.toolbar_image_thumb);
        toolbar_title = findViewById(R.id.txt_title);
        toolbar_subtitle = findViewById(R.id.txt_sub_title);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        DoctorVetApp.views_abiertos++;

        FloatingActionButton fab = findViewById(R.id.main_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ViewPetActivity.this, "ViewPetActivity");
                bottomSheetDialog.show(getSupportFragmentManager(), null);
            }
        });

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        if (savedInstanceState != null) {
            Pet pet = MySqlGson.getGson().fromJson(savedInstanceState.getString("pet"), Pet.class);
            setPet(pet);
            setTitles(pet);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("pet", MySqlGson.getGson().toJson(getPet()));
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        intent.putExtra(HelperClass.INTENT_EXTRA_REQUEST_CODE, requestCode);
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (getIdPet() == null && (id == R.id.action_edit || id == R.id.action_delete)) {
            Snackbar.make(tabLayout, R.string.error_cargando_registro, Snackbar.LENGTH_SHORT).show();
            return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        killMediaPlayer();
        DoctorVetApp.views_abiertos--;
    }

    @Override
    public void killMediaPlayer() {
        if(mediaPlayer!=null) {
            try {
                mediaPlayer.release();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public MediaPlayer getMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        return mediaPlayer;
    }

    @Override
    public void onButtonClicked(BottomSheetDialog.Buttons buttonClicked) {
        if (getPet() == null) {
            Snackbar.make(tabLayout, R.string.error_cargando_registro, Snackbar.LENGTH_SHORT).show();
            return;
        }
        Intent intent;
        switch (buttonClicked) {
            case PET_UPDATE:
                update_pet();
                break;
            case PET_DELETE:
                delete_pet();
                break;
            case PETS_NEW_CLINIC:
                intent = new Intent(ViewPetActivity.this, EditClinicActivity.class);
                intent.putExtra(DoctorVetApp.INTENT_VALUES.PET_OBJ.name(), MySqlGson.getGson().toJson(getPet().getPolish()));
                startActivity(intent);
                break;
            case PETS_NEW_CLINIC_2:
                intent = new Intent(ViewPetActivity.this, EditClinic2Activity.class);
                intent.putExtra(DoctorVetApp.INTENT_VALUES.PET_OBJ.name(), MySqlGson.getGson().toJson(getPet().getPolish()));
                startActivity(intent);
                break;
            case PETS_NEW_SUPPLY:
                intent = new Intent(ViewPetActivity.this, EditSupplyActivity_YesNo.class);
                intent.putExtra(DoctorVetApp.INTENT_VALUES.PET_OBJ.name(), MySqlGson.getGson().toJson(getPet().getPolish()));
                startActivity(intent);
                break;
            case PETS_NEW_STUDY:
                intent = new Intent(ViewPetActivity.this, EditStudyActivity_1.class);
                intent.putExtra(DoctorVetApp.INTENT_VALUES.PET_OBJ.name(), MySqlGson.getGson().toJson(getPet().getPolish()));
                startActivity(intent);
                break;
            case PETS_NEW_RECIPE:
                intent = new Intent(ViewPetActivity.this, EditRecipeActivity_1.class);
                intent.putExtra(DoctorVetApp.INTENT_VALUES.PET_OBJ.name(), MySqlGson.getGson().toJson(getPet().getPolish()));
                startActivity(intent);
                break;
            case PETS_NEW_AGENDA:
                intent = new Intent(ViewPetActivity.this, EditAgendaActivity.class);
                intent.putExtra(DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name(), MySqlGson.getGson().toJson(getPet().getFirstPrincipalOwner().getPolish()));
                intent.putExtra(DoctorVetApp.INTENT_VALUES.PET_OBJ.name(), MySqlGson.getGson().toJson(getPet().getPolish()));
                startActivity(intent);
                break;
            case SELLS_TO_PET_NEW:
                intent = new Intent(ViewPetActivity.this, EditSellActivity_1.class);
                intent.putExtra(DoctorVetApp.INTENT_VALUES.PET_OBJ.name(), MySqlGson.getGson().toJson(getPet().getPolish()));
                intent.putExtra(DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name(), MySqlGson.getGson().toJson(getPet().getFirstPrincipalOwner().getPolish()));
                startActivity(intent);
                break;
            case WAITING_ROOM_PET:
                intent = new Intent(ViewPetActivity.this, EditWaitingRoomActivity.class);
                intent.putExtra(DoctorVetApp.INTENT_VALUES.PET_OBJ.name(), MySqlGson.getGson().toJson(getPet().getPolish()));
                intent.putExtra(DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name(), MySqlGson.getGson().toJson(getPet().getFirstPrincipalOwner().getPolish()));
                intent.putExtra(DoctorVetApp.INTENT_VALUES.USER_OBJ.name(), MySqlGson.getGson().toJson(DoctorVetApp.get().getUser()));
                startActivity(intent);
                break;
            default:
                DoctorVetApp.get().handleGeneralBottomSheetClick(buttonClicked, ViewPetActivity.this);
        }
    }

    public Integer getIdPet() {
        android.util.Log.w("PETID", "showNotificationExecuted PETID:" + String.valueOf(getIntent().getIntExtra(DoctorVetApp.INTENT_VALUES.PET_ID.name(), 0)));
        return getIntent().getIntExtra(DoctorVetApp.INTENT_VALUES.PET_ID.name(), 0);
    }
    public Pet getPet() {
        return this.pet;
    }
    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public void update_pet() {
        final Pet pet_editar = getPet();
        Integer id_owner = pet_editar.getOwners().get(0).getId();
        DoctorVetApp.get().getOwner(id_owner, 0, new DoctorVetApp.VolleyCallbackOwner() {
            @Override
            public void onSuccess(Owner resultOwner) {
                if (resultOwner != null) {
                    Intent intent = new Intent(ViewPetActivity.this, EditPetActivity.class);
                    intent.putExtra(DoctorVetApp.INTENT_VALUES.PET_OBJ.name(), MySqlGson.getGson().toJson(pet_editar));
                    intent.putExtra(DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name(), MySqlGson.getGson().toJson(resultOwner));
                    startActivityForResult(intent, HelperClass.REQUEST_UPDATE);
                } else {
                    Snackbar.make(tabLayout, R.string.error_update_pet, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
    public void delete_pet() {
        HelperClass.getOKCancelDialog(ViewPetActivity.this, getString(R.string.action_delete_pregunta), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mLoadingIndicator.setVisibility(View.VISIBLE);
                final Integer id_pet = getIdPet();
                URL delete_ownerUrl = NetworkUtils.buildDeleteMascotaUrl(id_pet);
                TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.DELETE, delete_ownerUrl.toString(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    String success = MySqlGson.getStatusFromResponse(response);
                                    if (success.equalsIgnoreCase("success")) {
                                        finish();
                                    } else {
                                        DoctorVetApp.get().handle_error(new Exception(getString(R.string.error_borrando_registro)), TAG, true);
                                    }
                                } catch (Exception ex) {
                                    DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                                } finally {
                                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                DoctorVetApp.get().handle_volley_error(error, TAG, true);
                                mLoadingIndicator.setVisibility(View.INVISIBLE);
                            }
                        }
                );
                DoctorVetApp.get().addToRequestQueque(stringRequest);
            }
        });
    }
    public void setTitles(Pet pet) {
        String title = pet.getName();
        if (pet.getDeath() == 1)
            title += " (Deceso)";
        String subTitle = "De: " + pet.getOwnersNames();

        toolbar_title.setText(title);
        toolbar_subtitle.setText(subTitle);

        String thumb_url = pet.getThumb_url();
        if (thumb_url != null) {
            //on device search first. thumb
            String str_thumb_file_name = DoctorVetApp.get().getLocalFromUrl(thumb_url);
            if (HelperClass.fileExists(str_thumb_file_name))
                thumb_url = str_thumb_file_name;

            //on device search first. photo
            String photo_url = pet.getPhoto_url();
            String str_photo_file_name = DoctorVetApp.get().getLocalFromUrl(photo_url);
            if (HelperClass.fileExists(str_photo_file_name))
                photo_url = str_photo_file_name;

            Glide.with(getApplicationContext()).load(thumb_url).apply(RequestOptions.fitCenterTransform()).apply(RequestOptions.circleCropTransform()).into(toolbar_image);

            String finalPhoto_url = photo_url;
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent activity = new Intent(ViewPetActivity.this, ViewFullScreenPhoto.class);
                    activity.putExtra(HelperClass.INTENT_IMAGE_URL, finalPhoto_url);
                    startActivity(activity);
                }
            });
        } else {
            Glide.with(getApplicationContext()).load(R.drawable.ic_pets_dark).apply(RequestOptions.fitCenterTransform()).into(toolbar_image);
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new ViewPetTabInfo();
                case 1:
                    return new ViewPetTabClinic();
                case 2:
                    return new ViewPetTabSupply();
                default:
                    throw new RuntimeException("Fragment not exists");
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.pet);
                case 1:
                    return getString(R.string.clinica);
                case 2:
                    return getString(R.string.supply);
            }
            return null;
        }

    }

}
