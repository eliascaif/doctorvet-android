package com.xionce.doctorvetServices.data;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.xionce.doctorvetServices.DoctorVetApp;
import com.xionce.doctorvetServices.R;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.util.ArrayList;
import java.util.List;

public class Pet_racesAdapter extends RecyclerView.Adapter<Pet_racesAdapter.RazasAdapterViewHolder>
        implements DoctorVetApp.IGetIdByValueAdapter, DoctorVetApp.IGetSelectedObject {

    private final ArrayList<Pet_race> petraces;
    private HelperClass.AdapterOnClickHandler clickHandler;

    public Pet_racesAdapter(ArrayList<Pet_race> petraces) {
        this.petraces = petraces;
    }

    @Override
    public RazasAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_med;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new RazasAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RazasAdapterViewHolder RazasAdapterViewHolder, int position) {
        Pet_race petrace = this.petraces.get(position);
        RazasAdapterViewHolder.txt_title.setText(petrace.getName());
        RazasAdapterViewHolder.txt_subtitle.setVisibility(View.INVISIBLE);

        String thumb_url = petrace.getThumb_url();
        if (thumb_url != null) {
            Glide.with(RazasAdapterViewHolder.img_thumb.getContext()).load(thumb_url).apply(RequestOptions.circleCropTransform()).apply(RequestOptions.placeholderOf(R.drawable.ic_dog)).into(RazasAdapterViewHolder.img_thumb);
        } else {
            Glide.with(RazasAdapterViewHolder.img_thumb.getContext()).load(R.drawable.ic_dog).apply(RequestOptions.circleCropTransform()).into(RazasAdapterViewHolder.img_thumb);
        }
    }

    @Override
    public int getItemCount() {
        return petraces.size();
    }

    @Override
    public Integer getIdByValue(String value) {
        for (Pet_race r: petraces) {
            if (r.getName().equals(value))
                return r.getId();
        }
        return null;
    }

    public void setList(ArrayList<Pet_race> list) {
        petraces.clear();
        petraces.addAll(list);
        notifyDataSetChanged();
    }
    public void addItems(ArrayList<Pet_race> razas) {
        petraces.addAll(razas);
        notifyDataSetChanged();
    }
    public ArrayAdapter<String> getArrayAdapter(Context ctx) {
        ArrayList<String> lista = new ArrayList<>();
        for (Pet_race r: petraces) {
            lista.add(r.getName());
        }
        return new ArrayAdapter<String>(ctx, android.R.layout.simple_dropdown_item_1line, lista);
    }
    public Pet_racesAutocompleteAdapter getAutocompleteAdapter(Context ctx) {
        return new Pet_racesAutocompleteAdapter(ctx, new ArrayList<>(petraces));
    }

    public void setOnClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }
    public static ArrayList<Pet_race> searchRazas(String userInput, List<Pet_race> mascota_races) {
        ArrayList<Pet_race> filterList = new ArrayList<>();
        String nombre;
        //String especie;

        userInput = HelperClass.normalizeString(userInput);

        for (Pet_race mascota_race : mascota_races) {
            nombre = HelperClass.normalizeString(mascota_race.getName());
            if (nombre.contains(userInput)) {
                filterList.add(mascota_race);
                continue;
            }
//            especie = mascota_race.getEspecies().getName();
//            if (especie != null && especie.toLowerCase().contains(userInput)) {
//                filterList.add(mascota_race);
//                continue;
//            }
        }
        return filterList;
    }

    @Override
    public Object getObjectByName(String name) {
        for (Pet_race r: petraces) {
            if (r.getName().equals(name))
                return r;
        }
        return null;
    }

    public class RazasAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView txt_title;
        private final TextView txt_subtitle;
        private final ImageView img_thumb;

        public RazasAdapterViewHolder(View view) {
            super(view);
            txt_title = view.findViewById(R.id.txt_title);
            txt_subtitle = view.findViewById(R.id.txt_subtitle);
            img_thumb = view.findViewById(R.id.img_thumb);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler == null)
                return;

            int pos = getAdapterPosition();
            Pet_race petrace = petraces.get(pos);
            clickHandler.onClick(petrace, this.itemView, pos);
        }

    }

}
