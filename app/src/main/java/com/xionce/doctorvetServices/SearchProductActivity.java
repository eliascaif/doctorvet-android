package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.xionce.doctorvetServices.data.Get_pagination;
import com.xionce.doctorvetServices.data.Product;
import com.xionce.doctorvetServices.data.ProductsAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;

import java.util.ArrayList;

public class SearchProductActivity extends AppCompatActivity implements HelperClass.AdapterOnClickHandler {

    private static final String TAG = "SearchProductActivity";
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageDisplay;
    protected TextInputLayout txtSearch;
//    protected Spinner cmbFiltro;
    protected RecyclerView recyclerView;
    private ConstraintLayout constraint_search_info;
    private TextView txt_info;
    private LinearLayout bottom_bar;
    protected TextView txt_sugiere_uno;
    protected LinearLayoutManager layoutManager;
    private ImageView img_search;
    protected TextView txtCreateElement;

    private SwitchCompat switch_all_database;

    private ProductsAdapter productsAdapter;

    //pagination
    private Integer total_pages = 1;
    private Integer page = 1;

    protected boolean isLoading = false;
    //protected Integer lastVisibleItem, totalItemCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_products);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);
        bottom_bar = findViewById(R.id.bottom_bar);
        txt_sugiere_uno = findViewById(R.id.txt_suguiere_uno);
        txtSearch = findViewById(R.id.txt_search);
        img_search = findViewById(R.id.img_search);
//        cmbFiltro = findViewById(R.id.cmb_filtro);
        constraint_search_info = findViewById(R.id.constraint_search_info);
        txt_info = findViewById(R.id.txt_info);
        switch_all_database = findViewById(R.id.switch_all_database);
        recyclerView = findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        txtCreateElement = findViewById(R.id.btn_create_element);
        hideProgressBar();

        img_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search(false);
            }
        });

        txtSearch.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() != 0) {
                    //hideRecyclerView();
                    //showStartSearch();
                    hideBottomBar();
                }

                //You need to remove this to run only once
//                handler.removeCallbacks(input_finish_checker);
            }

            @Override
            public void afterTextChanged(Editable editable) {
//                if (editable.length() > 0) {
//                    last_text_edit = System.currentTimeMillis();
//                    handler.postDelayed(input_finish_checker, delay);
//                }
            }
        });

        txtSearch.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search(false);
                    return true;
                }
                return false;
            }
        });

        switch_all_database.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                search(true);
            }
        });

        ImageView productoSearchBarcode = findViewById(R.id.img_search_barcode);
        productoSearchBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchProductActivity.this, ScannerActivity.class);
                startActivityForResult(intent, HelperClass.REQUEST_READ_BARCODE);
            }
        });

        txtSearch.setHint(getString(R.string.buscar_product_servicios));
        search(true);
        setRecyclerView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        //barcodescanner
        if (requestCode == HelperClass.REQUEST_READ_BARCODE && data != null && productsAdapter != null) {
            String barcode = data.getStringExtra(HelperClass.INTENT_EXTRA_BARCODE);
            showProgressBar();
            DoctorVetApp.get().getProductByBarcode(barcode, new DoctorVetApp.VolleyCallbackObject() {
                @Override
                public void onSuccess(Object resultObject) {
                    if (resultObject != null) {
                        Product product = (Product)resultObject;
                        Intent activity = new Intent(SearchProductActivity.this, ViewProductActivity.class);
                        activity.putExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_ID.name(), product.getId());
                        startActivity(activity);
                        finish();
                    } else {
                        Snackbar.make(DoctorVetApp.getRootForSnack(SearchProductActivity.this), "Producto no encontrado", Snackbar.LENGTH_SHORT).show();
                    }
                    hideProgressBar();
                }
            });
            return;
        }
    }

    public void hideSearch() {
        img_search.setVisibility(View.GONE);
        txtSearch.setVisibility(View.GONE);
    }
    public void showErrorMessage() {
        txtSearch.setVisibility(View.GONE);
        //cmbFiltro.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setText(this.getString(R.string.error_conexion_servidor));
    }
    public void showProgressBar() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    public void hideProgressBar() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    //    public void showCmbFiltro() {
