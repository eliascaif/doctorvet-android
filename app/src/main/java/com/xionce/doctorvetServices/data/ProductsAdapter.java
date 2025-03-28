package com.xionce.doctorvetServices.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.xionce.doctorvetServices.DoctorVetApp;
import com.xionce.doctorvetServices.R;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public enum Products_types { NORMAL, SUPPLY, SELECTION, SEARCH, SERVICES_ASSOC }

    private HelperClass.AdapterOnClickHandler clickHandler;
    private HelperClass.AdapterOnSelectClickHandler selectClickHandler;
    private final Products_types adapter_type;
    private final ArrayList<Product> products;
    private final SparseBooleanArray selectedProducts = new SparseBooleanArray();

    public ProductsAdapter(ArrayList<Product> products, Products_types adapterType) {
        this.products = products;
        this.adapter_type = adapterType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view;

        Products_types producto_view_type = Products_types.values()[viewType];
        switch (producto_view_type) {
            case NORMAL:
                view = inflater.inflate(R.layout.list_item_product, viewGroup, false);
                return new ProductsHolder(view);
            case SUPPLY:
                view = inflater.inflate(R.layout.list_item_min, viewGroup, false);
                return new ProductsHolder(view);
            case SELECTION:
                view = inflater.inflate(R.layout.list_item_seleccion, viewGroup, false);
                return new ProductsSelectionHolder(view);
            case SEARCH:
                view = inflater.inflate(R.layout.list_item_product_search, viewGroup, false);
                return new ProductsSearchHolder(view);
            case SERVICES_ASSOC:
                view = inflater.inflate(R.layout.list_item_product_assoc, viewGroup, false);
                return new ProductsAssocHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Product product = products.get(position);

        switch (adapter_type) {
            case NORMAL:
            case SUPPLY:
                bindNormal(product, (ProductsHolder)holder);
                break;
            case SELECTION:
                bindSelection(product, (ProductsSelectionHolder)holder);
                break;
            case SEARCH:
                bindSearch(product, (ProductsSearchHolder)holder);
                break;
            case SERVICES_ASSOC:
                bindServicesAssoc(product, (ProductsAssocHolder)holder);
                break;
        }
    }

    private void bindNormal(Product product, ProductsHolder holder) {
        String product_name = product.getName();

        if (product.getVet_issued_name() != null)
            product_name = product.getVet_issued_name()  + " (" + product.getName() + ")";

        holder.txt_title.setText(product_name);
        holder.txt_subtitle.setText(product.getCategoriesNames());

        DoctorVetApp.get().setThumb(product.getThumb_url(), holder.img_image, R.drawable.ic_product_light);

        //quantity - avoid blink
        if (product.getIs_service() != null && product.getIs_service()) {
            holder.txt_quantity.setText("");
            //holder.txt_quantity.setVisibility(View.GONE);
        } else {
            holder.txt_quantity.setText("Cantidad: " + product.getPrettyQuantity());
            //holder.txt_quantity.setVisibility(View.VISIBLE);
        }

        //avoid blink
        if (product.hasPrices()) {
            holder.txt_prices.setText("Precios: " + product.getFormattedPrices());
            //holder.txt_prices.setVisibility(View.VISIBLE);
        } else {
            holder.txt_prices.setText("");
            //holder.txt_prices.setVisibility(View.GONE);
        }
    }
    private void bindSearch(Product product, ProductsSearchHolder holder) {
        String product_name = product.getName();

        if (product.getVet_issued_name() != null)
            product_name = product.getVet_issued_name()  + " (" + product.getName() + ")";

        holder.txt_name.setText(product_name);
        holder.txt_categorias.setText(product.getCategoriesNames());

        DoctorVetApp.get().setThumb(product.getThumb_url(), holder.img_thumb, R.drawable.ic_product_light);

        holder.txt_prices.setText("");
        //holder.txt_prices.setVisibility(View.GONE);

        holder.txt_quantity.setText("");
        holder.txt_quantity.setVisibility(View.GONE);
        //holder.txt_quantity_description.setVisibility(View.GONE);

        if (product.getIsAssociate_with_vet() != null && product.getIsAssociate_with_vet() == 1) {
            if (!product.getIs_service()) {
                //holder.txt_quantity.setText("Cant.: " + product.getPrettyQuantity());
                holder.txt_quantity.setText("Cantidad: " + product.getQuantity_string());
                holder.txt_quantity.setVisibility(View.VISIBLE);

                //quantity detail
//                if (product.getQuantity_detail() != null) {
//                    holder.txt_quantity_description.setVisibility(View.VISIBLE);
//                    holder.txt_quantity_description.setText(product.getQuantityDetailDescription());
//                }
            }

            if (product.hasPrices()) {
                holder.txt_prices.setText("Precios: " + product.getFormattedPrices());
                holder.txt_prices.setVisibility(View.VISIBLE);
            }
        } else {
            holder.txt_quantity.setVisibility(View.VISIBLE);
            holder.txt_quantity.setText("No asociado");
        }
    }
    private void bindSelection(Product product, ProductsSelectionHolder holder) {
        String product_name = product.getName();
        if (product.getVet_issued_name() != null)
            product_name = product.getVet_issued_name()  + " (" + product.getName() + ")";
        holder.txt_title.setText(product_name);
//        holder.txt_title.setText(product.getName());
        holder.txt_subtitle.setText(product.getCategoriesNames());

        DoctorVetApp.get().setThumb(product.getThumb_url(), holder.img_image, R.drawable.ic_product_light);

        if (product.getIsAssociate_with_vet() == 1) {
            holder.txt_third_line.setText("Asociado");
        } else {
            holder.txt_third_line.setText("No asociado");
        }

        //seleccion
        if (selectedProducts.get(product.getId())) {
            holder.lyt_constraint_layout.setBackgroundColor(Color.GRAY);
            //holder.lyt_constraint_layout.setActivated(true);
            //holder.itemView.setActivated(true);// .setBackgroundColor(Color.BLACK);
        } else {
            holder.lyt_constraint_layout.setBackgroundColor(Color.TRANSPARENT);
        }

    }
    private void bindServicesAssoc(Product product, ProductsAssocHolder holder) {
        holder.txt_name.setText(product.getName());

        holder.checkBox.setChecked(false);
        if (product.getService_assoc() == 1)
            holder.checkBox.setChecked(true);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    @Override
    public int getItemViewType(int position) {
       return adapter_type.ordinal();
    }

    public ProductsAutocompleteAdapter getAutocompleteAdapter(Context ctx) {
        return new ProductsAutocompleteAdapter(ctx, new ArrayList<>(products));
    }

    public boolean exists(String product_name) {
        for (Product product : products) {
            if (product.getName().equals(product_name))
                return true;
        }

        return false;
    }

    public void addItem(Product product) {
        if (exists(product.getName()))
            return;

        int pos = 0;
        String name = product.getName();
        Collator myCollator = Collator.getInstance();

        for (Product p: products) {
            if(myCollator.compare(name, p.getName()) < 0) break;
            pos++;
        }

        products.add(pos, product);
        notifyItemInserted(pos);
    }

    public void setSelectAll() {
        for (Product product : products) {
            if (selectedProducts.get(product.getId())) {
                selectedProducts.delete(product.getId());
            } else {
                selectedProducts.put(product.getId(), true);
            }
        }
    }

    public ArrayList<Integer> getSelectedProductsArray() {
        ArrayList<Integer> selected_products = new ArrayList<>();
        for(int i = 0; i < selectedProducts.size(); i++) {
            Integer id_product = selectedProducts.keyAt(i);
            selected_products.add(id_product);
        }
        return selected_products;
    }

    public void addItems(ArrayList<Product> items) {
        this.products.addAll(items);
        notifyDataSetChanged();
    }

    //para la asociacion de productos
    public void selectItem(Integer id_product) {
        if (selectedProducts.get(id_product)) {
            selectedProducts.delete(id_product);
        } else {
            selectedProducts.append(id_product, true);
        }
    }
    public void setList(ArrayList<Product> list) {
        products.clear();
        products.addAll(list);
        notifyDataSetChanged();
    }
    public void setOnClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }
    public void setOnSelectClickHandler(HelperClass.AdapterOnSelectClickHandler selectClickHandler) {
        this.selectClickHandler = selectClickHandler;
    }
    public ArrayList<Product> getArrayList() {
        return products;
    }
    public ArrayList<Product> getProductos_by_id_fabricante(Integer id_fabricante) {
        ArrayList<Product> lista_by_fabricante = new ArrayList<>();
        Integer id_fabricante_aux;
        for (Product p: products) {
            id_fabricante_aux = p.getId_manufacturer();
            if (id_fabricante_aux != null && id_fabricante_aux.equals(id_fabricante))
                lista_by_fabricante.add(p);
        }

        return lista_by_fabricante;
    }
    public int getcantidadProductosSeleccionados() {
        return selectedProducts.size();
    }

    public static ArrayList<Product> searchProductos(String userInput, List<Product> products, @Nullable Integer category_id) {
        ArrayList<Product> filterList = new ArrayList<>();
        ArrayList<Product> finalList = new ArrayList<>();

        userInput = userInput.trim();
        userInput = HelperClass.normalizeString(userInput);

        //category filter
//        if (category_id != null) {
//            HashMap<Integer, String> categorias;
//            for (Product p: products) {
//                categorias = p.getCategories_hashmap();
//                for (Integer id_cat:categorias.keySet()) {
//                    if (id_categoria.equals(id_cat))
//                        filterList.add(p);
//                }
//            }
//        } else {
//            filterList = (ArrayList<Product>) products;
//        }
        filterList = (ArrayList<Product>) products;

        //busqueda
        String codigo_barras;
        String name;

        String[] userInputs = userInput.split(" ");

        for (Product product : filterList) {
            //barcode search
            codigo_barras = product.getBar_code();
            if (codigo_barras != null && codigo_barras.toLowerCase().contains(userInputs[0])) {
                finalList.add(product);
                continue;
            }

            //name search
            name = HelperClass.normalizeString(product.getName());
            boolean matchAll = true;
            for (String input:userInputs) {
                if (!name.contains(input)) {
                    matchAll = false;
                    break;
                }
            }
            if (matchAll)
                finalList.add(product);
        }

        return finalList;
    }

    public class ProductsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView img_image;
        private final TextView txt_title;
        private final TextView txt_subtitle;
        private final TextView txt_quantity;
        private final TextView txt_prices;

        public ProductsHolder(View view) {
            super(view);
            img_image = view.findViewById(R.id.img_thumb);
            txt_title = view.findViewById(R.id.txt_title);
            txt_subtitle = view.findViewById(R.id.txt_subtitle);
            txt_quantity = view.findViewById(R.id.txt_quantity);
            txt_prices = view.findViewById(R.id.txt_prices);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickHandler == null)
                return;

            int pos = getAdapterPosition();
            Product product = products.get(pos);
            clickHandler.onClick(product, view, pos);
        }
    }
    public class ProductsSelectionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView img_image;
        private final TextView txt_title;
        private final TextView txt_subtitle;
        private final ImageView img_selection;
        private final View lyt_constraint_layout;
        private final TextView txt_third_line;

        public ProductsSelectionHolder(View view) {
            super(view);
            img_image = view.findViewById(R.id.img_thumb);
            txt_title = view.findViewById(R.id.txt_title);
            txt_subtitle = view.findViewById(R.id.txt_subtitle);
            img_selection = view.findViewById(R.id.img_ok_cancel);
            lyt_constraint_layout = view.findViewById(R.id.lyt_constraint_layout);
            txt_third_line = view.findViewById(R.id.txt_third_line);
            view.setOnClickListener(this);

            img_selection.setVisibility(View.VISIBLE);
            img_selection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectClickHandler == null)
                        return;

                    int adapterPos = getAdapterPosition();
                    selectClickHandler.onSelectClick(products.get(adapterPos), img_selection, adapterPos);
                    //notifyItemChanged(adapterPos);
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onClick(View view) {
            if (clickHandler == null)
                return;

            int pos = getAdapterPosition();
            Product product = products.get(pos);
            clickHandler.onClick(product, view, pos);
        }
    }
    public class ProductsSearchHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView img_thumb;
        private final TextView txt_name;
        private final TextView txt_categorias;
        private final TextView txt_quantity;
        private final TextView txt_prices;

        public ProductsSearchHolder(View view) {
            super(view);
            img_thumb = view.findViewById(R.id.img_thumb);
            txt_name = view.findViewById(R.id.txt_title);
            txt_categorias = view.findViewById(R.id.txt_subtitle);
            txt_quantity = view.findViewById(R.id.txt_quantity);
            txt_prices = view.findViewById(R.id.txt_prices);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler == null)
                return;

            int pos = getAdapterPosition();
            Product product = products.get(pos);
            clickHandler.onClick(product, this.itemView, pos);
        }
    }
    public class ProductsAssocHolder extends RecyclerView.ViewHolder {
        private final CheckBox checkBox;
        private final TextView txt_name;

        public ProductsAssocHolder(View view) {
            super(view);
            checkBox = view.findViewById(R.id.checkbox);
            txt_name = view.findViewById(R.id.txt_title);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    int pos = getAdapterPosition();
                    products.get(pos).setService_assoc(0);
                    if (b)
                        products.get(pos).setService_assoc(1);
                }
            });
        }
    }
}