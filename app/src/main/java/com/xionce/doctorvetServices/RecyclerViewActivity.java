package com.xionce.doctorvetServices;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.controls.PaginationRecyclerView;

public class RecyclerViewActivity extends AppCompatActivity {

    private static final String TAG = "RecyclerViewActivity";
    protected ImageView toolbar_image;
    protected TextView toolbar_title;
    protected TextView toolbar_subtitle;
    protected PaginationRecyclerView recyclerView;
    protected ProgressBar mLoadingIndicator;
    protected TextView mMessageDisplay;
    private Dialog mOverlayDialog;
    protected Toolbar myToolbar;
    protected SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recyclerview);
        myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar_image = findViewById(R.id.toolbar_image_thumb);
        toolbar_image.setVisibility(View.GONE);
        toolbar_title = findViewById(R.id.txt_title);
        toolbar_subtitle = findViewById(R.id.txt_sub_title);
        mMessageDisplay = findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        recyclerView =  findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        swipeRefreshLayout = findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshAdapter();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAdapter();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    protected void refreshAdapter() {}

    public void showErrorMessage() {
        recyclerView.setVisibility(View.INVISIBLE);
        mMessageDisplay.setVisibility(View.VISIBLE);
        mMessageDisplay.setText(this.getString(R.string.error_conexion_servidor));
    }
    public void showErrorToast(String errorMessage, Context ctx, String tag) {
        Snackbar.make(DoctorVetApp.getRootForSnack(this), errorMessage, Snackbar.LENGTH_LONG).show();
        Log.e(tag, errorMessage);
    }
    public void showRecyclerView() {
        mMessageDisplay.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
    }
    public void hideRecyclerView() {
        swipeRefreshLayout.setVisibility(View.VISIBLE);
    }
    public void showEmptyListMessage() {
        recyclerView.setVisibility(View.INVISIBLE);
        mMessageDisplay.setVisibility(View.VISIBLE);
        mMessageDisplay.setText(this.getString(R.string.error_lista_vacia));
    }
    public void showEmptyListMessage(String empty_text) {
        recyclerView.setVisibility(View.INVISIBLE);
        mMessageDisplay.setVisibility(View.VISIBLE);
        mMessageDisplay.setText(empty_text);
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
    public void showVolleyError(VolleyError error, String tag) {
        DoctorVetApp.get().handle_volley_error(error, /*this,*/ TAG, true);
    }
    public void hideSwipeRefreshLayoutProgressBar() {
        swipeRefreshLayout.setRefreshing(false);
    }

//    @Override
//    public void onPagination() {
//
//    }
//
//    @Nullable
//    @Override
//    public Context getContext() {
//        return getContext();
//    }

}
