package com.xionce.doctorvetServices;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.controls.PaginationRecyclerView;

public abstract class FragmentBase extends Fragment implements DoctorVetApp.IProgressBarActivity {

    private static final String TAG = "FragmentBase2";
    private ProgressBar mLoadingIndicator;
    protected TextView mErrorMessageDisplay;
    protected CoordinatorLayout coordinatorContainerLayout;
    private Dialog mOverlayDialog;
    protected boolean created = false;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected PaginationRecyclerView recyclerView;

    public FragmentBase() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        created = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_base, container, false);
        mLoadingIndicator = rootView.findViewById(R.id.pb_loading_indicator);
        mErrorMessageDisplay = rootView.findViewById(R.id.tv_error_message_display);
        coordinatorContainerLayout = rootView.findViewById(R.id.coordinator_container);
        View view = provideYourFragmentView(inflater, container, savedInstanceState);
        coordinatorContainerLayout.addView(view);
        return rootView;
    }

    public abstract View provideYourFragmentView(LayoutInflater inflater,ViewGroup parent, Bundle savedInstanceState);
    public abstract void refreshView();
    public void setSwipeRefreshLayout(View root_view) {
        swipeRefreshLayout = root_view.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshView();
            }
        });
    }
    public void setPaginationRecyclerView(PaginationRecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void showErrorMessage() {
        coordinatorContainerLayout.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setText(getContext().getString(R.string.error_conexion_servidor));
    }
    public void showEmptyListMessage() {
        coordinatorContainerLayout.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setText(getContext().getString(R.string.error_lista_vacia));
    }
    public void hideDisplayMessage() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }
    public void showCoordinatorView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        coordinatorContainerLayout.setVisibility(View.VISIBLE);
    }
    public void showProgressBar() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }
    public void hideProgressBar() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }
    public void showWaitDialog() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mOverlayDialog = HelperClass.getOverlayDialog(getContext());
        mOverlayDialog.show();
    }
    public void hideWaitDialog() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mOverlayDialog.dismiss();
    }
    public void hideSwipeRefreshLayoutProgressBar() {
        swipeRefreshLayout.setRefreshing(false);
    }

    public void hideRecyclerView() {
        recyclerView.setVisibility(View.INVISIBLE);
    }
    public void showRecyclerView() {
        showCoordinatorView();
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Nullable
    @Override
    public Context getContext() {
        return super.getContext();
    }

}
