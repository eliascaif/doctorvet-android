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

public class ViewProductActivity extends ViewBaseActivity
        implements BottomSheetDialog.BottomSheetListener2 {

    private static final String TAG = "ViewProductVetActivity";
    private Product product = null;
    private TextView label_expires;
    private TextView txt_expires;
    private TextView label_is_study;
    private TextView txt_is_study;
    private TextView txt_p1;
    private TextView txt_p2;
    private TextView txt_p3;
    private TextView txt_cost;
    private TextView txt_tax;
    private TextView txt_quantity;
    private TextView txt_min_quantity;
    private TextView txt_complex_qty;
//    private LinearLayout linear_p1;
//    private LinearLayout linear_p2;
//    private LinearLayout linear_p3;

    private Button btn_assoc;
//    private Button btn_delete;
    private Button btn_restore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_view_product);
        toolbar_subtitle.setVisibility(View.GONE);
        label_expires = findViewById(R.id.label_expires);
        txt_expires = findViewById(R.id.txt_expires);
        label_is_study = findViewById(R.id.label_is_study);
        txt_is_study = findViewById(R.id.txt_is_study);
        txt_p1 = findViewById(R.id.txt_p1);
        txt_p2 = findViewById(R.id.txt_p2);
        txt_p3 = findViewById(R.id.txt_p3);
        txt_cost = findViewById(R.id.txt_cost);
        txt_tax = findViewById(R.id.txt_tax);
        txt_quantity = findViewById(R.id.txt_quantity);
        txt_min_quantity = findViewById(R.id.txt_min_quantity);
        txt_complex_qty = findViewById(R.id.txt_complex_unit_quantity);
//        linear_p1 = findViewById(R.id.linear_p1);
//        linear_p2 = findViewById(R.id.linear_p2);
//        linear_p3 = findViewById(R.id.linear_p3);

        btn_assoc = findViewById(R.id.btn_assoc);
        btn_assoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                associate_product();
            }
        });
