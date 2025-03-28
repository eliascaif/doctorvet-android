package com.xionce.doctorvetServices.data;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xionce.doctorvetServices.DoctorVetApp;
import com.xionce.doctorvetServices.R;
import com.xionce.doctorvetServices.ViewPetActivity;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.util.ArrayList;
import java.util.Calendar;

public class Pet_supplyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public enum Pet_supplyAdapter_types { EDIT_SUPPLY, PENDING, PENDING_COMM, NEW_SUPPLY, PLANNING_ACTIVITY, PLANNING_ACTIVITY_NO_EDIT }

    private final Pet_supplyAdapter_types adapter_type;
    private final ArrayList<Pet_supply> petsupplies;
    private final ArrayList<Integer> selected_positions;
    private HelperClass.AdapterOnClickHandler clickHandler;
    private HelperClass.AdapterOnRemoveItemHandler removeItemHandler;
    private HelperClass.AdapterOnClickHandler checkItemHandler;

    private HelperClass.AdapterOnClickHandler callItemHandler;
    private HelperClass.AdapterOnClickHandler whatsappItemHandler;
    private HelperClass.AdapterOnClickHandler emailItemHandler;
    private HelperClass.AdapterOnClickHandler smsItemHandler;

    public Pet_supplyAdapter(ArrayList<Pet_supply> petsupplies, Pet_supplyAdapter_types adapter_type) {
        this.petsupplies = petsupplies;
        this.selected_positions = new ArrayList<>();
        this.adapter_type = adapter_type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch (adapter_type) {
            case NEW_SUPPLY:
                View view_tab_new_supply = inflater.inflate(R.layout.list_item_supply_new, viewGroup, false);
                return new SupplyNewHolder(view_tab_new_supply);
            case EDIT_SUPPLY:
                View view_tab_supply = inflater.inflate(R.layout.list_item_supply, viewGroup, false);
                return new SupplyHolder(view_tab_supply);
            case PENDING:
                View view_pending = inflater.inflate(R.layout.list_item_supply_pending, viewGroup, false);
                return new SupplyPendingHolder(view_pending);
            case PENDING_COMM:
                View view_pending_com = inflater.inflate(R.layout.list_item_supply_pending_com, viewGroup, false);
                return new SupplyPendingCommHolder(view_pending_com);
            case PLANNING_ACTIVITY:
            case PLANNING_ACTIVITY_NO_EDIT:
                View view_sell = inflater.inflate(R.layout.list_item_supply_planning_activity, viewGroup, false);
                return new SupplyPlanningActivityHolder(view_sell);
            default:
                throw new RuntimeException("non-existent viewType");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Pet_supply petsupply = petsupplies.get(position);

        switch (adapter_type) {
            case NEW_SUPPLY:
                bindNewSupply(petsupply, (SupplyNewHolder)holder, position);
                break;
            case EDIT_SUPPLY:
                bindEditSupply(petsupply, (SupplyHolder)holder, position);
                break;
            case PENDING:
                bindPending(petsupply, (SupplyPendingHolder) holder, position);
                break;
            case PENDING_COMM:
                bindPendingComm(petsupply, (SupplyPendingCommHolder) holder, position);
                break;
            case PLANNING_ACTIVITY:
                bindPlanningActivitySupply(petsupply, (SupplyPlanningActivityHolder)holder, position);
                break;
            case PLANNING_ACTIVITY_NO_EDIT:
                bindPlanningActivitySupplyNoEdit(petsupply, (SupplyPlanningActivityHolder)holder, position);
                break;
        }
    }

    private void bindNewSupply(Pet_supply petsupply, SupplyNewHolder holder, int position) {
        holder.txt_fecha_tentativa.setText(HelperClass.getDateInLocale(petsupply.getDate_tentative(), holder.txt_fecha_tentativa.getContext()));
        holder.txt_product.setText(petsupply.getProduct().getName());

        DoctorVetApp.get().setThumb(petsupply.getProduct().getThumb_url(), holder.img_thumb, R.drawable.ic_product_light);

        Context ctx = holder.img_thumb.getContext();
        if (petsupply.getDate_supply() == null) {
            holder.txt_fecha_tentativa.append(". " + ctx.getString(R.string.no_suministrado));
        } else {
            holder.txt_fecha_tentativa.append(". " + ctx.getString(R.string.suministrado));
        }

        holder.img_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem(position);
                //if (removeItemHandler == null) return;
                //removeItemHandler.onRemoveItem(petsupply, view, holder.getAdapterPosition());
            }
        });
    }
    private void bindEditSupply(Pet_supply petsupply, SupplyHolder holder, int position) {
        holder.txt_fecha_tentativa.setText(HelperClass.getDateInLocale(petsupply.getDate_tentative(), holder.txt_fecha_tentativa.getContext()));
        holder.txt_product.setText(petsupply.getProduct().getName());

        DoctorVetApp.get().setThumb(petsupply.getProduct().getThumb_url(), holder.img_thumb, R.drawable.ic_product_light);

        Context ctx = holder.img_thumb.getContext();
        if (petsupply.getDate_supply() == null) {
            holder.txt_fecha_tentativa.append(". " + ctx.getString(R.string.no_suministrado));
        } else {
            holder.txt_fecha_tentativa.append(". " + ctx.getString(R.string.suministrado));
        }

        holder.img_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (removeItemHandler == null) return;

                removeItemHandler.onRemoveItem(petsupply, view, holder.getAdapterPosition());
                //deleteItem(holder.getAdapterPosition());
            }
        });

        holder.img_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkItemHandler == null) return;

                checkItemHandler.onClick(petsupply, view, holder.getAdapterPosition());
            }
        });

