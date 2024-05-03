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

public class TreatmentsAdapter extends RecyclerView.Adapter<TreatmentsAdapter.TreatmentsAdapterViewHolder>
        implements DoctorVetApp.IGetIdByValueAdapter, DoctorVetApp.IGetSelectedObject {

    private final ArrayList<Treatment> treatments;
    private HelperClass.AdapterOnClickHandler clickHandler;

    public TreatmentsAdapter(ArrayList<Treatment> treatments) {
        this.treatments = treatments;
    }

    @Override
    public TreatmentsAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_simple;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new TreatmentsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TreatmentsAdapterViewHolder TreatmentsAdapterViewHolder, int position) {
        Treatment treatment = this.treatments.get(position);
        TreatmentsAdapterViewHolder.txt_title.setText(treatment.getName());
    }

    @Override
    public int getItemCount() {
        return treatments.size();
    }

    @Override
    public Integer getIdByValue(String value) {
        for (Treatment treatment : treatments) {
            if (treatment.getName().equals(value))
                return treatment.getId();
        }
        return null;
    }
    @Override
    public Object getObjectByName(String name) {
        for (Treatment r: treatments) {
            if (r.getName().equals(name))
                return r;
        }
        return null;
    }

    public ArrayAdapter<Treatment> getArrayAdapter(Context ctx) {
        ArrayAdapter<Treatment> arrayAdapter = new ArrayAdapter<Treatment>(ctx, android.R.layout.simple_dropdown_item_1line, treatments);
        return arrayAdapter;
    }

    public void setOnClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }
    public void addItems(ArrayList<Treatment> treatments) {
        this.treatments.addAll(treatments);
        notifyDataSetChanged();
    }

    public class TreatmentsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView txt_title;

        public TreatmentsAdapterViewHolder(View view) {
            super(view);
            txt_title = view.findViewById(R.id.txt_title);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler == null)
                return;

            int pos = getAdapterPosition();
            Treatment diagnostic = treatments.get(pos);
            clickHandler.onClick(diagnostic, this.itemView, pos);
        }

    }

}
