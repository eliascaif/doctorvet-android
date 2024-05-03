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

public class Pet_study_itemsAdapter extends RecyclerView.Adapter<Pet_study_itemsAdapter.StudyItemViewHolder> {
    private final ArrayList<Pet_study_item> studyItems;
    private HelperClass.AdapterOnClickHandler clickHandler;

    public Pet_study_itemsAdapter(ArrayList<Pet_study_item> studyItems) {
        this.studyItems = studyItems;
    }

    @NonNull
    @Override
    public StudyItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.list_item_simple, viewGroup, false);
        return new StudyItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudyItemViewHolder holder, int position) {
        Pet_study_item item = studyItems.get(position);
        holder.txt_title.setText(item.getDescription());
    }

    @Override
    public int getItemCount() {
        return studyItems.size();
    }

    public void setOnClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }
    public ArrayAdapter<Pet_study_item> getArrayAdapter(Context ctx) {
        ArrayAdapter<Pet_study_item> arrayAdapter = new ArrayAdapter<Pet_study_item>(ctx, android.R.layout.simple_dropdown_item_1line, studyItems);
        return arrayAdapter;
    }
    public void addItems(ArrayList<Pet_study_item> studyItems) {
        this.studyItems.addAll(studyItems);
        notifyDataSetChanged();
    }

    public class StudyItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView txt_title;

        public StudyItemViewHolder(View view) {
            super(view);
            txt_title = view.findViewById(R.id.txt_title);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickHandler == null)
                return;

            int pos = getAdapterPosition();
            Pet_study_item studyItem = studyItems.get(pos);
            clickHandler.onClick(studyItem, this.itemView, pos);
        }
    }
}