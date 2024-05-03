package com.xionce.doctorvetServices.data;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xionce.doctorvetServices.DoctorVetApp;
import com.xionce.doctorvetServices.R;

import java.util.ArrayList;
import java.util.List;

public class ProductsAutocompleteAdapter extends ArrayAdapter<Product> {

    private final List<Product> products;

    public ProductsAutocompleteAdapter(@NonNull Context context, @NonNull List<Product> products) {
        super(context, 0, products);
        this.products = new ArrayList<>(products);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return productosFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_min, parent, false);
        }

        TextView txt_title = convertView.findViewById(R.id.txt_title);
        TextView txt_subtitle = convertView.findViewById(R.id.txt_subtitle);
        txt_subtitle.setVisibility(View.GONE);
        ImageView img_thumb = convertView.findViewById(R.id.img_thumb);

        Product product = getItem(position);

        if (product != null) {
            String product_name = product.getName();
            if (product.getVet_issued_name() != null)
                product_name = product.getVet_issued_name()  + " (" + product.getName() + ")";
            txt_title.setText(product_name);

            DoctorVetApp.get().setThumb(product.getThumb_url(), img_thumb, R.drawable.ic_product_light);
        }

        return convertView;
    }

    private final Filter productosFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Product> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(products);
            } else {
                String userInput = constraint.toString().toLowerCase().trim();
                suggestions.addAll(ProductsAdapter.searchProductos(userInput, products, null));
            }

            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values == null) return;

            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((Product) resultValue).getName();
        }
    };

//    public boolean exists(String product_name) {
//        for (Product product : products) {
//            if (product.getName().equals(product_name))
//                return true;
//        }
//
//        return false;
//    }
}

