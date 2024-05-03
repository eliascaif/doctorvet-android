package com.xionce.doctorvetServices.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.xionce.doctorvetServices.DoctorVetApp;
import com.xionce.doctorvetServices.R;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class Purchase_itemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public enum PurchaseDetailAdapter_types { SHOW, FOR_PURCHASE_INPUT}

    private final PurchaseDetailAdapter_types adapter_type;
    private final ArrayList<Purchase_item> purchaseDetails;
    private HelperClass.AdapterOnRemoveItemHandler removeItemHandler;
    private boolean show_remove_action = true;

    public Purchase_itemAdapter(ArrayList<Purchase_item> purchaseDetails, PurchaseDetailAdapter_types adapter_type) {
        this.purchaseDetails = purchaseDetails;
        this.adapter_type = adapter_type;
    }
    public Purchase_itemAdapter(ArrayList<Purchase_item> purchaseDetails, PurchaseDetailAdapter_types adapter_type, boolean show_remove_action) {
        this.adapter_type = adapter_type;
        this.purchaseDetails = purchaseDetails;
        this.show_remove_action = show_remove_action;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch (adapter_type) {
            case SHOW:
                View view_none = inflater.inflate(R.layout.list_item_sell_detail_none, viewGroup, false);
                return new PurchaseViewHolder(view_none);
            case FOR_PURCHASE_INPUT:
                View view = inflater.inflate(R.layout.list_item_sell_detail_remove, viewGroup, false);
                return new PurchaseHolder(view);
            default:
                throw new RuntimeException("non-existent viewType");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Purchase_item purchaseItem = purchaseDetails.get(position);

        switch (adapter_type) {
            case SHOW:
                bindShow2(purchaseItem, (PurchaseViewHolder)holder, position);
                break;
            case FOR_PURCHASE_INPUT:
                bindForPurchaseInput2(purchaseItem, (PurchaseHolder)holder);
                break;
        }
    }
    private void bindForPurchaseInput2(Purchase_item selldetail, PurchaseHolder holder) {
        Context ctx = holder.img_thumb.getContext();
        String thumb_url = selldetail.getProduct().getThumb_url();
        if (thumb_url != null && !thumb_url.isEmpty()) {
            Glide.with(ctx).load(thumb_url).apply(RequestOptions.fitCenterTransform()).apply(RequestOptions.circleCropTransform()).into(holder.img_thumb);
        } else {
            Glide.with(ctx).load(R.drawable.ic_product_light).apply(RequestOptions.fitCenterTransform()).into(holder.img_thumb);
        }

        holder.txt_product.setText(selldetail.getProduct().getName());
        holder.txt_item_description.setText(selldetail.getDescription());
        holder.txt_subtotal.setText(selldetail.getSubtotal().toString());

        if (this.show_remove_action) {
            holder.img_remove.setVisibility(View.VISIBLE);
            holder.img_remove.setClickable(true);
        } else {
            holder.img_remove.setVisibility(View.GONE);
        }
    }
    private void bindShow2(Purchase_item selldetail, PurchaseViewHolder holder, int position) {
        DoctorVetApp.get().setThumb(selldetail.getProduct().getThumb_url(), holder.img_thumb, R.drawable.ic_product_light);
        holder.txt_product.setText(selldetail.getProduct().getName());
        holder.txt_item_description.setText(selldetail.getDescription());
        holder.txt_subtotal.setText(selldetail.getSubtotal().toString());
    }

    @Override
    public int getItemCount() {
        return purchaseDetails.size();
    }

    public ArrayList<Purchase_item> getArrayList() {
        return purchaseDetails;
    }
    public Purchase_item getItem(int position) {
        return purchaseDetails.get(position);
    }

    public void addItem(Purchase_item selldetail) {
        purchaseDetails.add(selldetail);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        purchaseDetails.remove(position);
        notifyItemRemoved(position);
    }
    public void setOnRemoveItemHandler(HelperClass.AdapterOnRemoveItemHandler removeItemHandler) {
        this.removeItemHandler = removeItemHandler;
    }
    public BigDecimal getTotal() {
        BigDecimal total = new BigDecimal(0);
        for (Purchase_item item: purchaseDetails) {
            total = total.add(item.getSubtotal());
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }
    public void updateList(ArrayList<Purchase_item> items) {
        purchaseDetails.clear();
        purchaseDetails.addAll(items);
        notifyDataSetChanged();
    }

    public class PurchaseHolder extends RecyclerView.ViewHolder {
        private final ImageView img_thumb;
        private final TextView txt_product;
        private final TextView txt_item_description;
        private final TextView txt_subtotal;
        private final ImageView img_remove;

        public PurchaseHolder(View view) {
            super(view);
            img_thumb = view.findViewById(R.id.img_thumb);
            txt_product = view.findViewById(R.id.txt_product);
            txt_item_description = view.findViewById(R.id.txt_item_description);
            txt_subtotal = view.findViewById(R.id.txt_subtotal);

            img_remove = view.findViewById(R.id.img_remove);
            img_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (removeItemHandler == null)
                        return;

                    view.setClickable(false);

                    int pos = getAdapterPosition();
                    Purchase_item selldetail = purchaseDetails.get(pos);
                    removeItem(pos);
                    removeItemHandler.onRemoveItem(selldetail, view, pos);
                }
            });
        }
    }
    public class PurchaseViewHolder extends RecyclerView.ViewHolder {
        private final ImageView img_thumb;
        private final TextView txt_product;
        private final TextView txt_item_description;
        private final TextView txt_subtotal;

        public PurchaseViewHolder(View view) {
            super(view);
            img_thumb = view.findViewById(R.id.img_thumb);
            txt_product = view.findViewById(R.id.txt_product);
            txt_item_description = view.findViewById(R.id.txt_item_description);
            txt_subtotal = view.findViewById(R.id.txt_subtotal);
        }
    }

}