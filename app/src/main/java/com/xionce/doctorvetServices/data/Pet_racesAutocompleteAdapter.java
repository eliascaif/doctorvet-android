package com.xionce.doctorvetServices.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.xionce.doctorvetServices.R;

import java.util.ArrayList;
import java.util.List;

public class Pet_racesAutocompleteAdapter extends ArrayAdapter<Pet_race> {

    private final List<Pet_race> petraces;

    public Pet_racesAutocompleteAdapter(@NonNull Context context, @NonNull List<Pet_race> petraces) {
        super(context, 0, petraces);
        this.petraces = new ArrayList<>(petraces);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return mascota_racesFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_med_fixed, parent, false
            );
        }

        TextView txt_info = convertView.findViewById(R.id.txt_title);
        TextView txt_info2 = convertView.findViewById(R.id.txt_subtitle);
        txt_info2.setVisibility(View.GONE);
        ImageView img_thumb = convertView.findViewById(R.id.img_thumb);

        Pet_race petrace = getItem(position);

        if (petrace != null) {
            txt_info.setText(petrace.getName());

            String thumb_url = petrace.getThumb_url();
            if (thumb_url != null) {
                Glide.with(img_thumb.getContext()).load(thumb_url).apply(RequestOptions.circleCropTransform()).apply(RequestOptions.placeholderOf(R.drawable.ic_dog)).into(img_thumb);
            } else {
                Glide.with(img_thumb.getContext()).load(R.drawable.ic_dog).apply(RequestOptions.circleCropTransform()).into(img_thumb);
            }
        }

        return convertView;
    }

    private final Filter mascota_racesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Pet_race> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(petraces);
            } else {
                String userInput = constraint.toString().toLowerCase().trim();
                suggestions.addAll(Pet_racesAdapter.searchRazas(userInput, petraces));
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
            return ((Pet_race) resultValue).getName();
        }
    };

}

