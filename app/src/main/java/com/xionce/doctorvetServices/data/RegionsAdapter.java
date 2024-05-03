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

public class RegionsAdapter extends RecyclerView.Adapter<RegionsAdapter.RegionesAdapterViewHolder>
        implements DoctorVetApp.IGetIdByValueAdapter {

    private final ArrayList<Region> regions;
    private HelperClass.AdapterOnClickHandler clickHandler;

    public RegionsAdapter(ArrayList<Region> regions) {
        this.regions = regions;
    }

    @Override
    public RegionesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_simple;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new RegionesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RegionesAdapterViewHolder regionesAdapterViewHolder, int position) {
        Region region = regions.get(position);
        regionesAdapterViewHolder.txt_title.setText(region.getFriendly_name());
        regionesAdapterViewHolder.txt_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickHandler != null)
                    clickHandler.onClick(region, regionesAdapterViewHolder.txt_title, regionesAdapterViewHolder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return regions.size();
    }

    public ArrayAdapter<Region> getArrayAdapter(Context ctx) {
        ArrayAdapter<Region> arrayAdapter = new ArrayAdapter<Region>(ctx, android.R.layout.simple_dropdown_item_1line, regions);
        return arrayAdapter;
    }

    @Override
    public Integer getIdByValue(String value) {
        for (Region r: regions) {
            if (r.getFriendly_name().equals(value))
                return r.getId();
        }
        return null;
    }

    public void addItems(ArrayList<Region> items) {
        for (Region region:items) {
            regions.add(region);
        }
        notifyDataSetChanged();
    }
    public void setList(ArrayList<Region> list) {
        regions.clear();
        regions.addAll(list);
        notifyDataSetChanged();
    }

    public void setOnClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    public class RegionesAdapterViewHolder extends RecyclerView.ViewHolder {
        public final TextView txt_title;

        public RegionesAdapterViewHolder(View view) {
            super(view);
            txt_title = view.findViewById(R.id.txt_title);
        }
    }

}
