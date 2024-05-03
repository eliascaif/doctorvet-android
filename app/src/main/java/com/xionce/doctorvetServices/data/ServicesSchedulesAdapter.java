package com.xionce.doctorvetServices.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.xionce.doctorvetServices.R;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.util.ArrayList;

public class ServicesSchedulesAdapter extends RecyclerView.Adapter<ServicesSchedulesAdapter.ServiceScheduleAdapterViewHolder> {

    private HelperClass.AdapterOnClickHandler removeClickHandler;
    private final ArrayList<Service_schedule> serviceSchedules;

    public ServicesSchedulesAdapter(ArrayList<Service_schedule> serviceSchedules) {
        this.serviceSchedules = serviceSchedules;
    }

    public void setOnRemoveClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.removeClickHandler = clickHandler;
    }

    @Override
    public ServiceScheduleAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_service_schedule;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new ServiceScheduleAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ServiceScheduleAdapterViewHolder serviceScheduleAdapterViewHolder, int position) {
        Service_schedule serviceSchedule = this.serviceSchedules.get(position);

        String textToDisplay = "";

        if (serviceSchedule.getService() != null)
            textToDisplay += serviceSchedule.getService();

        if (serviceSchedule.getWeekday() != null) {
            if (!textToDisplay.isEmpty()) textToDisplay += " - ";
            Context ctx = serviceScheduleAdapterViewHolder.img_remove.getContext();
            textToDisplay += String.format("%s de %s a %s", serviceSchedule.getWeekday(), serviceSchedule.getStarting_hour_local(ctx), serviceSchedule.getEnding_hour_local(ctx));
        }

        if (serviceSchedule.getUser() != null) {
            if (!textToDisplay.isEmpty()) textToDisplay += " - ";
            textToDisplay += serviceSchedule.getUser().getName();
        }

        serviceScheduleAdapterViewHolder.txt_title.setText(textToDisplay);
    }

    public void addItems(ArrayList<Service_schedule> items) {
        this.serviceSchedules.addAll(items);
        notifyDataSetChanged();
    }

    public void addItem(Service_schedule item) {
        this.serviceSchedules.add(item);
        notifyDataSetChanged();
    }

    public ArrayList<Service_schedule> getArrayList() {
        return this.serviceSchedules;
    }

    public void removeItem(Service_schedule item) {
        this.serviceSchedules.remove(item);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return serviceSchedules.size();
    }

    public class ServiceScheduleAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView txt_title;
        private ImageView img_remove;

        public ServiceScheduleAdapterViewHolder(View view) {
            super(view);
            txt_title = view.findViewById(R.id.txt_title);
            img_remove = view.findViewById(R.id.img_remove);
            img_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (removeClickHandler == null)
                        return;

                    int pos = getAdapterPosition();
                    Service_schedule serviceSchedule = serviceSchedules.get(pos);
                    removeClickHandler.onClick(serviceSchedule, view, pos);
                }
            });
        }
    }
}