//        btn_delete = findViewById(R.id.btn_delete);
//        btn_delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                go_delete();
//            }
//        });
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
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ViewProductActivity.this, "ViewProductVetActivity");
                bottomSheetDialog.show(getSupportFragmentManager(), null);
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

        String product_name = product.getName();
        if (product.getVet_issued_name() != null)
            product_name = product.getVet_issued_name()  + " (" + product.getName() + ")";

        toolbar_title.setText(product_name);
        DoctorVetApp.ObjectToTextView(findViewById(R.id.linear_product), product, "txt_");

        TextView txt_name = findViewById(R.id.txt_name);
        txt_name.setText(product_name);

        TextView txt_unit = findViewById(R.id.txt_unit);
        txt_unit.setText(product.getUnit().getName());

        label_expires.setVisibility(View.GONE);
        txt_expires.setVisibility(View.GONE);
        if (!product.getIs_service()) {
            label_expires.setVisibility(View.VISIBLE);
            txt_expires.setVisibility(View.VISIBLE);
            if (product.getExpires() == 1)
                txt_expires.setText(R.string.si);
            else
                txt_expires.setText(R.string.no);
        }

        label_is_study.setVisibility(View.GONE);
        txt_is_study.setVisibility(View.GONE);
        if (product.getIs_service()) {
            txt_expires.setText("");
            txt_quantity.setText("");
            label_is_study.setVisibility(View.VISIBLE);
            txt_is_study.setVisibility(View.VISIBLE);
            if (product.getIs_study() == 1)
                txt_is_study.setText(R.string.si);
            else
                txt_is_study.setText(R.string.no);
        } else {
            txt_is_study.setText("");
        }

        //categorias
        TextView txt_categorias = findViewById(R.id.txt_categories);
        txt_categorias.setText(product.getCategoriesNames());

        String thumb_url = product.getThumb_url();
        if (thumb_url != null && !thumb_url.isEmpty()) {
            Glide.with(getApplicationContext()).load(thumb_url).apply(RequestOptions.fitCenterTransform()).apply(RequestOptions.circleCropTransform()).into(toolbar_image);

            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent activity = new Intent(ViewProductActivity.this, ViewFullScreenPhoto.class);
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
        txt_cost.setText(product.getRoundedCost());
        txt_tax.setText(product.getRoundedTax());

        if (!product.getIs_service()) {
            txt_quantity.setText(product.getQuantity_string());
            txt_min_quantity.setText(product.getRoundedMinQuantity());
            txt_complex_qty.setText(product.getRoundedComplexQuantity());

            //quantity detail
            TextView txt_quantity_detail = findViewById(R.id.txt_quantity_description);
            //txt_quantity_detail.setVisibility(View.GONE);
            if (product.getQuantity_detail() != null) {
                //txt_quantity_detail.setVisibility(View.VISIBLE);
                txt_quantity_detail.setText(product.getQuantityDetailDescription());
            }

            //quantity detail branchs
            //TextView label_quantity_description_branchs = findViewById(R.id.label_quantity_description_branchs);
            TextView txt_quantity_detail_branchs = findViewById(R.id.txt_quantity_description_branchs);
            //label_quantity_description_branchs.setVisibility(View.GONE);
            //txt_quantity_detail_branchs.setVisibility(View.GONE);
            if (product.getQuantity_detail_branchs() != null) {
                //label_quantity_description_branchs.setVisibility(View.VISIBLE);
                //txt_quantity_detail_branchs.setVisibility(View.VISIBLE);
                txt_quantity_detail_branchs.setText(product.getQuantityDetailBranchsDescription());
            }
        }
        
//        linear_p1.setVisibility(View.GONE);
//        linear_p2.setVisibility(View.GONE);
//        linear_p3.setVisibility(View.GONE);
//        if (product.getRoundedPrice1() != null) linear_p1.setVisibility(View.VISIBLE);
//        if (product.getRoundedPrice2() != null) linear_p2.setVisibility(View.VISIBLE);
//        if (product.getRoundedPrice3() != null) linear_p3.setVisibility(View.VISIBLE);
        txt_p1.setVisibility(View.GONE);
        txt_p2.setVisibility(View.GONE);
        txt_p3.setVisibility(View.GONE);
        if (product.getRoundedPrice1() != null) txt_p1.setVisibility(View.VISIBLE);
        if (product.getRoundedPrice2() != null) txt_p2.setVisibility(View.VISIBLE);
        if (product.getRoundedPrice3() != null) txt_p3.setVisibility(View.VISIBLE);

        //buttons for assoc, delete and restore from previous delete
        btn_assoc.setVisibility(View.GONE);
//        btn_delete.setVisibility(View.GONE);
        btn_restore.setVisibility(View.GONE);
        if (product.getIsAssociate_with_vet() == 1 && product.getDeleted() == 1)
            btn_restore.setVisibility(View.VISIBLE);

        if (product.getIsAssociate_with_vet() == 0)
            btn_assoc.setVisibility(View.VISIBLE);

        invisibilizeEmptyViews();
    }

    @Override
    protected void invisibilizeEmptyViews() {
        LinearLayout linearProducts = findViewById(R.id.linear_product);
        DoctorVetApp.setTextViewVisibility(linearProducts);
//        DoctorVetApp.setTextViewVisibility(linearProducts.findViewById(R.id.linear_p1));
//        DoctorVetApp.setTextViewVisibility(linearProducts.findViewById(R.id.linear_p2));
//        DoctorVetApp.setTextViewVisibility(linearProducts.findViewById(R.id.linear_p3));
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
                                        on_delete_complete(id_product);
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
    }

    @Override
    protected void on_delete_complete(Integer deleted_id) {
    }

    private Integer getIdProduct() {
        return getIntent().getIntExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_ID.name(), 0);
    }

    @Override
    public void onButtonClicked(BottomSheetDialog.Buttons buttonClicked) {
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
            default:
                DoctorVetApp.get().handleGeneralBottomSheetClick(buttonClicked, ViewProductActivity.this);
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
                            Snackbar.make(DoctorVetApp.getRootForSnack( ViewProductActivity.this), "Producto asociado", Snackbar.LENGTH_SHORT).show();
                            refreshView();
                        } else {
                            Snackbar.make(DoctorVetApp.getRootForSnack( ViewProductActivity.this), "Error al asociar producto", Snackbar.LENGTH_SHORT).show();
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
                                Snackbar.make(DoctorVetApp.getRootForSnack(ViewProductActivity.this), "Producto restablecido", Snackbar.LENGTH_SHORT).show();
                                //DoctorVetApp.get().setInvalidate(DoctorVetApp.invalidateTables.PRODUCTS, true);
                                refreshView();
                            } else {
                                Snackbar.make(DoctorVetApp.getRootForSnack(ViewProductActivity.this), "Error al restablecer producto", Snackbar.LENGTH_SHORT).show();
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
