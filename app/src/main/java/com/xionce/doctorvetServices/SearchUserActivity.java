package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;
import com.xionce.doctorvetServices.data.Get_pagination;
import com.xionce.doctorvetServices.data.User;
import com.xionce.doctorvetServices.data.UsersAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;

import java.util.ArrayList;

public class SearchUserActivity extends SearchActivityBase implements HelperClass.AdapterOnClickHandler {

    private static final String TAG = "SearchUserActivity";
    private UsersAdapter usersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextInputLayout txtBuscar = findViewById(R.id.txt_search);
        txtBuscar.setHint(getString(R.string.user_search));
        search(true);
        setRecyclerView();
    }

    @Override
    public void search(boolean on_first_fill) {
        if (!on_first_fill && !validateEmptySearch())
            return;

        resetPage();
        showProgressBar();
        hideFailSearch();
        DoctorVetApp.get().getUsersPagination(getSearchText(), getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();
                //hideStartSearch();
                if (pagination != null) {
                    setTotal_pages(pagination.getTotal_pages());

                    ArrayList<User> users = ((User.Get_pagination_users)pagination).getContent();
                    usersAdapter = new UsersAdapter(users, UsersAdapter.Adapter_types.SHOW);
                    usersAdapter.setOnClickHandler(SearchUserActivity.this);
                    recyclerView.setAdapter(usersAdapter);

                    manageShowRecyclerView(usersAdapter, on_first_fill);

                    showSoftKeyboard();
                } else {
                    DoctorVetApp.get().handle_null_adapter("Owners", /*SearchUserActivity.this,*/ TAG, true);
                    showErrorMessage();
                }
            }
        });
    }

    @Override
    public void doPagination() {
        isLoading = true;
        showProgressBar();
        addPage();
        DoctorVetApp.get().getUsersPagination(getSearchText(), getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();
                if (pagination != null) {
                    ArrayList<User> users = ((User.Get_pagination_users)pagination).getContent();
                    usersAdapter.addItems(users);
                    isLoading = false;
                } else {
                    DoctorVetApp.get().handle_null_adapter("users", /*SearchUserActivity.this,*/ TAG, true);
                    showErrorMessage();
                }
            }
        });
    }

    @Override
    public void onClick(Object data, View view, int pos) {
        User user = (User) data;
        Intent intent = getIntent();

        if (intent.getBooleanExtra(DoctorVetApp.INTENT_SEARCH_VIEW, false)) {
            Intent activity = new Intent(SearchUserActivity.this, ViewUserActivity.class);
            activity.putExtra(DoctorVetApp.INTENT_VALUES.USER_ID.name(), user.getId());
            activity.putExtra(DoctorVetApp.INTENT_UPDATE_LAST_VIEW, 1);
            startActivity(activity);
        } else if (intent.getBooleanExtra(DoctorVetApp.INTENT_SEARCH_RETURN, false)) {
            Intent dataBackIntent = getIntent();
            dataBackIntent.putExtra(DoctorVetApp.INTENT_VALUES.USER_OBJ.name(), MySqlGson.getGson().toJson(user));
            this.setResult(RESULT_OK, dataBackIntent);
        }

        finish();
    }

}