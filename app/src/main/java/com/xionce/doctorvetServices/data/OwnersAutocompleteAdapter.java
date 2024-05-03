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

public class OwnersAutocompleteAdapter extends ArrayAdapter<Owner> {

    private final List<Owner> owners;

    public OwnersAutocompleteAdapter(@NonNull Context context, @NonNull List<Owner> owners) {
        super(context, 0, owners);
        this.owners = new ArrayList<>(owners);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return ownersFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_med, parent, false
            );
        }

        TextView txt_info = convertView.findViewById(R.id.txt_title);
        TextView txt_info2 = convertView.findViewById(R.id.txt_subtitle);
        ImageView img_thumb = convertView.findViewById(R.id.img_thumb);

        Owner owner = getItem(position);

        if (owner != null) {
            txt_info.setText(owner.getName());
            txt_info2.setText(owner.getEmailTelefono());

            DoctorVetApp.get().setThumb(owner.getThumb_url(), img_thumb, R.drawable.ic_account_circle_light);
        }

        return convertView;
    }

    private final Filter ownersFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Owner> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(owners);
            } else {
                String userInput = constraint.toString().toLowerCase().trim();
                suggestions.addAll(OwnersAdapter.searchOwners(userInput, owners));
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
            return ((Owner) resultValue).getName();
        }
    };

}

