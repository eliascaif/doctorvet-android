package com.xionce.doctorvetServices.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.xionce.doctorvetServices.R;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.util.ArrayList;

public class Movement_itemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public enum MovementAdapter_types { FOR_MOVEMENT_INPUT, SHOW }

    private final MovementAdapter_types adapter_type;
    private final ArrayList<Movement_item> movementItems;
    private HelperClass.AdapterOnRemoveItemHandler removeItemHandler;
    private boolean show_remove_action = true;

    public Movement_itemAdapter(ArrayList<Movement_item> movementItems, MovementAdapter_types adapter_type) {
        this.movementItems = movementItems;
        this.adapter_type = adapter_type;
    }
    public Movement_itemAdapter(ArrayList<Movement_item> movementItems, MovementAdapter_types adapter_type, boolean show_remove_action) {
        this.movementItems = movementItems;
        this.adapter_type = adapter_type;
        this.show_remove_action = show_remove_action;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch (adapter_type) {
            case FOR_MOVEMENT_INPUT:
                View view_delete = inflater.inflate(R.layout.list_item_movement_detail_remove, viewGroup, false);
                return new MovementInputHolder(view_delete);
            case SHOW:
                View view_none = inflater.inflate(R.layout.list_item_movement_detail_none, viewGroup, false);
                return new MovementViewHolder(view_none);
            default:
                throw new RuntimeException("non-existent viewType");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Movement_item selldetail = movementItems.get(position);

        switch (adapter_type) {
            case FOR_MOVEMENT_INPUT:
                bindForSellInput(selldetail, (MovementInputHolder)holder, position);
                break;
            case SHOW:
                bindShow(selldetail, (MovementViewHolder)holder, position);
                break;
        }
    }

    private void bindForSellInput(Movement_item selldetail, MovementInputHolder holder, int position) {
        holder.txt_quantity.setText(selldetail.getQuantity() + " " + selldetail.getSelected_unit());

        Context ctx = holder.img_thumb.getContext();
        String thumb_url = selldetail.getProduct().getThumb_url();
        if (thumb_url != null && !thumb_url.isEmpty()) {
            Glide.with(ctx).load(thumb_url).apply(RequestOptions.fitCenterTransform()).apply(RequestOptions.circleCropTransform()).into(holder.img_thumb);
        } else {
            Glide.with(ctx).load(R.drawable.ic_product_light).apply(RequestOptions.fitCenterTransform()).into(holder.img_thumb);
        }

        holder.txt_product.setText(selldetail.getProduct().getName());

        if (this.show_remove_action) {
            holder.img_remove.setVisibility(View.VISIBLE);
            holder.img_remove.setClickable(true);
        } else {
            holder.img_remove.setVisibility(View.GONE);
        }

//        holder.img_remove.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (removeItemHandler != null)
//                    removeItemHandler.onRemoveItem(selldetail, view, holder.getAdapterPosition());
//
//                removeItem(holder.getAdapterPosition());
//            }
//        });
    }
    private void bindShow(Movement_item selldetail, MovementViewHolder holder, int position) {
        holder.txt_quantity.setText(selldetail.getQuantity() + " " + selldetail.getProduct().getUnit().getSecond_unit_string());

        Context ctx = holder.img_thumb.getContext();
        String thumb_url = selldetail.getProduct().getThumb_url();
        if (thumb_url != null && !thumb_url.isEmpty()) {
            Glide.with(ctx).load(thumb_url).apply(RequestOptions.fitCenterTransform()).apply(RequestOptions.circleCropTransform()).into(holder.img_thumb);
        } else {
            Glide.with(ctx).load(R.drawable.ic_product_light).apply(RequestOptions.fitCenterTransform()).into(holder.img_thumb);
        }

        holder.txt_product.setText(selldetail.getProduct().getName());
    }

    @Override
    public int getItemCount() {
        return movementItems.size();
    }

    public ArrayList<Movement_item> getArrayList() {
        return movementItems;
    }
    public Movement_item getItem(int position) {
        return movementItems.get(position);
    }

    public void addItem(Movement_item selldetail) {
        movementItems.add(selldetail);
        notifyDataSetChanged();
    }

    public void updateList(ArrayList<Movement_item> items) {
        movementItems.clear();
        movementItems.addAll(items);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        movementItems.remove(position);
        notifyItemRemoved(position);
    }
    public void setOnRemoveItemHandler(HelperClass.AdapterOnRemoveItemHandler removeItemHandler) {
        this.removeItemHandler = removeItemHandler;
    }

    public class MovementInputHolder extends RecyclerView.ViewHolder {
        protected ImageView img_thumb;
        protected TextView txt_quantity;
        protected TextView txt_product;
        protected  ImageView img_remove;

        public MovementInputHolder(View view) {
            super(view);
            img_thumb = view.findViewById(R.id.img_thumb);
            txt_quantity = view.findViewById(R.id.txt_quantity);
            txt_product = view.findViewById(R.id.txt_product);

            img_remove = view.findViewById(R.id.img_remove);
            img_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (removeItemHandler == null)
                        return;

                    view.setClickable(false);

                    int pos = getAdapterPosition();
                    Movement_item selldetail = movementItems.get(pos);
                    removeItem(pos);
                    removeItemHandler.onRemoveItem(selldetail, view, pos);
                }
            });
        }
    }
    public class MovementViewHolder extends RecyclerView.ViewHolder {
        protected ImageView img_thumb;
        protected TextView txt_quantity;
        protected TextView txt_product;
        protected  ImageView img_remove;

        public MovementViewHolder(View view) {
            super(view);
            img_thumb = view.findViewById(R.id.img_thumb);
            txt_quantity = view.findViewById(R.id.txt_quantity);
            txt_product = view.findViewById(R.id.txt_product);
        }
    }

}