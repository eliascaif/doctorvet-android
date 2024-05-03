package com.xionce.doctorvetServices;

import android.app.Dialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.xionce.doctorvetServices.utilities.HelperClass;

import static com.xionce.doctorvetServices.utilities.HelperClass.REQUEST_CALL;

public abstract class ViewBaseActivity extends AppCompatActivity {

    private static final String TAG = "ViewBaseActivity";
    protected Toolbar toolbar;
    protected ImageView toolbar_image;
    protected TextView toolbar_title;
    protected TextView toolbar_subtitle;
    private TextView mMessageDisplay;
    private ProgressBar mLoadingIndicator;
    protected FrameLayout activityContainer;
    private Dialog mOverlayDialog;
    protected boolean loadedFinished;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected FloatingActionButton fab;

    private String phone;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_view_base);
        DoctorVetApp.views_abiertos++;
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DoctorVetApp.views_abiertos--;
    }

    @Override
    public void setContentView(int layoutResID) {
        LayoutInflater layoutInflater = getLayoutInflater();
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) layoutInflater.inflate(R.layout.activity_view_base, null);
        activityContainer = coordinatorLayout.findViewById(R.id.container_frame);
        toolbar = coordinatorLayout.findViewById(R.id.toolbar);
        toolbar_image = coordinatorLayout.findViewById(R.id.toolbar_image_thumb);
        toolbar_title = coordinatorLayout.findViewById(R.id.txt_title);
        toolbar_subtitle = coordinatorLayout.findViewById(R.id.txt_sub_title);
        mLoadingIndicator = coordinatorLayout.findViewById(R.id.pb_loading_indicator);
        mMessageDisplay = coordinatorLayout.findViewById(R.id.tv_error_message_display);

        layoutInflater.inflate(layoutResID, activityContainer, true);
        super.setContentView(coordinatorLayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab = coordinatorLayout.findViewById(R.id.main_fab);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (!loadedFinished) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), R.string.error_cargando_registro, Snackbar.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        intent.putExtra(HelperClass.INTENT_EXTRA_REQUEST_CODE, requestCode);
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendCall(this.phone);
            } else {
                Snackbar.make(DoctorVetApp.getRootForSnack(this), getString(R.string.error_falta_permiso), Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        //updates
        if (requestCode == HelperClass.REQUEST_UPDATE) {
            on_update_complete(data);
        }
    }

    protected abstract void refreshView();
    protected void setSwipeRefreshLayout() {
        swipeRefreshLayout = findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshView();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    protected abstract void go_update();
    protected abstract void go_delete();
    protected abstract void on_update_complete(Intent data);
    protected abstract void on_delete_complete(Integer deleted_id);

    protected abstract void setUI(Object object);

    protected void setLoadedFinished() {
        loadedFinished = true;
    }
    public void setPhoto(String thumb_url, String photo_url) {
        if (thumb_url != null) {
            //on device search first. thumb
            String str_thumb_file_name = DoctorVetApp.get().getLocalFromUrl(thumb_url);
            if (HelperClass.fileExists(str_thumb_file_name))
                thumb_url = str_thumb_file_name;

            //on device search first. photo
            String str_photo_file_name = DoctorVetApp.get().getLocalFromUrl(photo_url);
            if (HelperClass.fileExists(str_photo_file_name))
                photo_url = str_photo_file_name;

            Glide.with(getApplicationContext()).load(thumb_url).apply(RequestOptions.fitCenterTransform()).apply(RequestOptions.circleCropTransform()).into(toolbar_image);
            if (photo_url != null) {
                String finalPhoto_url = photo_url;
                toolbar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent activity = new Intent(ViewBaseActivity.this, ViewFullScreenPhoto.class);
                        activity.putExtra(HelperClass.INTENT_IMAGE_URL, finalPhoto_url);
                        startActivity(activity);
                    }
                });
            }
        }
    }
    public void setPhone(String phone) {
        if (phone != null)
            this.phone = phone;
    }

    public void hideActivityContainer() {
        activityContainer.setVisibility(View.GONE);
    }
    public void showActivityContainer() {
        activityContainer.setVisibility(View.VISIBLE);
    }
    public void showErrorMessage() {
        activityContainer.setVisibility(View.GONE);
        mMessageDisplay.setVisibility(View.VISIBLE);
        mMessageDisplay.setText(this.getString(R.string.error_conexion_servidor));
    }
    public void showProgressBar() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    public void hideProgressBar() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    public void showWaitDialog() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mOverlayDialog = HelperClass.getOverlayDialog(this);
        mOverlayDialog.show();
    }
    public void hideWaitDialog() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mOverlayDialog.dismiss();
    }
    public void showErrorToast(String error_message, String TAG) {
        Snackbar.make(DoctorVetApp.getRootForSnack(this), error_message, Snackbar.LENGTH_SHORT).show();
        Log.e(TAG, error_message);
    }
    public void hideToolbarImage() {
        toolbar_image.setVisibility(View.GONE);
    }
    public void hideSubtitle() {
        toolbar_subtitle.setVisibility(View.GONE);
    }

    public void hideSwipeRefreshLayoutProgressBar() {
        swipeRefreshLayout.setRefreshing(false);
    }
    public void hideFab() {
        fab.setVisibility(View.GONE);
    }
    public void showFab() {
        fab.setVisibility(View.VISIBLE);
    }

    protected abstract void invisibilizeEmptyViews();

    public void sendWhatsAppMessage(String phone) {
        try {
            HelperClass.sendWhatsAppMessage(ViewBaseActivity.this, phone, "");
        } catch (HelperClass.NoWhatsAppException ex) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), getString(R.string.error_falta_whatsapp), Snackbar.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), getString(R.string.error_falta_telefono), Snackbar.LENGTH_SHORT).show();
        }
    }

    public void sendCall(String phone) {
        try {
            HelperClass.makePhoneCall(ViewBaseActivity.this, phone);
        } catch (Exception ex) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), getString(R.string.error_falta_telefono), Snackbar.LENGTH_SHORT).show();
        }
    }
}
