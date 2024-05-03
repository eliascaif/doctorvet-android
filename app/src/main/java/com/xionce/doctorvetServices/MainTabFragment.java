package com.xionce.doctorvetServices;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public abstract class MainTabFragment extends FragmentBase {

    private static final String TAG = "MainTabFragment";
    //protected PaginationRecyclerView recyclerView;
    //protected SwipeRefreshLayout swipeRefreshLayout;

    public MainTabFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View provideYourFragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.fragment_main_tab, parent, false);
        swipeRefreshLayout = root_view.findViewById(R.id.swipe_container);
        recyclerView = root_view.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        setSwipeRefreshLayout(root_view);
        return root_view;
    }

    public void hideRecyclerView() {
        swipeRefreshLayout.setVisibility(View.INVISIBLE);
    }
    public void showRecyclerView() {
        coordinatorContainerLayout.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
    }

}
