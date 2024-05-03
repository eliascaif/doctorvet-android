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

import java.math.BigDecimal;
import java.util.ArrayList;

public class LowFoodAdapter extends RecyclerView.Adapter<LowFoodAdapter.LowFoodHolder> {

    private HelperClass.AdapterOnClickHandler clickHandler;
    private final ArrayList<LowFood> lowFoods;

    public LowFoodAdapter(ArrayList<LowFood> lowFoods) {
        this.lowFoods = lowFoods;
    }

    @Override
    public LowFoodHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_low_food;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new LowFoodHolder(view);
    }

    @Override
    public void onBindViewHolder(final LowFoodHolder holder, int position) {
        LowFood lowFood = this.lowFoods.get(position);
        DoctorVetApp.get().setThumb(lowFood.getPet().getThumb_url(), holder.img_thumb, R.drawable.ic_dog);
        holder.txt_pet.setText(lowFood.getPet().getName());
        holder.txt_owners.setText("De: " + lowFood.getPet().getOwnersNames());
        holder.txt_products.setText("Producto: " + lowFood.getProduct().getName());
        holder.txt_level.setText("Porcentaje restante: " + lowFood.getRest_percentage().toString());
    }

    public void addItems(ArrayList<LowFood> items) {
        this.lowFoods.addAll(items);
        notifyDataSetChanged();
    }

    public void setOnClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    @Override
    public int getItemCount() {
        return lowFoods.size();
    }

    public class LowFoodHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView img_thumb;
        private final TextView txt_pet;
        private final TextView txt_owners;
        private final TextView txt_products;
        private final TextView txt_level;

        public LowFoodHolder(View view) {
            super(view);
            img_thumb = view.findViewById(R.id.img_thumb);
            txt_pet = view.findViewById(R.id.txt_pet);
            txt_owners = view.findViewById(R.id.txt_owners);
            txt_products = view.findViewById(R.id.txt_products);
            txt_level = view.findViewById(R.id.txt_level);
            view.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler == null)
                return;

            int pos = getAdapterPosition();
            LowFood lowFood = lowFoods.get(pos);
            clickHandler.onClick(lowFood, this.itemView, pos);
        }
    }
}