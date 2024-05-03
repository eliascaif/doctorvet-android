package com.xionce.doctorvetServices.data;

import android.content.Context;
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

public class Products_unitsAdapter extends RecyclerView.Adapter<Products_unitsAdapter.Productos_unitesAdapterViewHolder>
        implements DoctorVetApp.IGetIdByValueAdapter, DoctorVetApp.IGetSelectedObject {

    private final ArrayList<Product_unit> productos_unites;
    private HelperClass.AdapterOnClickHandler clickHandler;

    public Products_unitsAdapter(ArrayList<Product_unit> productos_unites) {
        this.productos_unites = productos_unites;
    }

    @Override
    public Productos_unitesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_simple;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new Productos_unitesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Productos_unitesAdapterViewHolder Productos_unitesAdapterViewHolder, int position) {
        Product_unit productunit = productos_unites.get(position);
        Productos_unitesAdapterViewHolder.txt_title.setText(productunit.getName());
    }

    @Override
    public int getItemCount() {
        return productos_unites.size();
    }

    public ArrayAdapter<Product_unit> getArrayAdapter(Context ctx) {
        ArrayAdapter<Product_unit> arrayAdapter = new ArrayAdapter<Product_unit>(ctx, android.R.layout.simple_dropdown_item_1line, productos_unites);
        return arrayAdapter;
    }

    public Integer getId_by_name(String name) {
        for (Product_unit productunit : productos_unites) {
            if (productunit.getName().equalsIgnoreCase(name))
                return productunit.getId();
        }
        return null;
    }
    @Override
    public Integer getIdByValue(String value) {
        return getId_by_name(value);
    }
    @Override
    public Object getObjectByName(String name) {
        for (Product_unit productunit : productos_unites) {
            if (productunit.getName().equalsIgnoreCase(name))
                return productunit;
        }
        return null;
    }

    public void addItems(ArrayList<Product_unit> items) {
        this.productos_unites.addAll(items);
        notifyDataSetChanged();
    }
    public void setOnClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }


    public void setList(ArrayList<Product_unit> list) {
        productos_unites.clear();
        productos_unites.addAll(list);
        notifyDataSetChanged();
    }

    public class Productos_unitesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView txt_title;
        public final TextView txt_subtitle;

        public Productos_unitesAdapterViewHolder(View view) {
            super(view);
            txt_title = view.findViewById(R.id.txt_title);
            txt_subtitle = view.findViewById(R.id.txt_subtitle);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickHandler == null)
                return;

            int pos = getAdapterPosition();
            Product_unit productunit = productos_unites.get(pos);
            clickHandler.onClick(productunit, this.itemView, pos);
        }

    }

}
