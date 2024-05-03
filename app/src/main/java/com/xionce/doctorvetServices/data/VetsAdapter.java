package com.xionce.doctorvetServices.data;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xionce.doctorvetServices.DoctorVetApp;
import com.xionce.doctorvetServices.R;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.util.ArrayList;

public class VetsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public enum Adapter_types { SELECT_ITEM, SHOW }
    private final Adapter_types type;
    private final ArrayList<Vet> vets;
    private HelperClass.AdapterOnClickHandler clickHandler;
    private HelperClass.AdapterOnOkClickHandler okClickHandler;
    private Boolean isClickeable = true;

    public Boolean getClickeable() {
        return isClickeable;
    }

    public void setClickeable(Boolean clickeable) {
        isClickeable = clickeable;
    }

    public VetsAdapter(ArrayList<Vet> vets, Adapter_types type) {
        this.vets = vets;
        this.type = type;
    }

    public void setOnClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }
    public void setOnOkClickHandler(HelperClass.AdapterOnOkClickHandler okClickHandler) {
        this.okClickHandler = okClickHandler;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch (type) {
            case SELECT_ITEM:
                View view = inflater.inflate(R.layout.list_item_ok_cancel_vet, viewGroup, false);
                return new SelectViewHolder(view);
            case SHOW:
                View view_show = inflater.inflate(R.layout.list_item_med, viewGroup, false);
                return new ShowViewHolder(view_show);
            default:
                throw new RuntimeException("non-existent viewType");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Vet vet = vets.get(position);
        switch (type) {
            case SELECT_ITEM:
                bindSelectItem(vet, (SelectViewHolder)holder, position);
                break;
            case SHOW:
                bindShow(vet, (ShowViewHolder)holder, position);
                break;
        }
    }

    private void bindShow(Vet vet, ShowViewHolder holder, int position) {
        holder.txt_vet_name.setText(vet.getName());
        holder.txt_vet_email.setText(vet.getEmail()); //vet.getRegionEmail());
        DoctorVetApp.get().setThumb(vet.getThumb_url(), holder.img_thumb, R.drawable.ic_store_holo_ligth);
    }
    private void bindSelectItem(Vet vet, SelectViewHolder holder, int position) {
        holder.txt_vet_name.setText(vet.getName());
        holder.txt_vet_email.setText(vet.getEmail());
        holder.txt_vet_region.setText(vet.getRegion().getFriendly_name());
        DoctorVetApp.get().setThumb(vet.getThumb_url(), holder.img_thumb, R.drawable.ic_store_holo_ligth);

        if (vet.getAssociated() == 1) {
            //Asociado a esta veterinaria
            holder.img_ok_cancel.setVisibility(View.INVISIBLE);
            holder.txt_solicitud.setText(R.string.asociado);
        } else {
            holder.img_ok_cancel.setVisibility(View.VISIBLE);

            if (vet.getRequested() == 1) {
                holder.img_ok_cancel.setImageResource(R.drawable.ic_cancel_item_light);
                holder.txt_solicitud.setText(R.string.solicitud_pendiente);
            } else {
                holder.img_ok_cancel.setImageResource(R.drawable.ic_select_item);
                holder.txt_solicitud.setText("");
            }
        }
    }

    @Override
    public int getItemCount() {
        return vets.size();
    }

    public void addItems(ArrayList<Vet> items) {
        vets.addAll(items);
        notifyDataSetChanged();
    }

    public void setRequestSended(int pos) {
        vets.get(pos).setRequested(1);
        notifyItemChanged(pos);
    }

    public void setRequestCanceled(int pos) {
        vets.get(pos).setRequested(0);
        notifyItemChanged(pos);
    }

    public class SelectViewHolder extends RecyclerView.ViewHolder {
        private final ImageView img_thumb;
        private final TextView txt_vet_name;
        private final TextView txt_vet_email;
        private final TextView txt_vet_region;
        private final TextView txt_solicitud;
        private final ImageView img_ok_cancel;

        public SelectViewHolder(View view) {
            super(view);
            img_thumb = view.findViewById(R.id.img_thumb);
            txt_vet_name = view.findViewById(R.id.txt_vet_name);
            txt_vet_email = view.findViewById(R.id.txt_vet_email);
            txt_vet_region = view.findViewById(R.id.txt_vet_region);
            txt_solicitud = view.findViewById(R.id.txt_estado);

            img_ok_cancel = view.findViewById(R.id.img_ok_cancel);
            if (okClickHandler != null) {
                img_ok_cancel.setVisibility(View.VISIBLE);
                img_ok_cancel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        okClickHandler.onOkClick(vets.get(getAdapterPosition()), img_ok_cancel, getAdapterPosition());
                    }
                });
            }
        }
    }
    public class ShowViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        private final ImageView img_thumb;
        private final TextView txt_vet_name;
        private final TextView txt_vet_email;

        public ShowViewHolder(View view) {
            super(view);
            img_thumb = view.findViewById(R.id.img_thumb);
            txt_vet_name = view.findViewById(R.id.txt_title);
            txt_vet_email = view.findViewById(R.id.txt_subtitle);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler == null) return;
            if (!isClickeable) return;

            int pos = getAdapterPosition();
            Vet vet = vets.get(pos);
            clickHandler.onClick(vet, this.itemView, pos);
        }
    }

}