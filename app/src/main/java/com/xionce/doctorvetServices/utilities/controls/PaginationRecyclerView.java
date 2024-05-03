package com.xionce.doctorvetServices.utilities.controls;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xionce.doctorvetServices.utilities.HelperClass;

public class PaginationRecyclerView extends RecyclerView {

    private Integer total_pages = 1;
    private Integer page = 1;
    private boolean isLoading = false;

    private HelperClass.RecyclerOnPaginationHandler onPaginationHandler;

    public PaginationRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        addScrollListener();
    }

    public PaginationRecyclerView(@NonNull Context context) {
        super(context);
        addScrollListener();
    }

    private void addScrollListener() {
        this.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    LayoutManager lyt = getLayoutManager();
                    Integer totalItemCount = lyt.getItemCount();
                    Integer lastVisibleItem = ((LinearLayoutManager)lyt).findLastVisibleItemPosition();

                    if (lastVisibleItem == (totalItemCount - 1) && !isLoading) {
                        isLoading = true;
                        doPagination();
                    }
                }
            }
        });
    }
    public Integer getPage() {
        return page;
    }

    public void resetPage() {
        this.page = 1;
    }
    public void addPage() {
        this.page++;
    }

    public Integer getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(Integer total_pages) {
        this.total_pages = total_pages;
    }

    protected void doPagination() {
        if (onPaginationHandler != null)
            onPaginationHandler.onPagination();
    }

    public void finishLoading() {
        isLoading = false;
    }

    public void startLoading() {
        isLoading = true;
    }

    public void setOnPaginationHandler(HelperClass.RecyclerOnPaginationHandler onPaginationHandler) {
        this.onPaginationHandler = onPaginationHandler;
    }

}
