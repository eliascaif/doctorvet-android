package com.xionce.doctorvetServices;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.xionce.doctorvetServices.data.Product;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

public class ViewProductVetActivity extends ViewBaseActivity
        implements BottomSheetDialog.BottomSheetListener {

    private static final String TAG = "ViewProductVetActivity";
    private Product product = null;
    private LinearLayout linear_products_only_data;
    private LinearLayout linear_services_data;
    private LinearLayout linear_associate_products_data;
    private TextView txt_expires;
    private TextView txt_is_study;
    private TextView txt_p1;
    private TextView txt_p2;
    private TextView txt_p3;
    private TextView txt_cost;
    private TextView txt_tax;
    private TextView txt_quantity;
    private TextView txt_min_quantity;
    private TextView txt_complex_qty;

    private Button btn_assoc;
    private Button btn_delete;
    private Button btn_restore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_view_product_vet);
        toolbar_subtitle.setVisibility(View.GONE);
        linear_products_only_data = findViewById(R.id.linear_products_only_data);
        linear_services_data = findViewById(R.id.linear_services_data);
        linear_associate_products_data = findViewById(R.id.linear_associate_products_data);
        txt_expires = findViewById(R.id.txt_expires);
        txt_is_study = findViewById(R.id.txt_is_study);
        txt_p1 = findViewById(R.id.txt_p1);
        txt_p2 = findViewById(R.id.txt_p2);
        txt_p3 = findViewById(R.id.txt_p3);
        txt_cost = findViewById(R.id.txt_cost);
        txt_tax = findViewById(R.id.txt_tax);
        txt_quantity = findViewById(R.id.txt_quantity);
        txt_min_quantity = findViewById(R.id.txt_min_quantity);
        txt_complex_qty = findViewById(R.id.txt_complex_unit_quantity);

        btn_assoc = findViewById(R.id.btn_assoc);
        btn_assoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                associate_product();
            }
        });
        btn_delete = findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                go_delete();
            }
        });
        btn_restore = findViewById(R.id.btn_restore);
        btn_restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restoreProduct();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheet = new BottomSheetDialog();
                Bundle b = new Bundle();
                if (DoctorVetApp.get().userIsAdmin()) b.putString("producto_especifico", "true");
                bottomSheet.setArguments(b);
                bottomSheet.show(getSupportFragmentManager(), "bottomSheetDialog");
            }
        });
        if (!DoctorVetApp.get().userIsAdmin())
            hideFab();

        hideActivityContainer();
        setSwipeRefreshLayout();
    }

    private void showProduct() {
        showProgressBar();
        Integer id_product = getIdProduct();
        URL get_productUrl = NetworkUtils.buildGetProductVetUrl(id_product);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, get_productUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            showActivityContainer();
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            product = MySqlGson.getGson().fromJson(data, Product.class);
                            setUI(product);
                            setLoadedFinished();
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_error(ex, TAG, true);
                            showErrorMessage();
                        } finally {
                            hideProgressBar();
                            hideSwipeRefreshLayoutProgressBar();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, true);
                        hideProgressBar();
                        hideSwipeRefreshLayoutProgressBar();
                        showErrorMessage();
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    @Override
    protected void setUI(Object object) {
        Product product = (Product) object;

        linear_associate_products_data.setVisibility(View.GONE);
        linear_services_data.setVisibility(View.GONE);
        linear_products_only_data.setVisibility(View.GONE);
        if (product.getIsAssociate_with_vet() == 1) {
            linear_associate_products_data.setVisibility(View.VISIBLE);
            if (product.getIs_service()) {
                linear_services_data.setVisibility(View.VISIBLE);
            } else {
                linear_products_only_data.setVisibility(View.VISIBLE);
            }
        }

        String product_name = product.getName();
        if (product.getVet_issued_name() != null)
            product_name = product.getVet_issued_name()  + " (" + product.getName() + ")";

        toolbar_title.setText(product_name);
        DoctorVetApp.ObjectToTextView(findViewById(R.id.linear_general_product_data), product, "txt_");

        TextView txt_name = findViewById(R.id.txt_name);
        txt_name.setText(product_name);

        TextView txt_unit = findViewById(R.id.txt_unit);
        txt_unit.setText(product.getUnit().getName());

        if (product.getExpires())
            txt_expires.setText(R.string.si);
        else
            txt_expires.setText(R.string.no);

        if (product.getIs_study())
            txt_is_study.setText(R.string.si);
        else
            txt_is_study.setText(R.string.no);

        //buttons for assoc, delete and restore from previous delete
        btn_assoc.setVisibility(View.GONE);
        btn_delete.setVisibility(View.GONE);
        btn_restore.setVisibility(View.GONE);
        if (product.getIsAssociate_with_vet() == 1 && product.getDeleted() == 1)
            btn_restore.setVisibility(View.VISIBLE);

        if (product.getIsAssociate_with_vet() == 0)
            btn_assoc.setVisibility(View.VISIBLE);

        //categorias
        TextView txt_categorias = findViewById(R.id.txt_categories);
        txt_categorias.setText(product.getCategoriesNames());

        String thumb_url = product.getThumb_url();
        if (thumb_url != null && !thumb_url.isEmpty()) {
            Glide.with(getApplicationContext()).load(thumb_url).apply(RequestOptions.fitCenterTransform()).apply(RequestOptions.circleCropTransform()).into(toolbar_image);

            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent activity = new Intent(ViewProductVetActivity.this, ViewFullScreenPhoto.class);
                    activity.putExtra(HelperClass.INTENT_IMAGE_URL, product.getPhoto_url());
                    startActivity(activity);
                }
            });
        } else {
            Glide.with(getApplicationContext()).load(R.drawable.ic_product_dark).apply(RequestOptions.fitCenterTransform()).into(toolbar_image);
        }

        //precios
        txt_p1.setText(product.getRoundedPrice1());
        txt_p2.setText(product.getRoundedPrice2());
        txt_p3.setText(product.getRoundedPrice3());
