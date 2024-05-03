package com.xionce.doctorvetServices.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.xionce.doctorvetServices.DoctorVetApp;
import com.xionce.doctorvetServices.R;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.util.ArrayList;

public class ProductsTraceabilityAdapter extends RecyclerView.Adapter<ProductsTraceabilityAdapter.TraceabilityViewHolder> {

    private final ArrayList<Product_traceability> productTraceabilities;

    public ProductsTraceabilityAdapter(ArrayList<Product_traceability> productTraceabilities) {
        this.productTraceabilities = productTraceabilities;
    }

    @Override
    public TraceabilityViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_product_traceability;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new TraceabilityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TraceabilityViewHolder holder, int position) {
        Product_traceability productTraceability = productTraceabilities.get(position);

        holder.txt_user.setVisibility(View.GONE);
        if (productTraceability.getUser() != null) {
            DoctorVetApp.get().setThumb(productTraceability.getUser().getThumb_url(), holder.img_image, R.drawable.ic_account_circle_light);
            holder.txt_user.setText("Usuario: " + productTraceability.getUser().getName());
            holder.txt_user.setVisibility(View.VISIBLE);
        } else {
            Glide.with(holder.img_image.getContext()).load(R.drawable.ic_account_circle_light).apply(RequestOptions.fitCenterTransform()).into(holder.img_image);
        }

        holder.txt_created_at.setText("Creado el: " + HelperClass.getDateInLocaleShort(productTraceability.getCreated_at()));
        holder.txt_quantity.setText("Cantidad: " + productTraceability.getQuantity().toString());
        holder.txt_type.setText("Tipo: " + productTraceability.getType());
        holder.txt_accumulator.setText("Acumulador: " + productTraceability.getAccumulator().toString());

        holder.txt_receipt.setVisibility(View.GONE);
        if (productTraceability.getReceipt() != null) {
            holder.txt_receipt.setText("Comprobante: " + productTraceability.getReceipt());
            holder.txt_receipt.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return productTraceabilities.size();
    }

    public void addItems(ArrayList<Product_traceability> items) {
        this.productTraceabilities.addAll(items);
        notifyDataSetChanged();
    }

    public class TraceabilityViewHolder extends RecyclerView.ViewHolder {
        private final ImageView img_image;
        private final TextView txt_user;
        private final TextView txt_created_at;
        private final TextView txt_quantity;
        private final TextView txt_type;
        private final TextView txt_accumulator;
        private final TextView txt_receipt;

        public TraceabilityViewHolder(View view) {
            super(view);
            img_image = view.findViewById(R.id.img_thumb);
            txt_user = view.findViewById(R.id.txt_user);
            txt_created_at = view.findViewById(R.id.txt_created_at);
            txt_quantity = view.findViewById(R.id.txt_quantity);
            txt_type = view.findViewById(R.id.txt_type);
            txt_accumulator = view.findViewById(R.id.txt_accumulator);
            txt_receipt = view.findViewById(R.id.txt_receipt);
        }
    }

}
