package com.xionce.doctorvetServices.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.xionce.doctorvetServices.DoctorVetApp;
import com.xionce.doctorvetServices.R;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.util.ArrayList;

public class LogsAdapter extends RecyclerView.Adapter<LogsAdapter.LogViewHolder> {

    private final ArrayList<Log> logs;

    public LogsAdapter(ArrayList<Log> logs) {
        this.logs = logs;
    }

    @Override
    public LogViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_log;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LogViewHolder holder, int position) {
        Log log = logs.get(position);
        DoctorVetApp.get().setThumb(log.getUser().getThumb_url(), holder.img_image, R.drawable.ic_account_circle_light);
        holder.txt_user.setText("Usuario: " + log.getUser().getName());
        holder.txt_created_at.setText("Creado el: " + HelperClass.getDateInLocaleShort(log.getCreated_at()));
        holder.txt_operation.setText("Operación: " + log.getOperation());
        holder.txt_table_name.setText("Tabla: " + log.getTable_name());
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    public void addItems(ArrayList<Log> items) {
        this.logs.addAll(items);
        notifyDataSetChanged();
    }

    public class LogViewHolder extends RecyclerView.ViewHolder {
        private final ImageView img_image;
        private final TextView txt_user;
        private final TextView txt_created_at;
        private final TextView txt_operation;
        private final TextView txt_table_name;

        public LogViewHolder(View view) {
            super(view);
            img_image = view.findViewById(R.id.img_thumb);
            txt_user = view.findViewById(R.id.txt_user);
            txt_created_at = view.findViewById(R.id.txt_created_at);
            txt_operation = view.findViewById(R.id.txt_operation);
            txt_table_name = view.findViewById(R.id.txt_table_name);
        }
    }

}
