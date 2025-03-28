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
import java.util.Iterator;

public class AgendaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private HelperClass.AdapterOnClickHandler clickHandler;
    private final ArrayList<Agenda> agenda;
    private HelperClass.AdapterOnClickHandler checkClickHandler;
    private HelperClass.AdapterOnClickHandler rescheduleClickHandler;

    public enum AgendaUsersType { USER, ALL_USERS }
    private AgendaUsersType adapterUsersType;

    public enum AgendaAdapterTypes { NORMAL, DASHBOARD, RESCHEDULE }
    private final AgendaAdapterTypes adapterType;

    public AgendaAdapter(ArrayList<Agenda> agenda, AgendaAdapterTypes adapterType) {
        this.agenda = agenda;
        this.adapterType = adapterType;
    }

    public void addItems(ArrayList<Agenda> items) {
        agenda.addAll(items);
        notifyDataSetChanged();
    }

    public void setOnCheckClickHandler(HelperClass.AdapterOnClickHandler checkClickHandler) {
        this.checkClickHandler = checkClickHandler;
    }
    public void setOnRescheduleClickHandler(HelperClass.AdapterOnClickHandler rescheduleClickHandler) {
        this.rescheduleClickHandler = rescheduleClickHandler;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch (adapterType) {
            case NORMAL:
                View view = inflater.inflate(R.layout.list_item_agenda, viewGroup, false);
                return new AgendaHolder(view);
            case DASHBOARD:
                View view_search = inflater.inflate(R.layout.list_item_agenda_dashboard, viewGroup, false);
                return new DashboardHolder(view_search);
            case RESCHEDULE:
                View view_reschedule = inflater.inflate(R.layout.list_item_agenda_reschedule, viewGroup, false);
                return new RescheduleHolder(view_reschedule);
            default:
                throw new RuntimeException("non-existent viewType");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Agenda agenda_event = agenda.get(position);
        switch (adapterType) {
            case NORMAL:
                bindNormal(agenda_event, (AgendaHolder)holder/*, position*/);
                break;
            case DASHBOARD:
                bindDashboard(agenda_event, (DashboardHolder)holder/*, position*/);
                break;
            case RESCHEDULE:
                bindReschedule(agenda_event, (RescheduleHolder) holder/*, position*/);
                break;
        }
    }

    private void bindNormal(Agenda agenda, AgendaHolder holder/*, int position*/) {
        final Context ctx = holder.txt_event_info.getContext();

        //String event_name = HelperClass.getTimeInLocale(agenda.getBegin_time(), ctx);
        String event_name = HelperClass.getDateTimeInLocaleShort(agenda.getBegin_time());
        if (agenda.getEnd_time() != null)
            event_name += " - " + HelperClass.getDateTimeInLocaleShort(agenda.getEnd_time());
            //event_name += " - " + HelperClass.getTimeInLocale(agenda.getEnd_time(), ctx);
        event_name += ". " + agenda.getEvent_name();
        event_name += ". " + agenda.getUser().getName() + ". ";
        holder.txt_event_info.setText(event_name);

        Glide.with(ctx).load(R.drawable.ic_time).apply(RequestOptions.fitCenterTransform()).into(holder.img_thumb);
        if (agenda.getProduct() != null)
            DoctorVetApp.get().setThumb(agenda.getProduct().getThumb_url(), holder.img_thumb, R.drawable.ic_time);

        //petinfo
        String pet_info = "";
        if (agenda.getPet() != null)
            pet_info += agenda.getPet().getName();
        holder.txt_pet_info.setText(pet_info);

        //ownerinfo
        String owner = "";
        if (agenda.getOwner() != null) {
            owner = "De: " + agenda.getOwner().getName();
            holder.txt_owner_info.setText(owner);
        } else {
            holder.txt_owner_info.setVisibility(View.GONE);
        }

        //state
        holder.img_check.setVisibility(View.VISIBLE);

        if (agenda.getExecuted() == 1) {
            holder.img_check.setVisibility(View.GONE);
        }

    }
    private void bindDashboard(Agenda agenda, DashboardHolder holder/*, int position*/) {
        Context ctx = holder.img_thumb.getContext();

        DoctorVetApp.get().setThumb(agenda.getPet().getThumb_url(), holder.img_thumb, R.drawable.ic_pets_light);

        holder.txt_pet_name.setText(agenda.getPet().getName());

        // owner
        String owner = "";
        if (agenda.getOwner() != null)
            owner = "De: " + agenda.getOwner().getName();
        holder.txt_owners.setText(owner);

        //event info
        String event_info = agenda.getEvent_name() + " " + HelperClass.getTimeInLocale(agenda.getBegin_time(), ctx);
        if (agenda.getEnd_time() != null)
            event_info += " - " + HelperClass.getTimeInLocale(agenda.getEnd_time(), ctx);
        event_info += ". " + agenda.getUser().getName();
        holder.txt_event_info.setText(event_info);

        //invisibilize remove for supply derived tasks
        holder.txt_supply_event.setVisibility(View.GONE);

        //toggle between see all and see only mine
        if (adapterUsersType == AgendaUsersType.USER
                && !agenda.getUser().getId().equals(DoctorVetApp.get().getUser().getId())) {
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

    }
    private void bindReschedule(Agenda agenda, RescheduleHolder holder/*, int position*/) {
        final Context ctx = holder.txt_event_info.getContext();

        String event_name = HelperClass.getDateTimeInLocaleShort(agenda.getBegin_time());
        if (agenda.getEnd_time() != null)
            event_name += " - " + HelperClass.getDateTimeInLocaleShort(agenda.getEnd_time());

        event_name += ". " + agenda.getEvent_name();
        event_name += ". " + agenda.getUser().getName() + ". ";
        holder.txt_event_info.setText(event_name);

        Glide.with(ctx).load(R.drawable.ic_time).apply(RequestOptions.fitCenterTransform()).into(holder.img_thumb);
        if (agenda.getProduct() != null)
            DoctorVetApp.get().setThumb(agenda.getProduct().getThumb_url(), holder.img_thumb, R.drawable.ic_time);

        //petinfo
        String pet_info = "";
        if (agenda.getPet() != null)
            pet_info += agenda.getPet().getName();
        holder.txt_pet_info.setText(pet_info);

        //ownerinfo
        String owner = "";
        if (agenda.getOwner() != null) {
            owner = "De: " + agenda.getOwner().getName();
            holder.txt_owner_info.setText(owner);
        } else {
            holder.txt_owner_info.setVisibility(View.GONE);
        }

        //state
        holder.img_check.setVisibility(View.VISIBLE);
        holder.img_reschedule.setVisibility(View.VISIBLE);

        if (agenda.getExecuted() == 1) {
            holder.img_check.setVisibility(View.GONE);
            holder.img_reschedule.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return agenda.size();
    }

    public boolean addItem(Agenda item) {
        if (!existsById(item.getId())) {
            agenda.add(item);
            notifyDataSetChanged();
            return true;
        }

        return false;
    }

    public boolean existsById(Integer id) {
        for (Agenda agenda : agenda)
            if (agenda.getId().equals(id))
                return true;

        return false;
    }

    public void removeById(Integer id) {
        Iterator<Agenda> iterator = agenda.iterator();
        while (iterator.hasNext()) {
            Agenda agendaItem = iterator.next();
            if (agendaItem.getId().equals(id)) {
                agenda.remove(agendaItem);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void setAdapterType(AgendaUsersType adapterType) {
        int size = agenda.size();
        ArrayList<Agenda> tempCopy = new ArrayList<>(agenda);
        agenda.clear();
        notifyItemRangeRemoved(0, size);
        this.adapterUsersType = adapterType;
        agenda.addAll(tempCopy);
        notifyDataSetChanged();
    }

    public void setOnClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }
    public void setList(ArrayList<Agenda> list) {
        agenda.clear();
        agenda.addAll(list);
        notifyDataSetChanged();
    }
    public void updateList(ArrayList<Agenda> filterList) {
        agenda.clear();
        agenda.addAll(filterList);
        notifyDataSetChanged();
    }

    public class AgendaHolder extends RecyclerView.ViewHolder implements OnClickListener {
        private final ImageView img_thumb;
        private final TextView txt_event_info;
        private final TextView txt_pet_info;
        private final TextView txt_owner_info;
        private final ImageView img_check;

        public AgendaHolder(View view) {
            super(view);
            img_thumb = view.findViewById(R.id.img_thumb);
            txt_event_info = view.findViewById(R.id.txt_event_info);
            txt_pet_info = view.findViewById(R.id.txt_pet_info);
            txt_owner_info = view.findViewById(R.id.txt_owner_info);
            img_check = view.findViewById(R.id.img_check);
            img_check.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (checkClickHandler != null) {
                        int pos = getAdapterPosition();
                        Agenda agenda_item = agenda.get(pos);
                        checkClickHandler.onClick(agenda_item, img_check, pos);
                    }
                }
            });

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler != null) {
                int pos = getAdapterPosition();
                Agenda agenda_event = agenda.get(pos);
                clickHandler.onClick(agenda_event, this.itemView, pos);
            }
        }
    }
    public class DashboardHolder extends RecyclerView.ViewHolder implements OnClickListener {
        private final TextView txt_pet_name;
        private final ImageView img_thumb;
        private final TextView txt_owners;
        private final TextView txt_event_info;
        //private View div_line;
        private final ImageView imgCheck;
        private final TextView txt_supply_event;
        private final ImageView img_reschedule;

        public DashboardHolder(View view) {
            super(view);
            txt_pet_name = view.findViewById(R.id.txt_pet_name);
            img_thumb = view.findViewById(R.id.img_thumb);
            txt_owners = view.findViewById(R.id.txt_pet_owners);
            txt_event_info = view.findViewById(R.id.txt_event_info);
            //div_line = view.findViewById(R.id.div_line);
            txt_supply_event = view.findViewById(R.id.txt_supply_event);

            imgCheck = view.findViewById(R.id.img_check);
            imgCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (checkClickHandler == null) return;

                    int pos = getAdapterPosition();
                    Agenda agendaItem = agenda.get(pos);
                    checkClickHandler.onClick(agendaItem, imgCheck, pos);
                }
            });

            img_reschedule = view.findViewById(R.id.img_reschedule);
            img_reschedule.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (rescheduleClickHandler != null) {
                        int pos = getAdapterPosition();
                        Agenda agenda_item = agenda.get(pos);
                        rescheduleClickHandler.onClick(agenda_item, img_reschedule, pos);
                    }
                }
            });

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler == null) return;

            int pos = getAdapterPosition();
            Agenda agenda_event = agenda.get(pos);
            if (agenda_event.getPet() != null)
                clickHandler.onClick(agenda_event.getPet(), this.itemView, pos);
        }
    }
    public class RescheduleHolder extends RecyclerView.ViewHolder implements OnClickListener {
        private final ImageView img_thumb;
        private final TextView txt_event_info;
        private final TextView txt_pet_info;
        private final TextView txt_owner_info;
        private final ImageView img_check;
        private final ImageView img_reschedule;

        public RescheduleHolder(View view) {
            super(view);
            img_thumb = view.findViewById(R.id.img_thumb);
            txt_event_info = view.findViewById(R.id.txt_event_info);
            txt_pet_info = view.findViewById(R.id.txt_pet_info);
            txt_owner_info = view.findViewById(R.id.txt_owner_info);
            img_check = view.findViewById(R.id.img_check);
            img_check.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (checkClickHandler != null) {
                        int pos = getAdapterPosition();
                        Agenda agenda_item = agenda.get(pos);
                        checkClickHandler.onClick(agenda_item, img_check, pos);
                    }
                }
            });

            img_reschedule = view.findViewById(R.id.img_reschedule);
            img_reschedule.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (rescheduleClickHandler != null) {
                        int pos = getAdapterPosition();
                        Agenda agenda_item = agenda.get(pos);
                        rescheduleClickHandler.onClick(agenda_item, img_reschedule, pos);
                    }
                }
            });

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler != null) {
                int pos = getAdapterPosition();
                Agenda agenda_event = agenda.get(pos);
                clickHandler.onClick(agenda_event, this.itemView, pos);
            }
        }
    }

}