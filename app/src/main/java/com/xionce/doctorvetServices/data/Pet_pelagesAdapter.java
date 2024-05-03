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

public class Pet_pelagesAdapter extends RecyclerView.Adapter<Pet_pelagesAdapter.PelajesAdapterViewHolder>
        implements DoctorVetApp.IGetIdByValueAdapter, DoctorVetApp.IGetSelectedObject {

    private final ArrayList<Pet_pelage> petpelages;
    private HelperClass.AdapterOnClickHandler clickHandler;

    public Pet_pelagesAdapter(ArrayList<Pet_pelage> petpelages) {
        this.petpelages = petpelages;
    }

    @Override
    public PelajesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_simple;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new PelajesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PelajesAdapterViewHolder PelajesAdapterViewHolder, int position) {
        Pet_pelage petpelage = this.petpelages.get(position);
        PelajesAdapterViewHolder.txt_title.setText(petpelage.getName());
    }

    @Override
    public int getItemCount() {
        return petpelages.size();
    }

    @Override
    public Integer getIdByValue(String value) {
        for (Pet_pelage petpelage : petpelages) {
            if (petpelage.getName().equals(value))
                return petpelage.getId();
        }
        return null;
    }
    @Override
    public Object getObjectByName(String name) {
        for (Pet_pelage r: petpelages) {
            if (r.getName().equals(name))
                return r;
        }
        return null;
    }

    public ArrayAdapter<Pet_pelage> getArrayAdapter(Context ctx) {
        ArrayAdapter<Pet_pelage> arrayAdapter = new ArrayAdapter<Pet_pelage>(ctx, android.R.layout.simple_dropdown_item_1line, petpelages);
        return arrayAdapter;
    }

    public void setList(ArrayList<Pet_pelage> list) {
        petpelages.clear();
        petpelages.addAll(list);
        notifyDataSetChanged();
    }
    public void setOnClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }
    public void addItems(ArrayList<Pet_pelage> pelajes) {
        for (Pet_pelage pelaje:pelajes) {
            petpelages.add(pelaje);
        }

        notifyDataSetChanged();
    }

    public class PelajesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView txt_title;

        public PelajesAdapterViewHolder(View view) {
            super(view);
            txt_title = view.findViewById(R.id.txt_title);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler == null)
                return;

            int pos = getAdapterPosition();
            Pet_pelage petpelage = petpelages.get(pos);
            clickHandler.onClick(petpelage, this.itemView, pos);
        }

    }

}
