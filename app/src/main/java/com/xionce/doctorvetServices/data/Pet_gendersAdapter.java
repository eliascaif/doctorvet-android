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

public class Pet_gendersAdapter extends RecyclerView.Adapter<Pet_gendersAdapter.Mascota_gendersAdapterViewHolder>
        implements DoctorVetApp.IGetIdByValueAdapter, DoctorVetApp.IGetSelectedObject {

    private final ArrayList<Pet_gender> petgenders;
    private HelperClass.AdapterOnClickHandler clickHandler;

    public Pet_gendersAdapter(ArrayList<Pet_gender> petgenders) {
        this.petgenders = petgenders;
    }

    @Override
    public Mascota_gendersAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_simple;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new Pet_gendersAdapter.Mascota_gendersAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Mascota_gendersAdapterViewHolder Mascota_gendersAdapterViewHolder, int position) {
        Pet_gender petgender = petgenders.get(position);
        Mascota_gendersAdapterViewHolder.txt_title.setText(petgender.getName());
    }

    @Override
    public int getItemCount() {
        return petgenders.size();
    }

    public void setOnClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }
    public ArrayAdapter<Pet_gender> getArrayAdapter(Context ctx) {
        ArrayAdapter<Pet_gender> arrayAdapter = new ArrayAdapter<Pet_gender>(ctx, android.R.layout.simple_dropdown_item_1line, petgenders);
        return arrayAdapter;
    }

    public void setList(ArrayList<Pet_gender> list) {
        petgenders.clear();
        petgenders.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public Integer getIdByValue(String value) {
        for (Pet_gender petgender : petgenders) {
            if (petgender.getName().equals(value))
                return petgender.getId();
        }
        return null;
    }
    @Override
    public Object getObjectByName(String name) {
        for (Pet_gender r: petgenders) {
            if (r.getName().equals(name))
                return r;
        }
        return null;
    }

    public class Mascota_gendersAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView txt_title;

        public Mascota_gendersAdapterViewHolder(View view) {
            super(view);
            txt_title = view.findViewById(R.id.txt_title);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler == null)
                return;

            int pos = getAdapterPosition();
            Pet_gender petgender = petgenders.get(pos);
            clickHandler.onClick(petgender, this.itemView, pos);
        }

    }

}
