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

public class VetsDepositsAdapter extends RecyclerView.Adapter<VetsDepositsAdapter.DepositsViewHolder>
        implements DoctorVetApp.IGetIdByValueAdapter {

    private ArrayList<Vet_deposit> vetDeposits;
    private HelperClass.AdapterOnClickHandler editClickHandler;
    private HelperClass.AdapterOnClickHandler removeClickHandler;

    public VetsDepositsAdapter(ArrayList<Vet_deposit> vetDeposits) {
        this.vetDeposits = vetDeposits;
    }

    public void setOnEditClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.editClickHandler = clickHandler;
    }
    public void setOnRemoveClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.removeClickHandler = clickHandler;
    }

    @Override
    public DepositsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_vet_deposits;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new DepositsViewHolder(view);
    }

    public ArrayAdapter<Vet_deposit> getArrayAdapter(Context ctx) {
        ArrayAdapter<Vet_deposit> arrayAdapter = new ArrayAdapter<Vet_deposit>(ctx, android.R.layout.simple_dropdown_item_1line, vetDeposits);
        return arrayAdapter;
    }

    @Override
    public void onBindViewHolder(DepositsViewHolder holder, int position) {
        Vet_deposit vetDeposit = this.vetDeposits.get(position);

        holder.txt_title.setText(vetDeposit.getName());

        holder.txt_subtitle.setText("");
        if (vetDeposit.getIs_central() == 1)
            holder.txt_subtitle.setText("Central");

        holder.img_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editClickHandler != null) {
                    editClickHandler.onClick(vetDeposit, holder.img_edit, holder.getAdapterPosition());
                }
            }
        });

        holder.img_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (removeClickHandler != null) {
                    removeClickHandler.onClick(vetDeposit, holder.img_remove, holder.getAdapterPosition());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return vetDeposits.size();
    }

    @Override
    public Integer getIdByValue(String value) {
        return null;
    }

    public class DepositsViewHolder extends RecyclerView.ViewHolder {
        private final TextView txt_title;
        private final TextView txt_subtitle;
        private final ImageView img_edit;
        private final ImageView img_remove;

        public DepositsViewHolder(View view) {
            super(view);
            txt_title = view.findViewById(R.id.txt_title);
            txt_subtitle = view.findViewById(R.id.txt_subtitle);
            img_edit = view.findViewById(R.id.img_edit);
            img_remove = view.findViewById(R.id.img_delete);
        }
    }
}