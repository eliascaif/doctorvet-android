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

public class PetsAutocompleteAdapter extends ArrayAdapter<Pet> {

    private final List<Pet> pets;

    public PetsAutocompleteAdapter(@NonNull Context context, @NonNull List<Pet> pets) {
        super(context, 0, pets);
        this.pets = new ArrayList<>(pets);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return mascotasFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_min, parent, false);
        }

        TextView txt_title = convertView.findViewById(R.id.txt_title);
        TextView txt_subtitle = convertView.findViewById(R.id.txt_subtitle);
        ImageView img_thumb = convertView.findViewById(R.id.img_thumb);

        Pet pet = getItem(position);

        if (pet != null) {
            txt_title.setText(pet.getName());
            txt_subtitle.setText(String.format("%s %s", txt_subtitle.getContext().getString(R.string.de), pet.getOwnersNames()));

            String email = pet.getOwners().get(0).getEmail();
            if (email != null && !email.isEmpty())
                txt_subtitle.setText(String.format("%s %s %s", txt_subtitle.getText(), " - ", email));

            DoctorVetApp.get().setThumb(pet.getThumb_url(), img_thumb, R.drawable.ic_pets_dark);
        }

        return convertView;
    }

    private final Filter mascotasFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Pet> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(pets);
            } else {
                String userInput = constraint.toString().toLowerCase().trim();
                suggestions.addAll(PetsAdapter.searchPets(userInput, pets));
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
            return ((Pet) resultValue).getName();
        }
    };

//    public boolean exists(String mascota_name) {
//        for (Pet pet : pets) {
//            if (pet.getName().equals(mascota_name))
//                return true;
//        }
//
//        return false;
//    }
}

