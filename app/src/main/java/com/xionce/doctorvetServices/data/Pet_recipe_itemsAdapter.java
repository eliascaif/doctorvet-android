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

public class Pet_recipe_itemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public enum RecipeItemsAdapterTypes { SHOW, FOR_RECIPE_INPUT }

    private final RecipeItemsAdapterTypes adapter_type;
    private final ArrayList<Pet_recipe_item> recipeItems;
    private HelperClass.AdapterOnRemoveItemHandler removeItemHandler;
    private boolean show_remove_action = true;

    public Pet_recipe_itemsAdapter(ArrayList<Pet_recipe_item> recipeItems, RecipeItemsAdapterTypes adapter_type) {
        this.recipeItems = recipeItems;
        this.adapter_type = adapter_type;
    }
    public Pet_recipe_itemsAdapter(ArrayList<Pet_recipe_item> recipeItems, RecipeItemsAdapterTypes adapter_type, boolean show_remove_action) {
        this.recipeItems = recipeItems;
        this.adapter_type = adapter_type;
        this.show_remove_action = show_remove_action;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch (adapter_type) {
            case SHOW:
                View view_none = inflater.inflate(R.layout.list_item_recipe_detail_none, viewGroup, false);
                return new RecipeViewHolder(view_none);
            case FOR_RECIPE_INPUT:
                View view_input_2 = inflater.inflate(R.layout.list_item_recipe_detail_remove, viewGroup, false);
                return new RecipeInputHolder(view_input_2);
            default:
                throw new RuntimeException("non-existent viewType");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Pet_recipe_item sellItem = recipeItems.get(position);

        switch (adapter_type) {
            case SHOW:
                bindShow(sellItem, (RecipeViewHolder)holder, position);
                break;
            case FOR_RECIPE_INPUT:
                bindForRecipeInput(sellItem, (RecipeInputHolder)holder, position);
                break;
        }
    }

    private void bindForRecipeInput(Pet_recipe_item recipeItem, RecipeInputHolder holder, int position) {
        DoctorVetApp.get().setThumb(recipeItem.getProduct().getThumb_url(), holder.img_thumb, R.drawable.ic_product_light);

        holder.txt_product.setText(recipeItem.getProduct().getName());
        holder.txt_item_description.setText(recipeItem.getDescription());

        if (this.show_remove_action) {
            holder.img_remove.setVisibility(View.VISIBLE);
            holder.img_remove.setClickable(true);
        } else {
            holder.img_remove.setVisibility(View.GONE);
        }
    }
    private void bindShow(Pet_recipe_item recipeItem, RecipeViewHolder holder, int position) {
        DoctorVetApp.get().setThumb(recipeItem.getProduct().getThumb_url(), holder.img_thumb, R.drawable.ic_product_light);
        holder.txt_product.setText(recipeItem.getProduct().getName());
        holder.txt_item_description.setText(recipeItem.getDescription());
    }

    @Override
    public int getItemCount() {
        return recipeItems.size();
    }

    public ArrayList<Pet_recipe_item> getArrayList() {
        return recipeItems;
    }
    public Pet_recipe_item getItem(int position) {
        return recipeItems.get(position);
    }

    public void addItem(Pet_recipe_item sellItem) {
        recipeItems.add(sellItem);
        notifyDataSetChanged();
    }
    public void addItemFisrt(Pet_recipe_item sellItem) {
        recipeItems.add(0, sellItem);
        notifyDataSetChanged();
    }
    public void removeItem(int position) {
        recipeItems.remove(position);
        notifyItemRemoved(position);
    }
    public void setOnRemoveItemHandler(HelperClass.AdapterOnRemoveItemHandler removeItemHandler) {
        this.removeItemHandler = removeItemHandler;
    }
    public void updateList(ArrayList<Pet_recipe_item> items) {
        recipeItems.clear();
        recipeItems.addAll(items);
        notifyDataSetChanged();
    }

    public class RecipeInputHolder extends RecyclerView.ViewHolder {
        private final ImageView img_thumb;
        private final TextView txt_product;
        private final TextView txt_item_description;
        private final ImageView img_remove;

        public RecipeInputHolder(View view) {
            super(view);
            img_thumb = view.findViewById(R.id.img_thumb);
            txt_product = view.findViewById(R.id.txt_product);
            txt_item_description = view.findViewById(R.id.txt_item_description);

            img_remove = view.findViewById(R.id.img_remove);
            img_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (removeItemHandler == null)
                        return;

                    view.setClickable(false);

                    int pos = getAdapterPosition();
                    Pet_recipe_item selldetail = recipeItems.get(pos);
                    removeItem(pos);
                    removeItemHandler.onRemoveItem(selldetail, view, pos);
                }
            });
        }

    }
    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        private final ImageView img_thumb;
        private final TextView txt_product;
        private final TextView txt_item_description;

        public RecipeViewHolder(View view) {
            super(view);
            img_thumb = view.findViewById(R.id.img_thumb);
            txt_product = view.findViewById(R.id.txt_product);
            txt_item_description = view.findViewById(R.id.txt_item_description);
        }
    }

}