package com.xionce.doctorvetServices.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xionce.doctorvetServices.DoctorVetApp;
import com.xionce.doctorvetServices.R;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.util.ArrayList;

public class SymptomsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements DoctorVetApp.IGetSelectedObject {

    private final ArrayList<Symptom> symptoms;
    private final DoctorVetApp.AdapterSelectTypes adapterType;
    private HelperClass.AdapterOnClickHandler clickHandler;
    private HelperClass.AdapterOnCancelClickHandler removeClickHandler;

    public SymptomsAdapter(ArrayList<Symptom> symptoms, DoctorVetApp.AdapterSelectTypes adapterType) {
        this.symptoms = symptoms;
        this.adapterType = adapterType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch (adapterType) {
            case NORMAL:
                View view_extended = inflater.inflate(R.layout.list_item_simple, viewGroup, false);
                return new NormalHolder(view_extended);
            case REMOVE:
                View view_compact = inflater.inflate(R.layout.list_item_remove, viewGroup, false);
                return new RemoveHolder(view_compact);
            default:
                throw new RuntimeException("non-existent viewType");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Symptom symptom = symptoms.get(position);
        switch (adapterType) {
            case NORMAL:
                bindNormal(symptom, (NormalHolder)holder, position);
                break;
            case REMOVE:
                bindRemove(symptom, (RemoveHolder)holder, position);
                break;
        }
    }

    private void bindNormal(Symptom symptom, NormalHolder holder, int position) {
        holder.txt_title.setText(symptom.getName());
    }
    private void bindRemove(Symptom symptom, RemoveHolder holder, int position) {
        holder.txt_item_name.setText(symptom.getName());
    }

    @Override
    public int getItemCount() {
        return symptoms.size();
    }

    @Override
    public Object getObjectByName(String name) {
        for (Symptom r: symptoms) {
            if (r.getName().equals(name))
                return r;
        }
        return null;
    }

    public ArrayAdapter<Symptom> getArrayAdapter(Context ctx) {
        return new ArrayAdapter<Symptom>(ctx, android.R.layout.simple_dropdown_item_1line, symptoms);
    }
    public boolean addSymptom(Symptom symptom) {
        if (!existsByName(symptom.getName())) {
            symptoms.add(symptom);
            notifyDataSetChanged();
            return true;
        }

        return false;
    }
    public boolean existsByName(String name) {
        for (Symptom symptom:symptoms)
            if (symptom.getName().equalsIgnoreCase(name))
                return true;

        return false;
    }
    public ArrayList<Symptom> getArrayList() {
        return symptoms;
    }
    public void setOnClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }
    public void setOnRemoveClickHandler(HelperClass.AdapterOnCancelClickHandler removeClickHandler) {
        this.removeClickHandler = removeClickHandler;
    }
    public void addItems(ArrayList<Symptom> symptoms) {
        this.symptoms.addAll(symptoms);
        notifyDataSetChanged();
    }
    public void remove(Symptom symptom) {
        symptoms.remove(symptom);
        notifyDataSetChanged();
    }

    public class NormalHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView txt_title;
        private final TextView txt_subtitle;

        public NormalHolder(View view) {
            super(view);
            txt_title = view.findViewById(R.id.txt_title);
            txt_subtitle = view.findViewById(R.id.txt_subtitle);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickHandler == null)
                return;

            int pos = getAdapterPosition();
            Symptom symptom = symptoms.get(pos);
            clickHandler.onClick(symptom, this.itemView, pos);
        }
    }
    public class RemoveHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView txt_item_name;
        private final ImageView imgRemove;

        public RemoveHolder(View view) {
            super(view);
            txt_item_name = view.findViewById(R.id.txt_item_name);
            view.setOnClickListener(this);
            imgRemove = view.findViewById(R.id.img_remove);
            imgRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (removeClickHandler == null)
                        return;

                    int pos = getAdapterPosition();
                    Symptom symptom = symptoms.get(pos);
                    removeClickHandler.onCancelClick(symptom, imgRemove, pos);
                }
            });
        }

        @Override
        public void onClick(View view) {
            if (clickHandler == null)
                return;

            int pos = getAdapterPosition();
            Symptom symptom = symptoms.get(pos);
            clickHandler.onClick(symptom, this.itemView, pos);
        }
    }

}