//        txt_p4.setText(product.getRoundedPrice4());
        txt_cost.setText(product.getRoundedCost());
        txt_tax.setText(product.getRoundedTax());
        if (!product.getIs_service()) {
            //txt_quantity.setText(product.getRoundedQuantity());
            txt_quantity.setText(product.getQuantity_string());
            txt_min_quantity.setText(product.getRoundedMinQuantity());
            txt_complex_qty.setText(product.getRoundedComplexQuantity());

            //quantity detail
            TextView txt_quantity_detail = findViewById(R.id.txt_quantity_description);
            txt_quantity_detail.setVisibility(View.GONE);
            if (product.getQuantity_detail() != null) {
                txt_quantity_detail.setVisibility(View.VISIBLE);
                txt_quantity_detail.setText(product.getQuantityDetailDescription());
            }

            //quantity detail branchs
            TextView label_quantity_description_branchs = findViewById(R.id.label_quantity_description_branchs);
            TextView txt_quantity_detail_branchs = findViewById(R.id.txt_quantity_description_branchs);
            label_quantity_description_branchs.setVisibility(View.GONE);
            txt_quantity_detail_branchs.setVisibility(View.GONE);
            if (product.getQuantity_detail_branchs() != null) {
                label_quantity_description_branchs.setVisibility(View.VISIBLE);
                txt_quantity_detail_branchs.setVisibility(View.VISIBLE);
                txt_quantity_detail_branchs.setText(product.getQuantityDetailBranchsDescription());
            }
        }

        if (product.getRoundedPrice1() != null) findViewById(R.id.linear_p1).setVisibility(View.VISIBLE);
        if (product.getRoundedPrice2() != null) findViewById(R.id.linear_p2).setVisibility(View.VISIBLE);
        if (product.getRoundedPrice3() != null) findViewById(R.id.linear_p3).setVisibility(View.VISIBLE);
