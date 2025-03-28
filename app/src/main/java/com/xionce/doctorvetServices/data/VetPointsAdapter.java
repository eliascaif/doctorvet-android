package com.xionce.doctorvetServices.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.xionce.doctorvetServices.DoctorVetApp;
import com.xionce.doctorvetServices.R;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.util.ArrayList;

public class VetPointsAdapter extends RecyclerView.Adapter<VetPointsAdapter.VetPointHolder>
        implements DoctorVetApp.IGetIdByValueAdapter {

    private final ArrayList<Vet_point> vetPoints;
    private HelperClass.AdapterOnClickHandler removeClickHandler;

    public VetPointsAdapter(ArrayList<Vet_point> vetPoints) {
        this.vetPoints = vetPoints;
    }

    public void setOnRemoveClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.removeClickHandler = clickHandler;
    }

    @Override
    public VetPointHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_sell_point;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new VetPointHolder(view);
    }

    @Override
    public void onBindViewHolder(final VetPointHolder holder, int position) {
        Vet_point vetPoint = this.vetPoints.get(position);

        holder.txt_title.setText(vetPoint.getName());

//        String subtitle = String.format("Denominación: %s | Denominación corta: %s | Tipo: %s | Contador: %s",
//                vetPoint.getFinance_types_receipt().getDenomination(),
//                vetPoint.getFinance_types_receipt().getShort_denomination(),
//                vetPoint.getType(),
//                vetPoint.getCounter()
//        );
        String subtitle = String.format("Número: %s | Tipo: %s ",
                vetPoint.getNumber(),
                vetPoint.getType()
        );
        holder.txt_subtitle.setText(subtitle);

        holder.img_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (removeClickHandler != null) {
                    removeClickHandler.onClick(vetPoint, holder.img_remove, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return vetPoints.size();
    }

    public ArrayAdapter<Vet_point> getArrayAdapter(Context ctx) {
        return new ArrayAdapter<Vet_point>(ctx, android.R.layout.simple_dropdown_item_1line, vetPoints);
    }

    public int getPositionByName(String name) {
        for (Vet_point sellPoint : vetPoints) {
            if (sellPoint.getName().equalsIgnoreCase(name))
                return vetPoints.indexOf(sellPoint);
        }
        return -1;
    }

    @Override
    public Integer getIdByValue(String value) {
//            if (sellPoint.getFinance_types_receipt().getDenomination().equals(value))
        for (Vet_point sellPoint : vetPoints) {
            if (sellPoint.getName().equals(value))
                return sellPoint.getId();
        }
        return null;
    }

    public class VetPointHolder extends RecyclerView.ViewHolder {
        private final TextView txt_title;
        private final TextView txt_subtitle;
        private final ImageView img_remove;

        public VetPointHolder(View view) {
            super(view);
            txt_title = view.findViewById(R.id.txt_title);
            txt_subtitle = view.findViewById(R.id.txt_subtitle);
            img_remove = view.findViewById(R.id.img_delete);
        }
    }
}