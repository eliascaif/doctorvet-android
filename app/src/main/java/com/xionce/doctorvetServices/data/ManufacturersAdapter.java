package com.xionce.doctorvetServices.data;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xionce.doctorvetServices.DoctorVetApp;
import com.xionce.doctorvetServices.R;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.controls.CustomGridLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManufacturersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final ArrayList<Product_manufacturer> manufacturers;
    public enum Manufacturers_types {MANUFACTURERS, MANUFACTURERS_PRODUCTS, MANUFACTURERS_PRODUCTS2 }
    private final Manufacturers_types adapterType;

    private HelperClass.AdapterOnClickHandler clickHandler;
    private HelperClass.AdapterOnSelectClickHandler selectClickHandler;
    public RecyclerView recyclerView;

    private SparseIntArray expandedInRecyclerViewManufacturers;
    private HashMap<Integer, ProductsAdapter> hashmap_manufacturers_products = new HashMap<>();

    public ManufacturersAdapter(ArrayList<Product_manufacturer> manufacturers) {
        this.manufacturers = manufacturers;
        this.adapterType = Manufacturers_types.MANUFACTURERS;
    }
    public ManufacturersAdapter(ArrayList<Product_manufacturer> manufacturers, Manufacturers_types adapterType, ProductsAdapter productsAdapter) {
        this.manufacturers = manufacturers;
        this.adapterType = adapterType;

        expandedInRecyclerViewManufacturers = new SparseIntArray(manufacturers.size());

        for (Product_manufacturer manufacturer: manufacturers) {
            expandedInRecyclerViewManufacturers.put(manufacturer.getId(), 0);
            ProductsAdapter productsAdapter_aux = new ProductsAdapter(productsAdapter.getProductos_by_id_fabricante(manufacturer.getId()), ProductsAdapter.Products_types.SELECTION);
            hashmap_manufacturers_products.put(manufacturer.getId(), productsAdapter_aux);
        }
    }
    public ManufacturersAdapter(ArrayList<Product_manufacturer> manufacturers, Manufacturers_types adapterType) {
        this.manufacturers = manufacturers;
        this.adapterType = adapterType;

        expandedInRecyclerViewManufacturers = new SparseIntArray(manufacturers.size());

        for (Product_manufacturer manufacturer: manufacturers) {
            expandedInRecyclerViewManufacturers.put(manufacturer.getId(), 0);
            ProductsAdapter productsAdapter_aux = new ProductsAdapter(manufacturer.getProducts(), ProductsAdapter.Products_types.SELECTION);
            hashmap_manufacturers_products.put(manufacturer.getId(), productsAdapter_aux);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch (adapterType) {
            case MANUFACTURERS:
                View view_simple = inflater.inflate(R.layout.list_item_med, viewGroup, false);
                return new ManufacturersSimpleAdapterViewHolder(view_simple);
            case MANUFACTURERS_PRODUCTS:
            case MANUFACTURERS_PRODUCTS2:
                View view = inflater.inflate(R.layout.list_item_seleccion_ampliar, viewGroup, false);
                return new ManufacturersAdapterViewHolder(view);
            default:
                throw new RuntimeException("non-existent viewType");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Product_manufacturer manufacturer = manufacturers.get(position);
        switch (adapterType) {
            case MANUFACTURERS:
                bindSimple(manufacturer, (ManufacturersSimpleAdapterViewHolder)holder, position);
                break;
            case MANUFACTURERS_PRODUCTS:
            case MANUFACTURERS_PRODUCTS2:
                bind(manufacturer, (ManufacturersAdapterViewHolder)holder, position);
                break;
        }
    }

    private void bind(Product_manufacturer manufacturer, ManufacturersAdapterViewHolder holder, int position) {
        //final Product_manufacturer productmanufacturer = manufacturers.get(position);
        final Integer id_fabricante = manufacturer.getId();

        holder.txt_title.setText(manufacturer.getName());
        holder.txt_subtitle.setText(manufacturer.getWeb_page());

        DoctorVetApp.get().setThumb(manufacturer.getThumb_url(), holder.img_image, R.drawable.ic_factory);

        Context ctx_recycler = holder.recyclerView.getContext();
        CustomGridLayoutManager linearLayoutManager = new CustomGridLayoutManager(ctx_recycler); //, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setScrollEnabled(false);
        holder.recyclerView.setLayoutManager(linearLayoutManager);
        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setAdapter(hashmap_manufacturers_products.get(id_fabricante));

        //para reducir flickeo
        //productosFabricantesAdapterViewHolder.recyclerView.setItemAnimator(null);
        //productosFabricantesAdapterViewHolder.recyclerView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        final int extended = expandedInRecyclerViewManufacturers.get(id_fabricante);
        if (extended == 1) {
            holder.recyclerView.setVisibility(View.VISIBLE);
            holder.img_extended.setImageResource(R.drawable.ic_up);
        } else {
            holder.recyclerView.setVisibility(View.GONE);
            holder.img_extended.setImageResource(R.drawable.ic_down);
        }

        holder.img_extended.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expandedInRecyclerViewManufacturers.get(id_fabricante) == 1) {
                    //collapse
                    ((CustomGridLayoutManager)recyclerView.getLayoutManager()).setScrollEnabled(false);

                    holder.recyclerView.setVisibility(View.GONE);
                    Glide.with(holder.img_extended.getContext()).load(R.drawable.ic_down).into(holder.img_extended);
                    expandedInRecyclerViewManufacturers.append(id_fabricante, 0);
                    holder.extended = false;

                    ((CustomGridLayoutManager)recyclerView.getLayoutManager()).setScrollEnabled(true);
                } else {
                    ((CustomGridLayoutManager)recyclerView.getLayoutManager()).setScrollEnabled(false);

                    holder.recyclerView.setVisibility(View.VISIBLE);
                    Glide.with(holder.img_extended.getContext()).load(R.drawable.ic_up).into(holder.img_extended);
                    expandedInRecyclerViewManufacturers.append(id_fabricante, 1);
                    holder.extended = true;

                    ((CustomGridLayoutManager)recyclerView.getLayoutManager()).setScrollEnabled(true);
                }
            }
        });
    }
    private void bindSimple(Product_manufacturer manufacturer, ManufacturersSimpleAdapterViewHolder holder, int position) {
        holder.txt_title.setText(manufacturer.getName());
        holder.txt_subtitle.setText(manufacturer.getEmail());
        DoctorVetApp.get().setThumb(manufacturer.getThumb_url(), holder.img_thumb, R.drawable.ic_factory);
    }

    @Override
    public int getItemCount() {
        return manufacturers.size();
    }

    public void addItems(ArrayList<Product_manufacturer> items) {
        manufacturers.addAll(items);
        notifyDataSetChanged();
    }

    public void setOnClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    public void setOnSelectClickHandler(HelperClass.AdapterOnSelectClickHandler selectClickHandler) {
        this.selectClickHandler = selectClickHandler;
    }

    public void setProductsAdapterOnClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        for (Map.Entry<Integer, ProductsAdapter> entry: hashmap_manufacturers_products.entrySet()) {
            entry.getValue().setOnClickHandler(clickHandler);
        }
    }
    public void setProductsAdapterOnSelectClickHandler(HelperClass.AdapterOnSelectClickHandler selectClickHandler) {
        for (Map.Entry<Integer, ProductsAdapter> entry: hashmap_manufacturers_products.entrySet()) {
            entry.getValue().setOnSelectClickHandler(selectClickHandler);
        }
    }

    public void setSelectedProduct_by_id_manufacturer(Integer id_manufacturer, int pos) {
        ProductsAdapter productsAdapter = hashmap_manufacturers_products.get(id_manufacturer);
        productsAdapter.setSelectAll();
        notifyItemChanged(pos);
    }
    public void setSelectProduct(Integer id_product, @Nullable Integer pos) {
        for (Map.Entry<Integer, ProductsAdapter> entry: hashmap_manufacturers_products.entrySet()) {
            for (Product product :entry.getValue().getArrayList()) {
                if (product.getId().equals(id_product)) {
                    entry.getValue().selectItem(id_product);
                    if (pos != null)
                        notifyItemChanged(pos);
                }
            }
        }
    }

    public int getSelectedProductsQuantity() {
        int cantidad = 0;
        for (Map.Entry<Integer, ProductsAdapter> entry: hashmap_manufacturers_products.entrySet()) {
            cantidad += entry.getValue().getcantidadProductosSeleccionados();
        }
        return cantidad;
    }
    public ArrayList<Integer> getSelectedProductsArray() {
        ArrayList<Integer> productos_seleccionados = new ArrayList<>();
        for (Map.Entry<Integer, ProductsAdapter> entry: hashmap_manufacturers_products.entrySet()) {
            productos_seleccionados.addAll(entry.getValue().getSelectedProductsArray());
        }
        return productos_seleccionados;
    }

    public class ManufacturersSimpleAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView txt_title;
        private final TextView txt_subtitle;
        private final ImageView img_thumb;

        public ManufacturersSimpleAdapterViewHolder(View view) {
            super(view);
            txt_title = view.findViewById(R.id.txt_title);
            txt_subtitle = view.findViewById(R.id.txt_subtitle);
            img_thumb = view.findViewById(R.id.img_thumb);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler == null)
                return;

            int pos = getAdapterPosition();
            Product_manufacturer manufacturer = manufacturers.get(pos);
            clickHandler.onClick(manufacturer, this.itemView, pos);
        }

    }
    public class ManufacturersAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView img_image;
        public final TextView txt_title;
        public final TextView txt_subtitle;
        public final ImageView img_ok_cancel;
        public final ImageView img_extended;
        public final RecyclerView recyclerView;
        public boolean extended = false;

        public ManufacturersAdapterViewHolder(View view) {
            super(view);
            img_image = view.findViewById(R.id.img_thumb);
            txt_title = view.findViewById(R.id.txt_title);
            txt_subtitle = view.findViewById(R.id.txt_subtitle);
            img_extended = view.findViewById(R.id.img_ampliar);
            recyclerView = view.findViewById(R.id.recyclerview);
            view.setOnClickListener(this);

            img_ok_cancel = view.findViewById(R.id.img_ok_cancel);
            if (selectClickHandler != null) {
                //img_ok_cancel.setVisibility(View.VISIBLE);
                img_ok_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectClickHandler.onSelectClick(manufacturers.get(getAdapterPosition()), img_ok_cancel, getAdapterPosition()/*, ampliado*/);
                    }
                });
            }
        }

        @Override
        public void onClick(View view) {
            if (clickHandler == null)
                return;

            int pos = getAdapterPosition();
            Product_manufacturer productmanufacturer = manufacturers.get(pos);
            clickHandler.onClick(productmanufacturer, view, pos);
        }
    }

}