//        cmbFiltro.setVisibility(View.VISIBLE);
//    }
//    public void hideCmbFiltro() {
//        cmbFiltro.setVisibility(View.GONE);
//    }
    public void showBottomBar() {
        bottom_bar.setVisibility(View.VISIBLE);
    }
    public void hideBottomBar() {
        bottom_bar.setVisibility(View.GONE);
    }
    public void showRecyclerView() {
        recyclerView.setVisibility(View.VISIBLE);
    }
    public void hideRecyclerView() {
        recyclerView.setVisibility(View.GONE);
    }
    public void showFailSearch() {
        constraint_search_info.setVisibility(View.VISIBLE);
        txt_info.setText(getString(R.string.fail_search));
    }
    public void hideFailSearch() {
        constraint_search_info.setVisibility(View.GONE);
    }
    public void showStartSearch() {
        constraint_search_info.setVisibility(View.VISIBLE);
        txt_info.setText(getString(R.string.start_search));
    }
    public void hideStartSearch() {
        constraint_search_info.setVisibility(View.GONE);
    }

    public void search(boolean on_first_fill) {
        if (!on_first_fill && !validateEmptySearch())
            return;

        resetPage();
        showProgressBar();
        hideFailSearch();

        if (switch_all_database.isChecked()) {
            DoctorVetApp.get().getProductsVetIncludeGlobalsPagination(getSearchText(), getPage(), new DoctorVetApp.VolleyCallbackPagination() {
                @Override
                public void onSuccess(Get_pagination pagination) {
                    hideProgressBar();
                    ////hideStartSearch();
                    if (pagination != null) {
                        setTotal_pages(pagination.getTotal_pages());

                        ArrayList<Product> products = ((Product.Get_pagination_products)pagination).getContent();
                        productsAdapter = new ProductsAdapter(products, ProductsAdapter.Products_types.SEARCH);
                        productsAdapter.setOnClickHandler(SearchProductActivity.this);
                        recyclerView.setAdapter(productsAdapter);

                        manageShowRecyclerView(productsAdapter, on_first_fill);

                        manageShowCreateElement(productsAdapter, "crear producto", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(SearchProductActivity.this, EditProductActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });

                        showSoftKeyboard();
                    } else {
                        DoctorVetApp.get().handle_null_adapter("Products", TAG, true);
                        showErrorMessage();
                    }
                }
            });
        } else {
            DoctorVetApp.get().getProductsVetPagination(getSearchText(), getPage(), new DoctorVetApp.VolleyCallbackPagination() {
                @Override
                public void onSuccess(Get_pagination pagination) {
                    hideProgressBar();
                    //hideStartSearch();
                    if (pagination != null) {
                        setTotal_pages(pagination.getTotal_pages());

                        ArrayList<Product> products = ((Product.Get_pagination_products)pagination).getContent();
                        productsAdapter = new ProductsAdapter(products, ProductsAdapter.Products_types.SEARCH);
                        productsAdapter.setOnClickHandler(SearchProductActivity.this);
                        recyclerView.setAdapter(productsAdapter);

                        manageShowRecyclerView(productsAdapter, on_first_fill);

                        showSoftKeyboard();
                    } else {
                        DoctorVetApp.get().handle_null_adapter("Products", TAG, true);
                        showErrorMessage();
                    }
                }
            });
        }
    }

    protected void manageShowCreateElement(RecyclerView.Adapter adapter, String textToDisplay, View.OnClickListener clickListener) {
        if (getIntent().getBooleanExtra(DoctorVetApp.INTENT_SEARCH_RETURN, false))
            return;

        if (!DoctorVetApp.get().userIsAdmin())
            return;

        if (adapter.getItemCount() == 0) {
            showCreateElement(textToDisplay);
            txtCreateElement.setOnClickListener(clickListener);
        } else {
            hideCreateElement("");
        }
    }
    public void showCreateElement(String textToDisplay) {
        txtCreateElement.setVisibility(View.VISIBLE);
        txtCreateElement.setText(textToDisplay);
    }
    public void hideCreateElement(String textToDisplay) {
        txtCreateElement.setVisibility(View.GONE);
    }

    public boolean validateEmptySearch() {
        return HelperClass.validateEmpty(txtSearch);
    }

    public String getSearchText() {
        return txtSearch.getEditText().getText().toString();
    }

    public void setSugiereUnoListener(Class gotoActivity) {
        txt_sugiere_uno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activity = new Intent(SearchProductActivity.this, gotoActivity);
                startActivity(activity);
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

    protected void manageShowRecyclerView(RecyclerView.Adapter adapter) {
        if (adapter.getItemCount() > 0) {
            showRecyclerView();
        } else {
            showFailSearch();
        }
    }
    protected void manageShowRecyclerView(RecyclerView.Adapter adapter, boolean on_first_fill) {
        if (adapter.getItemCount() > 0) {
            showRecyclerView();
        } else {
            if (on_first_fill)
                showEmptyRecordSet();
            else
                showFailSearch();
        }
    }

    protected void showEmptyRecordSet() {
        constraint_search_info.setVisibility(View.VISIBLE);
        txt_info.setText(getString(R.string.empty_recordset));
    }

//    protected abstract void fillFirstPage_onCreate();

    protected void setRecyclerView() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    Integer totalItemCount = layoutManager.getItemCount();
                    Integer lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                    if (lastVisibleItem == (totalItemCount - 1) && !isLoading) {
                        isLoading = true;
                        doPagination();
                    }
                }
            }
        });

    }

    public void doPagination() {
        isLoading = true;
        showProgressBar();
        addPage();

        if (switch_all_database.isChecked()) {
            DoctorVetApp.get().getProductsVetIncludeGlobalsPagination(getSearchText(), getPage(), new DoctorVetApp.VolleyCallbackPagination() {
                @Override
                public void onSuccess(Get_pagination pagination) {
                    hideProgressBar();
                    if (pagination != null) {
                        ArrayList<Product> products = ((Product.Get_pagination_products) pagination).getContent();
                        productsAdapter.addItems(products);
                        isLoading = false;
                    } else {
                        DoctorVetApp.get().handle_null_adapter("Products", TAG, true);
                        showErrorMessage();
                    }
                }
            });
        } else {
            DoctorVetApp.get().getProductsVetPagination(getSearchText(), getPage(), new DoctorVetApp.VolleyCallbackPagination() {
                @Override
                public void onSuccess(Get_pagination pagination) {
                    hideProgressBar();
                    if (pagination != null) {
                        ArrayList<Product> products = ((Product.Get_pagination_products) pagination).getContent();
                        productsAdapter.addItems(products);
                        isLoading = false;
                    } else {
                        DoctorVetApp.get().handle_null_adapter("Products", TAG, true);
                        showErrorMessage();
                    }
                }
            });
        }
    }

    @Override
    public void onClick(Object data, View view, int pos) {
        Product product = (Product) data;
        Intent intent = getIntent();

        if (intent.getBooleanExtra(DoctorVetApp.INTENT_SEARCH_VIEW, false)) {
            Intent activity = new Intent(SearchProductActivity.this, ViewProductActivity.class);
            activity.putExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_ID.name(), product.getId());
            startActivity(activity);
        } else if (intent.getBooleanExtra(DoctorVetApp.INTENT_SEARCH_RETURN, false)) {
            Intent dataBackIntent = getIntent();
            dataBackIntent.putExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_OBJ.name(), MySqlGson.getGson().toJson(product));
            this.setResult(RESULT_OK, dataBackIntent);
        }

        finish();
    }

    protected void showSoftKeyboard() {
        if (!DoctorVetApp.get().isSoftKeyboardVisible(txtSearch)) {
            DoctorVetApp.get().showKeyboard();
            txtSearch.requestFocus();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (DoctorVetApp.get().isSoftKeyboardVisible(txtSearch))
            DoctorVetApp.get().closeKeyboard();
    }


//    long delay = 1000; // 1 seconds after user stops typing
//    long last_text_edit = 0;
//    Handler handler = new Handler();
//    private Runnable input_finish_checker = new Runnable() {
//        public void run() {
//            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
//                //Log.i("TASK-----------", "TASK---------------");
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        search(false);
//                    }
//                });
//            }
//        }
//    };

}
