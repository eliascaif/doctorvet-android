package com.xionce.doctorvetServices.data;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xionce.doctorvetServices.DoctorVetApp;
import com.xionce.doctorvetServices.R;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class Sell_itemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public enum SellItemsAdapterTypes { SHOW, FOR_SELL_INPUT }

    private final SellItemsAdapterTypes adapter_type;
    private final ArrayList<Sell_item> sellItems;
    private HelperClass.AdapterOnRemoveItemHandler removeItemHandler;
    private boolean show_remove_action = true;

    public Sell_itemAdapter(ArrayList<Sell_item> sellItems, SellItemsAdapterTypes adapter_type) {
        this.sellItems = sellItems;
        this.adapter_type = adapter_type;
    }
    public Sell_itemAdapter(ArrayList<Sell_item> sellItems, SellItemsAdapterTypes adapter_type, boolean show_remove_action) {
        this.sellItems = sellItems;
        this.adapter_type = adapter_type;
        this.show_remove_action = show_remove_action;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch (adapter_type) {
            case SHOW:
                View view_none = inflater.inflate(R.layout.list_item_sell_detail_none, viewGroup, false);
                return new SellViewHolder(view_none);
            case FOR_SELL_INPUT:
                View view_input_2 = inflater.inflate(R.layout.list_item_sell_detail_remove, viewGroup, false);
                return new SellInputHolder(view_input_2);
            default:
                throw new RuntimeException("non-existent viewType");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Sell_item sellItem = sellItems.get(position);

        switch (adapter_type) {
            case SHOW:
                bindShow(sellItem, (SellViewHolder)holder, position);
                break;
            case FOR_SELL_INPUT:
                bindForSellInput(sellItem, (SellInputHolder)holder, position);
                break;
        }
    }

    private void bindForSellInput(Sell_item sellItem, SellInputHolder holder, int position) {
        DoctorVetApp.get().setThumb(sellItem.getProduct().getThumb_url(), holder.img_thumb, R.drawable.ic_product_light);

        holder.txt_product.setText(sellItem.getProduct().getName());
        holder.txt_item_description.setText(sellItem.getDescription());
        holder.txt_subtotal.setText(sellItem.getSubtotal().toString());

        if (this.show_remove_action) {
            holder.img_remove.setVisibility(View.VISIBLE);
            holder.img_remove.setClickable(true);
        } else {
            holder.img_remove.setVisibility(View.GONE);
        }
    }
    private void bindShow(Sell_item selldetail, SellViewHolder holder, int position) {
        DoctorVetApp.get().setThumb(selldetail.getProduct().getThumb_url(), holder.img_thumb, R.drawable.ic_product_light);
        holder.txt_product.setText(selldetail.getProduct().getName());
        holder.txt_item_description.setText(selldetail.getDescription());
        holder.txt_subtotal.setText(selldetail.getSubtotal().toString());
    }

    @Override
    public int getItemCount() {
        return sellItems.size();
    }

    public ArrayList<Sell_item> getArrayList() {
        return sellItems;
    }
    public Sell_item getItem(int position) {
        return sellItems.get(position);
    }

    public void addItem(Sell_item sellItem) {
        sellItems.add(sellItem);
        notifyDataSetChanged();
    }
    public void addItemFisrt(Sell_item sellItem) {
        sellItems.add(0, sellItem);
        notifyDataSetChanged();
    }
    public void removeItem(int position) {
        sellItems.remove(position);
        notifyItemRemoved(position);
    }
    public void setOnRemoveItemHandler(HelperClass.AdapterOnRemoveItemHandler removeItemHandler) {
        this.removeItemHandler = removeItemHandler;
    }
    public BigDecimal getTotal() {
        BigDecimal total = new BigDecimal(0);
        for (Sell_item item: sellItems) {
            total = total.add(item.getSubtotal());
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }
    public void updateList(ArrayList<Sell_item> items) {
        sellItems.clear();
        sellItems.addAll(items);
        notifyDataSetChanged();
    }

//    public Integer getProductsCount() {
//        Integer productsCount = 0;
//
//        for (Sell_item sellItem:sellItems) {
//            if (sellItem.getProduct().getIs_service() == null)
//                return 0;
//
//            if (!sellItem.getProduct().getIs_service())
//                productsCount++;
//        }
//
//        return productsCount;
//    }
//    public Integer getServicesCount() {
//        Integer servicesCount = 0;
//
//        for (Sell_item sellItem:sellItems) {
//            if (sellItem.getProduct().getIs_service() == null)
//                return 0;
//
//            if (sellItem.getProduct().getIs_service())
//                servicesCount++;
//        }
//
//        return servicesCount;
//    }

    public class SellInputHolder extends RecyclerView.ViewHolder {
        private final ImageView img_thumb;
        private final TextView txt_product;
        private final TextView txt_item_description;
        private final TextView txt_subtotal;
        private final ImageView img_remove;

        public SellInputHolder(View view) {
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
                    Sell_item selldetail = sellItems.get(pos);
                    removeItem(pos);
                    removeItemHandler.onRemoveItem(selldetail, view, pos);
                }
            });
        }

    }
    public class SellViewHolder extends RecyclerView.ViewHolder {
        private final ImageView img_thumb;
        private final TextView txt_product;
        private final TextView txt_item_description;
        private final TextView txt_subtotal;

        public SellViewHolder(View view) {
            super(view);
            img_thumb = view.findViewById(R.id.img_thumb);
            txt_product = view.findViewById(R.id.txt_product);
            txt_item_description = view.findViewById(R.id.txt_item_description);
            txt_subtotal = view.findViewById(R.id.txt_subtotal);
        }
    }

}