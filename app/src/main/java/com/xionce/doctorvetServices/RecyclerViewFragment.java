package com.xionce.doctorvetServices;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RecyclerViewFragment extends FragmentBase {

    private static final String TAG = "RecyclerViewFragment";

    public RecyclerViewFragment() {}

    @Override
    public View provideYourFragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recyclerview, parent, false);
        swipeRefreshLayout = rootView.findViewById(R.id.swipe_container);
        recyclerView = rootView.findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        setSwipeRefreshLayout(rootView);
        setPaginationRecyclerView(recyclerView);
        return rootView;
    }

    @Override
    public void refreshView() {

    }

}
