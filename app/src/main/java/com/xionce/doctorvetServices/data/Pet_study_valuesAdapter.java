package com.xionce.doctorvetServices.data;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xionce.doctorvetServices.DoctorVetApp;
import com.xionce.doctorvetServices.R;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.util.ArrayList;

public class Pet_study_valuesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final ArrayList<Pet_study_value> studyValues;
    private final DoctorVetApp.AdapterSelectTypes adapterType;
    private HelperClass.AdapterOnClickHandler clickHandler;
    private HelperClass.AdapterOnRemoveItemHandler removeItemHandler;

    public Pet_study_valuesAdapter(ArrayList<Pet_study_value> studyValues, DoctorVetApp.AdapterSelectTypes adapterType) {
        this.studyValues = studyValues;
        this.adapterType = adapterType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch (adapterType) {
            case NORMAL:
                View view = inflater.inflate(R.layout.list_item_study_value, viewGroup, false);
                return new NormalHolder(view);
            case REMOVE:
                View view_remove = inflater.inflate(R.layout.list_item_study_value_remove, viewGroup, false);
                return new RemoveHolder(view_remove);
            default:
                throw new RuntimeException("non-existent viewType");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Pet_study_value studyValue = (Pet_study_value) studyValues.get(position);
        switch (adapterType) {
            case NORMAL:
                bindNormal(studyValue, (NormalHolder)holder, position);
                break;
            case REMOVE:
                bindRemove(studyValue, (RemoveHolder)holder, position);
                break;
        }
    }

    private void bindNormal(Pet_study_value studyValue, NormalHolder holder, int position) {
        holder.txt_item.setText(studyValue.getItem().getDescription());
        holder.txt_value.setText(studyValue.getValue());
    }

    private void bindRemove(Pet_study_value studyValue, RemoveHolder holder, int position) {
        holder.txt_item.setText(studyValue.getItem().getDescription());
        holder.txt_value.setText(studyValue.getValue());
        holder.img_remove.setClickable(true);
    }

    @Override
    public int getItemCount() {
        return studyValues.size();
    }

    public void addItem(Pet_study_value petStudyItem) {
        studyValues.add(petStudyItem);
        notifyDataSetChanged();
    }
    public void removeItem(int position) {
        studyValues.remove(position);
        notifyItemRemoved(position);
    }
    public void setOnRemoveItemHandler(HelperClass.AdapterOnRemoveItemHandler removeItemHandler) {
        this.removeItemHandler = removeItemHandler;
    }
    public void updateList(ArrayList<Pet_study_value> items) {
        studyValues.clear();
        studyValues.addAll(items);
        notifyDataSetChanged();
    }

    public class NormalHolder extends RecyclerView.ViewHolder {
        private final TextView txt_item;
        private final TextView txt_value;

        public NormalHolder(View view) {
            super(view);
            txt_item = view.findViewById(R.id.txt_item);
            txt_value = view.findViewById(R.id.txt_value);
        }
    }
    public class RemoveHolder extends RecyclerView.ViewHolder {
        private final TextView txt_item;
        private final TextView txt_value;
        private final ImageView img_remove;

        public RemoveHolder(View view) {
            super(view);
            txt_item = view.findViewById(R.id.txt_item);
            txt_value = view.findViewById(R.id.txt_value);

            img_remove = view.findViewById(R.id.img_remove);
            img_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (removeItemHandler == null)
                        return;

                    view.setClickable(false);

                    int pos = getAdapterPosition();
                    Pet_study_value value = studyValues.get(pos);
                    removeItem(pos);
                    removeItemHandler.onRemoveItem(value, view, pos);
                }
            });

        }
    }

}