//        holder.viewHolder.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                if (longClickHandler == null) return false;
//                longClickHandler.onLongClick(petsupply, v, holder.getAdapterPosition());
//                return true;
//            }
//        });

        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickHandler == null) return;
                clickHandler.onClick(petsupply, v, holder.getAdapterPosition());
            }
        });

        toggleCheckedColor(holder, holder.getAdapterPosition());
        //displayImage(mascotasSuministroAdapterViewHolder, mascota_suministro);

        if (petsupply.id_supplied()) {
            holder.img_check.setVisibility(View.GONE);
        }
    }
    private void bindPending(Pet_supply petsupply, SupplyPendingHolder holder, int position) {
        DoctorVetApp.get().setThumb(petsupply.getProduct().getThumb_url(), holder.img_thumb, R.drawable.ic_product_light);

        Context ctx = holder.img_thumb.getContext();
        holder.txt_name_pet.setText(petsupply.getPet().getName());
        holder.txt_name_pet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx, ViewPetActivity.class);
                intent.putExtra(DoctorVetApp.INTENT_VALUES.PET_ID.name(), petsupply.getPet().getId());
                ctx.startActivity(intent);
            }
        });
        holder.img_thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx, ViewPetActivity.class);
                intent.putExtra(DoctorVetApp.INTENT_VALUES.PET_ID.name(), petsupply.getPet().getId());
                ctx.startActivity(intent);
            }
        });

        holder.txt_pet_owners.setText("De: " + petsupply.getPet().getOwnersNames());

        String info = "";
        info = petsupply.getProduct().getShort_name() + ". ";
        info += petsupply.getUser().getName() + ". ";
        holder.txt_info.setText(info);

        holder.img_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkItemHandler != null)
                    checkItemHandler.onClick(petsupply, view, holder.getAdapterPosition());
            }
        });
    }
    private void bindPendingComm(Pet_supply petsupply, SupplyPendingCommHolder holder, int position) {
        DoctorVetApp.get().setThumb(petsupply.getProduct().getThumb_url(), holder.img_thumb, R.drawable.ic_product_light);

        holder.txt_name_pet.setText(petsupply.getPet().getName());
        holder.txt_pet_owners.setText("De: " + petsupply.getPet().getOwnersNames());

        String info = "";
        info = petsupply.getProduct().getShort_name() + ". ";
        info += petsupply.getUser().getName() + ". ";
        info += "A suministar: " + HelperClass.getDateInLocaleShort(petsupply.getDate_tentative()) + ". ";
        holder.txt_info.setText(info);

        holder.img_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkItemHandler != null)
                    checkItemHandler.onClick(petsupply, view, holder.getAdapterPosition());
            }
        });

        holder.img_com_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callItemHandler != null)
                    callItemHandler.onClick(petsupply, view, holder.getAdapterPosition());
            }
        });
        holder.img_com_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (whatsappItemHandler != null)
                    whatsappItemHandler.onClick(petsupply, view, holder.getAdapterPosition());
            }
        });
        holder.img_com_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emailItemHandler != null)
                    emailItemHandler.onClick(petsupply, view, holder.getAdapterPosition());
            }
        });
        holder.img_com_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (smsItemHandler != null)
                    smsItemHandler.onClick(petsupply, view, holder.getAdapterPosition());
            }
        });

    }
    private void bindPlanningActivitySupply(Pet_supply petsupply, SupplyPlanningActivityHolder holder, int position) {
        DoctorVetApp.get().setThumb(petsupply.getPet().getThumb_url(), holder.img_thumb, R.drawable.ic_pets_light);
        holder.txt_pet.setText(petsupply.getPet().getName());
        holder.txt_product.setText(petsupply.getProduct().getName());
        holder.txt_date_tentative.setText("A suminstrar: " + HelperClass.getDateInLocale(petsupply.getDate_tentative(), holder.txt_date_tentative.getContext()));

        holder.img_remove.setVisibility(View.GONE);
        if (petsupply.getPlanning_activity_new() != null && petsupply.getPlanning_activity_new() == 1) {
            holder.txt_info.setText("Suministro nuevo");
            holder.img_remove.setVisibility(View.VISIBLE);
        } else {
            holder.txt_info.setText("Suministro existente");
            if (petsupply.getDate_supply() != null)
                holder.txt_info.setText("Suministro existente que se marcará como suministrado en base a un producto/servicio de la venta");
        }

        holder.img_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem(position);
            }
        });

    }
    private void bindPlanningActivitySupplyNoEdit(Pet_supply petsupply, SupplyPlanningActivityHolder holder, int position) {
        DoctorVetApp.get().setThumb(petsupply.getPet().getThumb_url(), holder.img_thumb, R.drawable.ic_pets_light);
        holder.txt_pet.setText(petsupply.getPet().getName());
        holder.txt_product.setText(petsupply.getProduct().getName());
        holder.txt_date_tentative.setText("A suminstrar: " + HelperClass.getDateInLocale(petsupply.getDate_tentative(), holder.txt_date_tentative.getContext()));

        holder.img_remove.setVisibility(View.GONE);
        if (petsupply.getPlanning_activity_new() != null && petsupply.getPlanning_activity_new() == 1) {
            holder.txt_info.setText("Suministro nuevo");
        } else {
            holder.txt_info.setText("Suministro existente que se marcará como suministrado en base a un producto/servicio de la venta");
        }
    }

    private int getSuministroColor(Pet_supply petsupply) {
        if (petsupply.getDate_supply() == null) {
            //no esta puesta
            //if (mascota_suministro.getFecha_tentativa()..after(HelperClass.getDateWithoutTimeUsingCalendar() /*Calendar.getInstance().getTime()*/)){

            if (petsupply.getDate_tentative().compareTo(HelperClass.getDateWithoutTimeUsingCalendar()) >= 0) {
                //pendiente
                return Color.parseColor("#EBFFB8"); //4
            } else {
                //vencida
                return Color.parseColor("#FFC0BE"); //4
            }
        } else {
            //esta suministrado
            return Color.parseColor("#CBFFC1"); //4
        }

    }

    @Override
    public int getItemCount() {
        return petsupplies.size();
    }

    public void addItems(ArrayList<Pet_supply> items) {
        this.petsupplies.addAll(items);

        notifyDataSetChanged();
    }

    public ArrayList<Pet_supply> getArrayList() {
        return petsupplies;
    }
    public Pet_supply getItem(int position) {
        return petsupplies.get(position);
    }

    private void toggleCheckedColor(RecyclerView.ViewHolder holder, int position) {
        SupplyHolder mascotasHolder = (SupplyHolder)holder;
        Pet_supply petsupply = petsupplies.get(position);

        if (selected_positions.contains(position)) {
            mascotasHolder.contenedor.setBackgroundColor(Color.GRAY);
        } else {
            mascotasHolder.contenedor.setBackgroundColor(getSuministroColor(petsupply));
        }
    }
    public void clearSelections() {
        selected_positions.clear();
        notifyDataSetChanged();
    }

    public void addItem(Pet_supply petsupply) {
        petsupplies.add(petsupply);
        clearSelections();
        notifyDataSetChanged();
    }
    public void checkItem(int position) {
        Pet_supply petsupply = petsupplies.get(position);
        if (petsupply.getDate_supply() == null) {
            petsupply.setDate_supply(Calendar.getInstance().getTime());
        } else {
            petsupply.setDate_supply(null);
        }

        notifyItemChanged(position);
    }
    public void deleteItem(int position) {
        petsupplies.remove(position);
        notifyItemRemoved(position);
    }
    public void setOnClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }
    public void setOnRemoveItemHandler(HelperClass.AdapterOnRemoveItemHandler removeItemHandler) {
        this.removeItemHandler = removeItemHandler;
    }
    public void setOnCheckItemHandler(HelperClass.AdapterOnClickHandler checkItemHandler) {
        this.checkItemHandler = checkItemHandler;
    }
    public void setComHandlers(HelperClass.AdapterOnClickHandler callItemHandler,
                               HelperClass.AdapterOnClickHandler whatsappItemHandler,
                               HelperClass.AdapterOnClickHandler emailItemHandler,
                               HelperClass.AdapterOnClickHandler smsItemHandler) {
        this.callItemHandler = callItemHandler;
        this.whatsappItemHandler = whatsappItemHandler;
        this.emailItemHandler = emailItemHandler;
        this.smsItemHandler = smsItemHandler;
    }

    public class SupplyNewHolder extends RecyclerView.ViewHolder {
        private final ImageView img_thumb;
        private final TextView txt_product;
        private final TextView txt_fecha_tentativa;
        private final ImageView img_remove;

        public SupplyNewHolder(View view) {
            super(view);
            img_thumb = view.findViewById(R.id.img_thumb);
            txt_product = view.findViewById(R.id.txt_name_product);
            txt_fecha_tentativa = view.findViewById(R.id.txt_date);
            img_remove = view.findViewById(R.id.img_remove);
        }
    }
    public class SupplyHolder extends RecyclerView.ViewHolder {
        private final ImageView img_thumb;
        private final TextView txt_product;
        private final TextView txt_fecha_tentativa;
        private final ImageView img_remove;
        private final ImageView img_check;
        private final View viewHolder;
        private final ConstraintLayout contenedor;

        public SupplyHolder(View view) {
            super(view);
            img_thumb = view.findViewById(R.id.img_thumb);
            txt_product = view.findViewById(R.id.txt_name_product);
            txt_fecha_tentativa = view.findViewById(R.id.txt_date);
            img_check = view.findViewById(R.id.img_check);
            img_remove = view.findViewById(R.id.img_remove);
            contenedor = view.findViewById(R.id.prueba_linear);
            viewHolder = view;
        }
    }
    public class SupplyPendingHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {
        private final ImageView img_thumb;
        private final TextView txt_name_pet;
        private final TextView txt_pet_owners;
        private final TextView txt_info;
        private final ImageView img_check;

        public SupplyPendingHolder(View view) {
            super(view);
            img_thumb = view.findViewById(R.id.img_thumb);
            txt_name_pet = view.findViewById(R.id.txt_pet_name);
            txt_pet_owners = view.findViewById(R.id.txt_pet_owners);
            txt_info = view.findViewById(R.id.txt_info);
            img_check = view.findViewById(R.id.img_check);
        }
    }
    public class SupplyPendingCommHolder extends RecyclerView.ViewHolder {
        private final ImageView img_thumb;
        private final TextView txt_name_pet;
        private final TextView txt_pet_owners;
        private final TextView txt_info;
        private final LinearLayoutCompat linear_communication;
        private final ImageView img_check;
        private final ImageView img_com_call;
        private final ImageView img_com_whatsapp;
        private final ImageView img_com_email;
        private final ImageView img_com_sms;

        private boolean collapsed = true;

        public SupplyPendingCommHolder(View view) {
            super(view);
            img_thumb = view.findViewById(R.id.img_thumb);
            txt_name_pet = view.findViewById(R.id.txt_pet_name);
            txt_pet_owners = view.findViewById(R.id.txt_pet_owners);
            txt_info = view.findViewById(R.id.txt_info);
            img_check = view.findViewById(R.id.img_check);

            img_com_call = view.findViewById(R.id.btn_com_telefono);
            img_com_whatsapp = view.findViewById(R.id.btn_com_whatsapp);
            img_com_email = view.findViewById(R.id.btn_com_email);
            img_com_sms = view.findViewById(R.id.btn_com_sms);
            linear_communication = view.findViewById(R.id.linear_comunication);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickHandler == null)
                        return;

                    int pos = getAdapterPosition();
                    Pet_supply pet_supply = petsupplies.get(pos);
                    clickHandler.onClick(pet_supply, view, pos);
                }
            });

            ImageView img_comm = view.findViewById(R.id.img_comm);
            img_comm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (collapsed) {
                        linear_communication.setVisibility(View.VISIBLE);
                        collapsed = false;
                    } else {
                        linear_communication.setVisibility(View.GONE);
                        collapsed = true;
                    }
                }
            });
        }
    }
    public class SupplyPlanningActivityHolder extends RecyclerView.ViewHolder {
        private final ImageView img_thumb;
        private final TextView txt_pet;
        private final TextView txt_product;
        private final TextView txt_date_tentative;
        private final TextView txt_info;
        private final ImageView img_remove;

        public SupplyPlanningActivityHolder(View view) {
            super(view);
            img_thumb = view.findViewById(R.id.img_thumb);
            txt_pet = view.findViewById(R.id.txt_pet);
            txt_product = view.findViewById(R.id.txt_product);
            txt_date_tentative = view.findViewById(R.id.txt_date_tentative);
            txt_info = view.findViewById(R.id.txt_info);
            img_remove = view.findViewById(R.id.img_remove);
        }
    }

}