//        if (product.getRoundedPrice4() != null) findViewById(R.id.linear_p4).setVisibility(View.VISIBLE);

        invisibilizeEmptyViews();
    }

    @Override
    protected void invisibilizeEmptyViews() {
        LinearLayout linear = findViewById(R.id.linear_general_product_data);
        DoctorVetApp.invisibilizeEmptyTextView(linear);
        linear = findViewById(R.id.linear_associate_products_data);
        DoctorVetApp.invisibilizeEmptyTextView(linear);
        linear = findViewById(R.id.linear_products_only_data);
        DoctorVetApp.invisibilizeEmptyTextView(linear);
//        linear = findViewById(R.id.linear_services_data);
//        DoctorVetApp.invisibilizeEmptyTextView(linear);

        //BigDecimal zero = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
//
//        if (txt_p1.getText().toString().isEmpty()) {
//            txt_p1.setText(zero.toString());
//        }

        if (txt_p2.getText().toString().isEmpty()){
            //txt_p2.setVisibility(View.GONE);
            findViewById(R.id.linear_p2).setVisibility(View.GONE);
        }

        if (txt_p3.getText().toString().isEmpty()){
            findViewById(R.id.linear_p3).setVisibility(View.GONE);
        }

//        if (txt_p4.getText().toString().isEmpty()){
//            findViewById(R.id.linear_p4).setVisibility(View.GONE);
//        }

        if (txt_complex_qty.getText().toString().equalsIgnoreCase("0")) {
            txt_complex_qty.setVisibility(View.GONE);
            findViewById(R.id.label_complex_unit_quantity).setVisibility(View.GONE);
        }

//        if (txt_expires.getText().toString().equalsIgnoreCase("NO")) {
//            txt_expires.setVisibility(View.GONE);
//            findViewById(R.id.label_expires).setVisibility(View.GONE);
//        }

    }

    @Override
    protected void refreshView() {
        showProduct();
    }

    @Override
    protected void go_update() {
        //Global services cant be modified. they can, they can.
        Intent intent;

        if (product.getIs_service()) {
            intent = new Intent(this, EditServiceActivity.class);
        } else {
            intent = new Intent(this, EditProductActivity.class);
        }

        intent.putExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_OBJ.name(), MySqlGson.getGson().toJson(product));
        startActivityForResult(intent, HelperClass.REQUEST_UPDATE);
    }

    @Override
    protected void go_delete() {
        HelperClass.getOKCancelDialog(this, getString(R.string.action_delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showWaitDialog();
                final Integer id_product = getIdProduct();

                URL deleteProductoUrl = NetworkUtils.buildDeleteProductVetUrl(id_product);
                TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.DELETE, deleteProductoUrl.toString(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    String success = MySqlGson.getStatusFromResponse(response);
                                    if (success.equalsIgnoreCase("success")) {
                                        //DoctorVetApp.getInstance().deleteToProductosAdapter_cache(id_product);
                                        on_delete_complete(id_product);
                                        //DoctorVetApp.get().setInvalidate(DoctorVetApp.invalidateTables.PRODUCTS, true);
                                        refreshView();
                                    } else {
                                        showErrorToast(getString(R.string.error_borrando_registro), TAG);
                                    }
                                } catch (Exception ex) {
                                    DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                                } finally {
                                    hideWaitDialog();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                DoctorVetApp.get().handle_volley_error(error, TAG, true);
                                hideWaitDialog();
                            }
                        }
                );
                DoctorVetApp.get().addToRequestQueque(stringRequest);
            }
        });
    }

    @Override
    protected void on_update_complete(Intent data) {
        refreshView();
//        product = MySqlGson.getGson(true, false).fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_OBJ.name()), Product.class);
//        setUI(product);
        //DoctorVetApp.getInstance().updateToProductosAdapter_cache(producto);
    }

    @Override
    protected void on_delete_complete(Integer deleted_id) {
        //DoctorVetApp.getInstance().deleteToProductosAdapter_cache(deleted_id);
    }

    private Integer getIdProduct() {
        return getIntent().getIntExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_ID.name(), 0);
    }

    @Override
    public void onButtonClicked(BottomSheetDialog.BottomSheetButtonClicked buttonClicked) {
        if (!loadedFinished) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), R.string.error_cargando_registro, Snackbar.LENGTH_SHORT).show();
            return;
        }

        switch (buttonClicked) {
            case PRODUCT_UPDATE:
                go_update();
                break;
            case PRODUCT_DELETE:
                go_delete();
                break;
//            case PRODUCT_ASSOCIATE:
//                associate_product();
//                break;
            default:
                DoctorVetApp.get().handleGeneralBottomSheetClick(buttonClicked, ViewProductVetActivity.this);
        }
    }

    private void associate_product() {
        showWaitDialog();

        HashMap hashMap = new HashMap();
        hashMap.put("products", Arrays.asList(product.getId()));
        final String json_object = MySqlGson.getGson().toJson(hashMap);

        URL updateProductAssoc = NetworkUtils.buildProductsUrl(NetworkUtils.ProductsUrlEnum.ASSOCIATES, null);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.PUT, updateProductAssoc.toString(),
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        String success = MySqlGson.getStatusFromResponse(response);
                        if (success.equalsIgnoreCase("success")) {
                            Snackbar.make(DoctorVetApp.getRootForSnack( ViewProductVetActivity.this), "Producto asociado", Snackbar.LENGTH_SHORT).show();
                            //DoctorVetApp.get().setInvalidate(DoctorVetApp.invalidateTables.PRODUCTS, true);
                            refreshView();
                        } else {
                            Snackbar.make(DoctorVetApp.getRootForSnack( ViewProductVetActivity.this), "Error al asociar producto", Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (Exception ex) {
                        DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                    } finally {
                        hideWaitDialog();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    DoctorVetApp.get().handle_volley_error(error, TAG, true);
                    hideWaitDialog();
                }
            })
            {
                @Override
                public byte[] getBody() {
                    return json_object.getBytes();
                }
            };

        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    private void restoreProduct() {
        showWaitDialog();

        URL restoreProductUrl = NetworkUtils.buildProductsUrl(NetworkUtils.ProductsUrlEnum.RESTORE, getIdProduct());
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.PUT, restoreProductUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String success = MySqlGson.getStatusFromResponse(response);
                            if (success.equalsIgnoreCase("success")) {
                                Snackbar.make(DoctorVetApp.getRootForSnack(ViewProductVetActivity.this), "Producto restablecido", Snackbar.LENGTH_SHORT).show();
                                //DoctorVetApp.get().setInvalidate(DoctorVetApp.invalidateTables.PRODUCTS, true);
                                refreshView();
                            } else {
                                Snackbar.make(DoctorVetApp.getRootForSnack(ViewProductVetActivity.this), "Error al restablecer producto", Snackbar.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                        } finally {
                            hideWaitDialog();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, true);
                        hideWaitDialog();
                    }
                }
            );

        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

}
