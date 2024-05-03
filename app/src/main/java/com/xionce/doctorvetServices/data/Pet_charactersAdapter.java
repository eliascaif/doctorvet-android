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
import java.util.List;

public class Pet_charactersAdapter extends RecyclerView.Adapter<Pet_charactersAdapter.Mascota_characteresAdapterViewHolder>
        implements DoctorVetApp.IGetIdByValueAdapter, DoctorVetApp.IGetSelectedObject {

    private final ArrayList<Pet_character> pet_characters;
    private HelperClass.AdapterOnClickHandler clickHandler;

    public Pet_charactersAdapter(ArrayList<Pet_character> mascotas_characteres) {
        this.pet_characters = mascotas_characteres;
    }

    @Override
    public Mascota_characteresAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_simple;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new Mascota_characteresAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Mascota_characteresAdapterViewHolder PelajesAdapterViewHolder, int position) {
        Pet_character petcharacter = this.pet_characters.get(position);
        PelajesAdapterViewHolder.txt_title.setText(petcharacter.getName());
    }

    @Override
    public int getItemCount() {
        return pet_characters.size();
    }

    @Override
    public Integer getIdByValue(String value) {
        for (Pet_character petcharacter : pet_characters) {
            if (petcharacter.getName().equals(value))
                return petcharacter.getId();
        }
        return null;
    }
    @Override
    public Object getObjectByName(String name) {
        for (Pet_character r: pet_characters) {
            if (r.getName().equals(name))
                return r;
        }
        return null;
    }

//    public ArrayAdapter<String> getArrayAdapter(Context ctx) {
//        ArrayList<String> lista = new ArrayList<>();
//        for (Pet_character r: pet_characters) {
//            lista.add(r.getName());
//        }
//        return new ArrayAdapter<String>(ctx, android.R.layout.simple_dropdown_item_1line, lista);
//    }
//    public ArrayAdapter<DoctorVetApp.INameIdObject> getClassArrayAdapter(Context ctx) {
//        ArrayList<DoctorVetApp.INameIdObject> list = new ArrayList<>(pet_characters);
//        ArrayAdapter<DoctorVetApp.INameIdObject> arrayAdapter = new ArrayAdapter<>(ctx, android.R.layout.simple_dropdown_item_1line, list);
//        return arrayAdapter;
//    }
    public ArrayAdapter<Pet_character> getArrayAdapter(Context ctx) {
        return new ArrayAdapter<Pet_character>(ctx, android.R.layout.simple_dropdown_item_1line, pet_characters);
    }

    public void setList(ArrayList<Pet_character> list) {
        pet_characters.clear();
        pet_characters.addAll(list);
        notifyDataSetChanged();
    }
    public void setOnClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }
    private void updateList(ArrayList<Pet_character> filterList) {
        pet_characters.clear();
        pet_characters.addAll(filterList);
        notifyDataSetChanged();
    }
    public static ArrayList<Pet_character> searchCaracteres(String userInput, List<Pet_character> mascota_characteres) {
        ArrayList<Pet_character> filterList = new ArrayList<>();
        String name;

        userInput = HelperClass.normalizeString(userInput);

        for (Pet_character petcharacter : mascota_characteres) {
            name = HelperClass.normalizeString(petcharacter.getName());
            if (name.contains(userInput)) {
                filterList.add(petcharacter);
            }
        }
        return filterList;
    }

    public class Mascota_characteresAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView txt_title;

        public Mascota_characteresAdapterViewHolder(View view) {
            super(view);
            txt_title = view.findViewById(R.id.txt_title);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler != null) {
                int pos = getAdapterPosition();
                Pet_character petcharacter = pet_characters.get(pos);
                clickHandler.onClick(petcharacter, this.itemView, pos);
            }
        }

    }

}
