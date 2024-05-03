package com.xionce.doctorvetServices.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.xionce.doctorvetServices.DoctorVetApp;
import com.xionce.doctorvetServices.R;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.util.ArrayList;

public class VetsStructuresAdapter extends RecyclerView.Adapter<VetsStructuresAdapter.VetsStructuresAdapterViewHolder>
        implements DoctorVetApp.IGetIdByValueAdapter, DoctorVetApp.IGetSelectedObject {

    private final ArrayList<Vet_structure> vetStructures;
    private HelperClass.AdapterOnClickHandler clickHandler;

    public VetsStructuresAdapter(ArrayList<Vet_structure> vetStructures) {
        this.vetStructures = vetStructures;
    }

    @Override
    public VetsStructuresAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_simple;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new VetsStructuresAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VetsStructuresAdapterViewHolder VetsStructuresAdapterViewHolder, int position) {
        Vet_structure treatment = this.vetStructures.get(position);
        VetsStructuresAdapterViewHolder.txt_title.setText(treatment.getVet_issued_name());
    }

    @Override
    public int getItemCount() {
        return vetStructures.size();
    }

    @Override
    public Integer getIdByValue(String value) {
        for (Vet_structure treatment : vetStructures) {
            if (treatment.getVet_issued_name().equals(value))
                return treatment.getId();
        }
        return null;
    }
    @Override
    public Object getObjectByName(String name) {
        for (Vet_structure r: vetStructures) {
            if (r.getVet_issued_name().equals(name))
                return r;
        }
        return null;
    }

    public ArrayAdapter<String> getArrayAdapter(Context ctx) {
        ArrayList<String> lista = new ArrayList<>();
        for (Vet_structure r: vetStructures) {
            lista.add(r.getVet_issued_name());
        }
        return new ArrayAdapter<String>(ctx, android.R.layout.simple_dropdown_item_1line, lista);
    }

    public void setOnClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }
    public void addItems(ArrayList<Vet_structure> vetStructures) {
        this.vetStructures.addAll(vetStructures);
        notifyDataSetChanged();
    }

    public class VetsStructuresAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView txt_title;

        public VetsStructuresAdapterViewHolder(View view) {
            super(view);
            txt_title = view.findViewById(R.id.txt_title);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler == null)
                return;

            int pos = getAdapterPosition();
            Vet_structure vetStructure = vetStructures.get(pos);
            clickHandler.onClick(vetStructure, this.itemView, pos);
        }

    }

}
