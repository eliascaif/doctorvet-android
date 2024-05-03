package com.xionce.doctorvetServices.data;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.xionce.doctorvetServices.DoctorVetApp;
import com.xionce.doctorvetServices.R;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.util.ArrayList;

public class OwnersNotificationsAdapter extends RecyclerView.Adapter<OwnersNotificationsAdapter.UserNotificationHolder> {

    private final ArrayList<OwnerNotification> ownerNotifications;
    private HelperClass.AdapterOnClickHandler clickHandler;

    public OwnersNotificationsAdapter(ArrayList<OwnerNotification> ownerNotifications) {
        this.ownerNotifications = ownerNotifications;
    }

    @Override
    public UserNotificationHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.list_item_notification, viewGroup, false);
        return new UserNotificationHolder(view);
    }

    @Override
    public void onBindViewHolder(UserNotificationHolder holder, int position) {
        OwnerNotification userNotification = ownerNotifications.get(position);

        DoctorVetApp.get().setThumb(userNotification.getOwner().getThumb_url(), holder.img_thumb, R.drawable.ic_account_circle_light);

        holder.txt_notification_type.setText(userNotification.getCaption());
        holder.txt_owner_name.setText(userNotification.getOwner().getName());

        if (userNotification.getPet() != null)
            holder.txt_pet_name.setText((userNotification.getPet().getName()));

        String sended = "Enviado";
        if (userNotification.getMessage_sended() != 1)
            sended = "No enviado";
        holder.txt_sended.setText(sended);

        String error = "";
        if (userNotification.getError() != null)
            error = userNotification.getError();
        holder.txt_error.setText(error);

    }

    public void addItems(ArrayList<OwnerNotification> items) {
        ownerNotifications.addAll(items);
        notifyDataSetChanged();
    }

    public void setOnClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    @Override
    public int getItemCount() {
        return ownerNotifications.size();
    }

    public class UserNotificationHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView img_thumb;
        private final TextView txt_notification_type;
        private final TextView txt_owner_name;
        private final TextView txt_pet_name;
        private final TextView txt_sended;
        private final TextView txt_error;

        public UserNotificationHolder(View view) {
            super(view);
            img_thumb = view.findViewById(R.id.img_thumb);
            txt_owner_name = view.findViewById(R.id.txt_owner_name);
            txt_notification_type = view.findViewById(R.id.txt_notification_type);
            txt_pet_name = view.findViewById(R.id.txt_pet_name);
            txt_sended = view.findViewById(R.id.txt_sended);
            txt_error = view.findViewById(R.id.txt_error);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler == null)
                return;

            int pos = getAdapterPosition();
            OwnerNotification ownerNotification = ownerNotifications.get(pos);
            clickHandler.onClick(ownerNotification, this.itemView, pos);
        }
    }

}
