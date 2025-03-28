package com.xionce.doctorvetServices.data;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xionce.doctorvetServices.DoctorVetApp;
import com.xionce.doctorvetServices.R;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public enum Adapter_types { PROMOTE, SHOW }
    private final UsersAdapter.Adapter_types type;
    private final ArrayList<User> users;
    private HelperClass.AdapterOnClickHandler clickHandler;
    private HelperClass.AdapterOnClickHandler upgradeClickHandler;
    private HelperClass.AdapterOnClickHandler downgradeClickHandler;
    private HelperClass.AdapterOnClickHandler removeClickHandler;
    private HelperClass.AdapterOnClickHandler editClickHandler;

    public UsersAdapter(ArrayList<User> users, Adapter_types type) {
        this.users = users;
        this.type = type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch (type) {
            case PROMOTE:
                View view = inflater.inflate(R.layout.list_item_promote_users, viewGroup, false);
                return new PromoteViewHolder(view);
            case SHOW:
                View view_show = inflater.inflate(R.layout.list_item_med, viewGroup, false);
                return new ShowViewHolder(view_show);
            default:
                throw new RuntimeException("non-existent viewType");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        User user = users.get(position);
        switch (type) {
            case PROMOTE:
                bindPromote(user, (PromoteViewHolder)holder, position);
                break;
            case SHOW:
                bindShow(user, (ShowViewHolder)holder, position);
                break;
        }
    }

    private void bindShow(User user, ShowViewHolder holder, int position) {
        holder.txt_title.setText(user.getName());
        holder.txt_subtitle.setText(user.getEmail());
        DoctorVetApp.get().setThumb(user.getThumb_url(), holder.img_image, R.drawable.ic_account_circle_light);
    }
    private void bindPromote(User user, PromoteViewHolder holder, int position) {
        holder.txt_title.setText(user.getName());
        holder.txt_subtitle.setText(user.getEmail());
        holder.txt_rol.setText(user.getRol());

        DoctorVetApp.get().setThumb(user.getThumb_url(), holder.img_image, R.drawable.ic_account_circle_light);

        holder.img_upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (upgradeClickHandler != null) {
                    upgradeClickHandler.onClick(user, holder.img_upgrade, position);
                }
            }
        });

        holder.img_downgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (downgradeClickHandler != null) {
                    downgradeClickHandler.onClick(user, holder.img_upgrade, position);
                }
            }
        });

        holder.img_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (removeClickHandler != null) {
                    removeClickHandler.onClick(user, holder.img_upgrade, position);
                }
            }
        });

        holder.img_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editClickHandler != null) {
                    editClickHandler.onClick(user, holder.img_edit, position);
                }
            }
        });

        if (user.getRol().equalsIgnoreCase("owner")) {
            holder.img_upgrade.setVisibility(View.GONE);
            holder.img_downgrade.setVisibility(View.GONE);
            holder.img_remove.setVisibility(View.GONE);
            holder.img_edit.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setOnClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }
    public void setOnUpgradeClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.upgradeClickHandler = clickHandler;
    }
    public void setOnDowngradeClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.downgradeClickHandler = clickHandler;
    }
    public void setOnRemoveClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.removeClickHandler = clickHandler;
    }
    public void setOnEditClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.editClickHandler = clickHandler;
    }

    public ArrayAdapter<String> getArrayAdapter(Context ctx) {
        ArrayList<String> lista = new ArrayList<>();
        for (User r: users) {
            lista.add(r.getName());
        }
        return new ArrayAdapter<String>(ctx, android.R.layout.simple_dropdown_item_1line, lista);
    }

    public void addItems(ArrayList<User> items) {
        users.addAll(items);
        notifyDataSetChanged();
    }

    public class ShowViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView img_image;
        private final TextView txt_title;
        private final TextView txt_subtitle;

        public ShowViewHolder(View view) {
            super(view);
            img_image = view.findViewById(R.id.img_thumb);
            txt_title = view.findViewById(R.id.txt_title);
            txt_subtitle = view.findViewById(R.id.txt_subtitle);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler == null)
                return;

            int pos = getAdapterPosition();
            User user = users.get(pos);
            clickHandler.onClick(user, this.itemView, pos);
        }

    }
    public class PromoteViewHolder extends RecyclerView.ViewHolder {
        private final ImageView img_image;
        private final TextView txt_title;
        private final TextView txt_subtitle;
        private final TextView txt_rol;
        private final ImageView img_upgrade;
        private final ImageView img_downgrade;
        private final ImageView img_remove;
        private final ImageView img_edit;

        public PromoteViewHolder(View view) {
            super(view);
            img_image = view.findViewById(R.id.img_thumb);
            txt_title = view.findViewById(R.id.txt_title);
            txt_subtitle = view.findViewById(R.id.txt_subtitle);
            txt_rol = view.findViewById(R.id.txt_rol);
            img_upgrade = view.findViewById(R.id.img_upgrade);
            img_downgrade = view.findViewById(R.id.img_downgrade);
            img_remove = view.findViewById(R.id.img_delete);
            img_edit = view.findViewById(R.id.img_edit);
        }
    }
}
