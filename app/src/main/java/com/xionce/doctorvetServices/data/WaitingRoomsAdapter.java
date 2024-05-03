package com.xionce.doctorvetServices.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xionce.doctorvetServices.DoctorVetApp;
import com.xionce.doctorvetServices.R;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.util.ArrayList;
import java.util.Iterator;

public class WaitingRoomsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements DoctorVetApp.IGetIdByValueAdapter, DoctorVetApp.IGetSelectedObject {

    private final ArrayList<Waiting_room> waitingRooms;
    private HelperClass.AdapterOnClickHandler clickHandler;
    private HelperClass.AdapterOnClickHandler checkClickHandler;
    private HelperClass.AdapterOnCancelClickHandler removeClickHandler;

    public enum WaitingRoomsAdapterType { USER, ALL_USERS, REPORT }
    private WaitingRoomsAdapterType adapterType;

    public WaitingRoomsAdapter(ArrayList<Waiting_room> waitingRooms, WaitingRoomsAdapterType adapterType) {
        this.waitingRooms = waitingRooms;
        this.adapterType = adapterType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view_compact = inflater.inflate(R.layout.list_item_waiting_rooms, viewGroup, false);
        return new RemoveViewHolder(view_compact);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Waiting_room waitingRoom = waitingRooms.get(position);
        bind(waitingRoom, (RemoveViewHolder)holder, position);
    }

    private void bind(Waiting_room waitingRoom, RemoveViewHolder holder, int position) {
        DoctorVetApp.get().setThumb(waitingRoom.getPet().getThumb_url(), holder.imgThumb, R.drawable.ic_dog);

        holder.txtPetName.setText(waitingRoom.getPet().getName());

        // owner
        String owner = "";
        if (waitingRoom.getOwner() != null)
            owner = "De: " + waitingRoom.getOwner().getName();

        holder.txtPetOwner.setText(owner);

        // attended by user
        String attend = "Sin usuario asignado";
        if (waitingRoom.getPre_attended_by_user() != null)
            attend = "Atiende " + waitingRoom.getPre_attended_by_user().getName();
        if (waitingRoom.getSite() != null)
            attend += " en " + waitingRoom.getSite();
        holder.txtAttendedUser.setText(attend);

//        if (position == getItemCount()-1) {
//            holder.divLine.setVisibility(View.GONE);
//        }

        if (adapterType == WaitingRoomsAdapterType.USER
                && waitingRoom.getPre_attended_by_user() != null && !waitingRoom.getPre_attended_by_user().getId().equals(DoctorVetApp.get().getUser().getId())) {
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        if (adapterType == WaitingRoomsAdapterType.REPORT) {
            holder.imgRemove.setVisibility(View.GONE);
            holder.imgCheck.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return waitingRooms.size();
    }

    @Override
    public Integer getIdByValue(String value) {
        for (Waiting_room waitingRoom : waitingRooms) {
            if (waitingRoom.getSite().equals(value))
                return waitingRoom.getId();
        }
        return null;
    }
    @Override
    public Object getObjectByName(String name) {
        for (Waiting_room r: waitingRooms) {
            if (r.getSite().equals(name))
                return r;
        }
        return null;
    }

    public void setAdapterType(WaitingRoomsAdapterType adapterType) {
        int size = waitingRooms.size();
        ArrayList<Waiting_room> tempCopy = new ArrayList<>(waitingRooms);
        waitingRooms.clear();
        notifyItemRangeRemoved(0, size);
        this.adapterType = adapterType;
        waitingRooms.addAll(tempCopy);
        notifyDataSetChanged();
    }

    public ArrayAdapter<String> getArrayAdapter(Context ctx) {
        ArrayList<String> lista = new ArrayList<>();
        for (Waiting_room r: waitingRooms) {
            lista.add(r.getSite());
        }
        return new ArrayAdapter<String>(ctx, android.R.layout.simple_dropdown_item_1line, lista);
    }

    public boolean addWaitingRoom(Waiting_room waitingRoom) {
        if (!existsById(waitingRoom.getId())) {
            waitingRooms.add(waitingRoom);
            notifyDataSetChanged();
            return true;
        }

        return false;
    }

    public boolean existsById(Integer id) {
        for (Waiting_room waitingRoom: waitingRooms)
            if (waitingRoom.getId().equals(id))
                return true;

        return false;
    }

    public void removeWaitingRoom(Integer id) {
        Iterator<Waiting_room> iterator = waitingRooms.iterator();
        while (iterator.hasNext()) {
            Waiting_room waitingRoom = iterator.next();
            if (waitingRoom.getId().equals(id)) {
                waitingRooms.remove(waitingRoom);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void setList(ArrayList<Waiting_room> list) {
        waitingRooms.clear();
        waitingRooms.addAll(list);
        notifyDataSetChanged();
    }
    public void setOnClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }
    public void setOnCheckClickHandler(HelperClass.AdapterOnClickHandler checkClickHandler) {
        this.checkClickHandler = checkClickHandler;
    }
    public void setOnRemoveClickHandler(HelperClass.AdapterOnCancelClickHandler removeClickHandler) {
        this.removeClickHandler = removeClickHandler;
    }
    public void addItems(ArrayList<Waiting_room> symptoms) {
        this.waitingRooms.addAll(symptoms);
        notifyDataSetChanged();
    }

    public class RemoveViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView imgThumb;
        private final TextView txtPetName;
        private final TextView txtPetOwner;
        private final TextView txtAttendedUser;
        private final ImageView imgRemove;
        private final ImageView imgCheck;
//        private final View divLine;

        public RemoveViewHolder(View view) {
            super(view);
            imgThumb = view.findViewById(R.id.img_thumb);
            txtPetName = view.findViewById(R.id.txt_pet_name);
            txtPetOwner = view.findViewById(R.id.txt_pet_owners);
            txtAttendedUser = view.findViewById(R.id.txt_attended_by);
//            divLine = view.findViewById(R.id.div_line);

            imgCheck = view.findViewById(R.id.img_check);
            imgCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (checkClickHandler != null) {
                        int pos = getAdapterPosition();
                        Waiting_room waitingRoom = waitingRooms.get(pos);
                        checkClickHandler.onClick(waitingRoom, imgCheck, pos);
                    }
                }
            });

            imgRemove = view.findViewById(R.id.img_remove);
            imgRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (removeClickHandler != null) {
                        int pos = getAdapterPosition();
                        Waiting_room waitingRoom = waitingRooms.get(pos);
                        removeClickHandler.onCancelClick(waitingRoom, imgRemove, pos);
                    }
                }
            });

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickHandler != null) {
                int pos = getAdapterPosition();
                Waiting_room waitingRoom = waitingRooms.get(pos);
                clickHandler.onClick(waitingRoom, this.itemView, pos);
            }
        }
    }

}
