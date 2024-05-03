package com.xionce.doctorvetServices.data;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.xionce.doctorvetServices.DoctorVetApp;
import com.xionce.doctorvetServices.R;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.util.ArrayList;

public class X_requests_users_vets_adminAdapter extends RecyclerView.Adapter<X_requests_users_vets_adminAdapter.X_peticiones_users_veterinariasAdminAdapterViewHolder> {

    private final HelperClass.AdapterOnClickHandler clickHandler;
    private final HelperClass.AdapterOnOkClickHandler okClickHandler;
    private final HelperClass.AdapterOnCancelClickHandler cancelClickHandler;
    ArrayList<X_requests_users_vets> peticiones_users_veterinarias;

    public X_requests_users_vets_adminAdapter(ArrayList<X_requests_users_vets> peticiones_users_veterinarias, HelperClass.AdapterOnClickHandler clickHandler, HelperClass.AdapterOnOkClickHandler okClickHandler, HelperClass.AdapterOnCancelClickHandler cancelClickHandler) {
        this.peticiones_users_veterinarias = peticiones_users_veterinarias;
        this.clickHandler = clickHandler;
        this.okClickHandler = okClickHandler;
        this.cancelClickHandler = cancelClickHandler;
    }

    @Override
    public X_peticiones_users_veterinariasAdminAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.list_item_ok_cancel, viewGroup, false);
        return new X_peticiones_users_veterinariasAdminAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(X_peticiones_users_veterinariasAdminAdapterViewHolder holder, int position) {
//        final Context ctx = holder.txt_info.getContext();
        X_requests_users_vets peticion = peticiones_users_veterinarias.get(position);
        //Usuario usuario = peticion.get_users().get(0);
        holder.txt_info.setText(peticion.getUser().getName());
        holder.txt_info2.setText(peticion.getUser().getEmail());

//        String photo_url = peticion.getUser().getThumb_url();
//        if (photo_url != null && !photo_url.isEmpty()) {
//            Glide.with(ctx).load(photo_url).apply(RequestOptions.fitCenterTransform()).apply(RequestOptions.circleCropTransform()).into(x_peticiones_users_veterinariasAdminAdapterViewHolder.img_thumb);
//        } else {
//            Glide.with(ctx).load(R.drawable.ic_account_circle_light).apply(RequestOptions.fitCenterTransform()).into(x_peticiones_users_veterinariasAdminAdapterViewHolder.img_thumb);
//        }
        DoctorVetApp.get().setThumb(peticion.getUser().getThumb_url(), holder.img_thumb, R.drawable.ic_account_circle_light);
    }

    @Override
    public int getItemCount() {
        return peticiones_users_veterinarias.size();
    }

    public void remove(int pos){
        this.peticiones_users_veterinarias.remove(pos);
    }

    public void addItems(ArrayList<X_requests_users_vets> joinRequestUsers) {
        peticiones_users_veterinarias.addAll(joinRequestUsers);
    }

    public class X_peticiones_users_veterinariasAdminAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        private final TextView txt_info;
        private final TextView txt_info2;
        private final ImageView img_thumb;

        public X_peticiones_users_veterinariasAdminAdapterViewHolder(View view) {
            super(view);
            img_thumb = view.findViewById(R.id.img_thumb);
            txt_info = view.findViewById(R.id.txt_title);
            txt_info2 = view.findViewById(R.id.txt_subtitle);
            TextView txt_estado = view.findViewById(R.id.txt_estado);
            txt_estado.setVisibility(View.INVISIBLE);
            final ImageView img_ok = view.findViewById(R.id.img_ok_cancel);
            img_ok.setVisibility(View.VISIBLE);
            final ImageView img_cancel = view.findViewById(R.id.img_cancel);
            img_cancel.setVisibility(View.VISIBLE);

            img_ok.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (okClickHandler == null)
                        return;

                    okClickHandler.onOkClick(peticiones_users_veterinarias.get(getAdapterPosition()), img_ok, getAdapterPosition());
                }
            });

            img_cancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cancelClickHandler == null)
                        return;

                    cancelClickHandler.onCancelClick(peticiones_users_veterinarias.get(getAdapterPosition()), img_cancel, getAdapterPosition());
                }
            });

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler == null)
                return;

            int pos = getAdapterPosition();
            clickHandler.onClick(peticiones_users_veterinarias.get(pos), this.itemView, pos);
        }

    }

}
