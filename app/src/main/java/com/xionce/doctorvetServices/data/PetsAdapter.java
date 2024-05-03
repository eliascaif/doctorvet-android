package com.xionce.doctorvetServices.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xionce.doctorvetServices.DoctorVetApp;
import com.xionce.doctorvetServices.R;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class PetsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<Pet> pets;
    private final DoctorVetApp.Adapter_types adapterType;
    private HelperClass.AdapterOnClickHandler clickHandler;

    public PetsAdapter(ArrayList<Pet> pets, DoctorVetApp.Adapter_types adapterType) {
        this.pets = pets;
        this.adapterType = adapterType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch (adapterType) {
            case EXTENDED:
            case COMPACT:
                View view = inflater.inflate(R.layout.list_item_pet, viewGroup, false);
                return new ExtendedPetsHolder(view);
            case SEARCH:
                View view_search = inflater.inflate(R.layout.list_item_med, viewGroup, false);
                return new SearchPetsAdapterViewHolder(view_search);
            default:
                throw new RuntimeException("non-existent viewType");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Pet pet = pets.get(position);
        switch (adapterType) {
            case EXTENDED:
            case COMPACT:
                bindExtended(pet, (ExtendedPetsHolder)holder, position);
                break;
            case SEARCH:
                bindSearch(pet, (SearchPetsAdapterViewHolder)holder/*, position*/);
                break;
        }
    }

    private void bindExtended(Pet pet, ExtendedPetsHolder holder, int position) {
        DoctorVetApp.get().setThumb(pet.getThumb_url(), holder.img_thumb, R.drawable.ic_dog);

        holder.txt_name.setText(pet.getName());

        //ultima visita
        DoctorVetApp.get().setLastVisit(pet.getLast_visit(), holder.txt_last_visit);

        //DoctorVetApp.getInstance().fillOwnersInLinear(ctx, pet.getOwners(), holder.linear_owners);
        holder.txt_owners.setText("De: " + pet.getOwnersNames());

        holder.txt_reason.setText(DoctorVetApp.get().reasonToString(pet.getReason()));

        if (DoctorVetApp.get().getVet().getMultiuser() > 1 && pet.getUser() != null) {
            holder.txt_user.setVisibility(View.VISIBLE);
            holder.txt_user.setText(pet.getUser().getName());
        }

        //if (position == getItemCount() - 1) {
        //    holder.div_line.setVisibility(View.GONE);
        //}
    }
    private void bindSearch(Pet pet, SearchPetsAdapterViewHolder holder/*, int position*/) {
        holder.txt_name.setText(pet.getName());
        holder.txt_owners.setText("De: " + pet.getOwnersNames());
        DoctorVetApp.get().setThumb(pet.getThumb_url(), holder.img_thumb, R.drawable.ic_dog);
    }

    @Override
    public int getItemCount() {
        return pets.size();
    }

    public ArrayList<Pet> getArrayList() {
        return pets;
    }
    public PetsAutocompleteAdapter getAutocompleteAdapter(Context ctx) {
        //return new PetsAutocompleteAdapter(ctx, new ArrayList<>(mascotas_src));
        return new PetsAutocompleteAdapter(ctx, new ArrayList<>(pets));
    }
//    public HashMap<Integer, String> getIdNameHashMap() {
//        LinkedHashMap<Integer, String> id_name = new LinkedHashMap<>();
//        for (Pet m: pets) {
//            id_name.put(m.getId(), m.getName()); // p.getName(), p.getId());
//        }
//        return id_name;
//    }
    public void setOnClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }
    public void addItem(Pet pet) {
        int pos = 0;
        String name = pet.getName();
        Collator myCollator = Collator.getInstance();

        for (Pet m: pets) {
            if(myCollator.compare(name, m.getName()) < 0) break;
            pos++;
        }

        pets.add(pos, pet);
        notifyItemInserted(pos);
    }
    public void insertItem(Pet pet) {
        pets.add(0, pet);
    }
    public void replaceRecent(Pet pet) {
        if (exists(pet.getId()))
            updateItem(pet);
        else
            insertItem(pet);
    }
    public void updateItem(Pet pet) {
        int pos = 0;
        Integer id = pet.getId();
        for (Pet m: pets) {
            if (id.equals(m.getId())) {
                pets.set(pos, pet);
                notifyItemChanged(pos);
                break;
            }
            pos++;
        }
    }
    public void setList(ArrayList<Pet> list) {
        pets.clear();
        pets.addAll(list);
        notifyDataSetChanged();
    }
    public Boolean exists(Integer id_pet) {
        for (Pet m: pets) {
            if (id_pet.equals(m.getId()))
                return true;
        }

        return false;
    }
    public static ArrayList<Pet> searchPets(String userInput, List<Pet> pets) {
        ArrayList<Pet> filterList = new ArrayList<>();
        String name;
        String chip;

        userInput = HelperClass.normalizeString(userInput);

        for (Pet pet : pets) {
            name = HelperClass.normalizeString(pet.getName());
            if (name.contains(userInput)) {
                filterList.add(pet);
                continue;
            }
            chip = pet.getChip();
            if (chip != null && chip.toLowerCase().contains(userInput)) {
                filterList.add(pet);
                continue;
            }
            for (Owner owner : pet.getOwners()) {  //getOwners_hashmap().values()) {
                if (HelperClass.normalizeString(owner.getName()).contains(userInput)) {
                    filterList.add(pet);
                }
            }
        }
        return filterList;
    }
    public void addItems(ArrayList<Pet> items) {
        pets.addAll(items);
        notifyDataSetChanged();
    }

    public class ExtendedPetsHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public final TextView txt_name;
        public final TextView txt_last_visit;
        public final ImageView img_thumb;
        private final TextView txt_owners;
        private final TextView txt_reason;
        private final TextView txt_user;

        public ExtendedPetsHolder(View view) {
            super(view);
            txt_name = view.findViewById(R.id.txt_pet_name);
            txt_last_visit = view.findViewById(R.id.txt_last_visit);
            img_thumb = view.findViewById(R.id.img_thumb);
            txt_owners = view.findViewById(R.id.txt_pet_owners);
            txt_reason = view.findViewById(R.id.txt_reason);
            txt_user = view.findViewById(R.id.txt_user);
            //div_line = view.findViewById(R.id.div_line);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler == null)
                return;

            int pos = getAdapterPosition();
            Pet pet = pets.get(pos);
            clickHandler.onClick(pet, this.itemView, pos);
        }
    }
    public class SearchPetsAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public ImageView img_thumb;
        public TextView txt_name;
        public TextView txt_owners;

        public SearchPetsAdapterViewHolder(View view) {
            super(view);
            img_thumb = view.findViewById(R.id.img_thumb);
            txt_name = view.findViewById(R.id.txt_title);
            txt_owners = view.findViewById(R.id.txt_subtitle);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler == null)
                return;

            int pos = getAdapterPosition();
            Pet pet = pets.get(pos);
            clickHandler.onClick(pet, this.itemView, pos);
        }
    }

}