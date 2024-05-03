package com.xionce.doctorvetServices.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.xionce.doctorvetServices.DoctorVetApp;
import com.xionce.doctorvetServices.R;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.util.ArrayList;
import java.util.List;

public class Products_categoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements DoctorVetApp.IGetSelectedObject, DoctorVetApp.IGetIdByValueAdapter {

    private final ArrayList<Product_category> product_categories;
    private final DoctorVetApp.AdapterSelectTypes adapterType;
    private HelperClass.AdapterOnClickHandler clickHandler;
    private HelperClass.AdapterOnCancelClickHandler removeClickHandler;

    public Products_categoriesAdapter(ArrayList<Product_category> product_categories, DoctorVetApp.AdapterSelectTypes adapterType) {
        this.product_categories = product_categories;
        this.adapterType = adapterType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch (adapterType) {
            case NORMAL:
                View view_extended = inflater.inflate(R.layout.list_item_simple, viewGroup, false);
                return new NormalViewHolder(view_extended);
            case REMOVE:
                View view_compact = inflater.inflate(R.layout.list_item_remove, viewGroup, false);
                return new RemoveViewHolder(view_compact);
            default:
                throw new RuntimeException("non-existent viewType");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Product_category productcategory = product_categories.get(position);
        switch (adapterType) {
            case NORMAL:
                bindNormal(productcategory, (NormalViewHolder)holder, position);
                break;
            case REMOVE:
                bindRemove(productcategory, (RemoveViewHolder)holder, position);
                break;
        }
    }

    private void bindNormal(Product_category productcategory, NormalViewHolder holder, int position) {
        holder.txt_title.setText(productcategory.getName());
    }
    private void bindRemove(Product_category productcategory, RemoveViewHolder holder, int position) {
        holder.txt_item_name.setText(productcategory.getName());
    }

    @Override
    public int getItemCount() {
        return product_categories.size();
    }

    public ArrayAdapter<Product_category> getArrayAdapter(Context ctx) {
        return new ArrayAdapter<Product_category>(ctx, android.R.layout.simple_dropdown_item_1line, product_categories);
    }
    public void setOnClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }
    public void setOnRemoveClickHandler(HelperClass.AdapterOnCancelClickHandler removeClickHandler) {
        this.removeClickHandler = removeClickHandler;
    }

    public Integer getId_by_name(String name) {
        for (Product_category productcategory : product_categories) {
            if (productcategory.getName().equalsIgnoreCase(name))
                return productcategory.getId();
        }

        return null;
    }

    @Override
    public Integer getIdByValue(String value) {
        return getId_by_name(value);
    }

    public void setList(ArrayList<Product_category> list) {
        product_categories.clear();
        product_categories.addAll(list);
        notifyDataSetChanged();
    }

    public void addItems(ArrayList<Product_category> items) {
        this.product_categories.addAll(items);
        notifyDataSetChanged();
    }

    public void addCategory(Product_category productcategory) {
        if (!existsByName(productcategory.getName())) {
            product_categories.add(productcategory);
            notifyDataSetChanged();
        }
    }

    public boolean existsByName(String name) {
        for (Product_category p: product_categories)
            if (p.getName().equalsIgnoreCase(name))
                return true;

        return false;
    }

    public void remove(Product_category category) {
        product_categories.remove(category);
        notifyDataSetChanged();
    }

    @Override
    public Object getObjectByName(String name) {
        for (Product_category productCategory: product_categories) {
            if (productCategory.getName().equals(name))
                return productCategory;
        }
        return null;
    }

    public class NormalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView txt_title;
        public final TextView txt_subtitle;

        public NormalViewHolder(View view) {
            super(view);
            txt_title = view.findViewById(R.id.txt_title);
            txt_subtitle = view.findViewById(R.id.txt_subtitle);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickHandler != null) {
                int pos = getAdapterPosition();
                Product_category productcategory = product_categories.get(pos);
                clickHandler.onClick(productcategory, this.itemView, pos);
            }
        }
    }

    public class RemoveViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView txt_item_name;

        public RemoveViewHolder(View view) {
            super(view);
            txt_item_name = view.findViewById(R.id.txt_item_name);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (removeClickHandler != null) {
                int pos = getAdapterPosition();
                Product_category productcategory = product_categories.get(pos);
                removeClickHandler.onCancelClick(productcategory, this.itemView, pos);
            }
        }
    }

}
