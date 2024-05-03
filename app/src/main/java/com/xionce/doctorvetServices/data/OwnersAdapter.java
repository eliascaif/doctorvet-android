package com.xionce.doctorvetServices.data;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xionce.doctorvetServices.DoctorVetApp;
import com.xionce.doctorvetServices.R;
import com.xionce.doctorvetServices.ViewPetActivity;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.util.ArrayList;
import java.util.List;

public class OwnersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<Owner> owners;
    private DoctorVetApp.Adapter_types adapterType;
    private HelperClass.AdapterOnClickHandler clickHandler;
    private HelperClass.AdapterOnClickHandler add_email_clickHandler;
    private HelperClass.AdapterOnClickHandler add_telefono_clickHandler;
    private HelperClass.AdapterOnClickHandler debtDetailsHandler;
    private HelperClass.AdapterOnClickHandler debtPayHandler;
    private HelperClass.AdapterOnCancelClickHandler removeClickHandler;

    public OwnersAdapter(ArrayList<Owner> owners, DoctorVetApp.Adapter_types adapterType) {
        this.owners = owners;
        this.adapterType = adapterType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch (adapterType) {
            case EXTENDED:
                View view_extended = inflater.inflate(R.layout.list_item_owner, viewGroup, false);
                return new ExtendedAdapterViewHolder(view_extended);
            case COMPACT:
                View view_compact = inflater.inflate(R.layout.list_item_owner_min, viewGroup, false);
                return new CompactAdapterViewHolder(view_compact);
            case SEARCH:
                View view_search = inflater.inflate(R.layout.list_item_med, viewGroup, false);
                return new SearchAdapterViewHolder(view_search);
            case OWNERS_DEBTORS:
                View view_debtor = inflater.inflate(R.layout.list_item_owner_debtor, viewGroup, false);
                return new DebtorHolder(view_debtor);
            case REMOVE:
                View view_remove = inflater.inflate(R.layout.list_item_remove, viewGroup, false);
                return new RemoveViewHolder(view_remove);
            default:
                throw new RuntimeException("non-existent viewType");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Owner owner = owners.get(position);
        switch (adapterType) {
            case EXTENDED:
                bindExtended(owner, (ExtendedAdapterViewHolder)holder, position);
                break;
            case COMPACT:
                bindCompact(owner, (CompactAdapterViewHolder)holder, position);
                break;
            case SEARCH:
                bindSearch(owner, (SearchAdapterViewHolder)holder, position);
                break;
            case OWNERS_DEBTORS:
                bindDebtors(owner, (DebtorHolder)holder, position);
                break;
            case REMOVE:
                bindRemove(owner, (RemoveViewHolder)holder, position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return owners.size();
    }

    private void bindCompact(Owner owner, CompactAdapterViewHolder holder, int position) {
        Context ctx = holder.txt_name.getContext();

        DoctorVetApp.get().setThumb(owner.getThumb_url(), holder.img_image, R.drawable.ic_account_circle_light);

        holder.txt_name.setText(owner.getName());

        holder.txt_email_no_email.setOnClickListener(null);
        holder.txt_email_no_email.setTextColor(Color.BLACK);
        holder.txt_phone_no_telefono.setOnClickListener(null);
        holder.txt_phone_no_telefono.setTextColor(Color.BLACK);

        //email
        String email = owner.getEmail();
        if (email != null && !email.isEmpty()) {
            holder.txt_email_no_email.setText(email);
        } else {
            holder.txt_email_no_email.setText(R.string.agregar_email);
            holder.txt_email_no_email.setTextColor(Color.BLUE);
            holder.txt_email_no_email.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (add_email_clickHandler != null)
                        add_email_clickHandler.onClick(owner, holder.txt_phone_no_telefono, position);
                }
            });
        }

        //telefono
        String telefono = owner.getPhone();
        if (telefono != null && !telefono.isEmpty()) {
            holder.txt_phone_no_telefono.setText(telefono);
        } else {
            holder.txt_phone_no_telefono.setText(R.string.agregar_movil);
            holder.txt_phone_no_telefono.setTextColor(Color.BLUE);
            holder.txt_phone_no_telefono.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (add_telefono_clickHandler != null)
                        add_telefono_clickHandler.onClick(owner, holder.txt_phone_no_telefono, position);
                }
            });
        }

        //ultima visita
        DoctorVetApp.get().setLastVisit(owner.getLast_visit(), holder.txt_last_visit);

        holder.txt_reason.setText(DoctorVetApp.get().reasonToString(owner.getReason()));

        if (DoctorVetApp.get().getVet().getMultiuser() > 1 && owner.getUser() != null) {
            holder.txt_user.setVisibility(View.VISIBLE);
            holder.txt_user.setText(owner.getUser().getName());
        }

        holder.linear_pets.removeAllViews();
        if (owner.getPets().size() > 0) {
            for (Pet pet : owner.getPets()) {
                final Integer id_pet = pet.getId();
                TextView txt_pet = new TextView(ctx);
                txt_pet.setTypeface(txt_pet.getTypeface(), Typeface.BOLD);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0,0,16,0);
                txt_pet.setLayoutParams(params);
                txt_pet.setText(pet.getName());
                holder.linear_pets.addView(txt_pet);
                txt_pet.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ctx, ViewPetActivity.class);
                        intent.putExtra(DoctorVetApp.INTENT_VALUES.PET_ID.name(), id_pet);
                        ctx.startActivity(intent);
                    }
                });
            }
        }
        else {
            holder.horizontalScrollView.setVisibility(View.GONE);
            Resources r = holder.bottom_line.getResources();
            int px = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    24,
                    r.getDisplayMetrics()
            );
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.bottom_line.getLayoutParams();
            params.topMargin = px;
        }
    }
    private void bindExtended(Owner owner, ExtendedAdapterViewHolder holder, int position) {
        Context ctx = holder.txt_name.getContext();

        DoctorVetApp.get().setThumb(owner.getThumb_url(), holder.img_image, R.drawable.ic_account_circle_light);

        holder.txt_name.setText(owner.getName());

        holder.txt_email_no_email.setOnClickListener(null);
        holder.txt_email_no_email.setTextColor(Color.BLACK);
        holder.txt_phone_no_telefono.setOnClickListener(null);
        holder.txt_phone_no_telefono.setTextColor(Color.BLACK);

        //email
        String email = owner.getEmail();
        if (email != null && !email.isEmpty()) {
            holder.txt_email_no_email.setText(email);
        } else {
            holder.txt_email_no_email.setText(R.string.agregar_email);
            holder.txt_email_no_email.setTextColor(Color.BLUE);
            holder.txt_email_no_email.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (add_email_clickHandler != null)
                        add_email_clickHandler.onClick(owner, holder.txt_phone_no_telefono, position);
                }
            });
        }

        //telefono
        String telefono = owner.getPhone();
        if (telefono != null && !telefono.isEmpty()) {
            holder.txt_phone_no_telefono.setText(telefono);
        } else {
            holder.txt_phone_no_telefono.setText(R.string.agregar_movil);
            holder.txt_phone_no_telefono.setTextColor(Color.BLUE);
            holder.txt_phone_no_telefono.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (add_telefono_clickHandler != null)
                        add_telefono_clickHandler.onClick(owner, holder.txt_phone_no_telefono, position);
                }
            });
        }

        //ultima visita
        DoctorVetApp.get().setLastVisit(owner.getLast_visit(), holder.txt_last_visit);

        holder.txt_reason.setText(DoctorVetApp.get().reasonToString(owner.getReason()));

        if (DoctorVetApp.get().getVet().getMultiuser() > 1 && owner.getUser() != null) {
            holder.txt_user.setVisibility(View.VISIBLE);
            holder.txt_user.setText(owner.getUser().getName());
        }

        holder.linear_pets.removeAllViews();
        if (owner.getPets().size() > 0) {
            for (Pet pet : owner.getPets()) {
                final Integer id_pet = pet.getId();
                LayoutInflater layoutInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View mascota_min = layoutInflater.inflate(R.layout.list_item_pet_min_relative, null, false);
                TextView txt_name_pet = mascota_min.findViewById(R.id.txt_pet_name);
                txt_name_pet.setText(pet.getName());

                ImageView image_pet = mascota_min.findViewById(R.id.img_thumb);
                DoctorVetApp.get().setThumb(pet.getThumb_url(), image_pet, R.drawable.ic_dog);

                holder.linear_pets.addView(mascota_min);
                mascota_min.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ctx, ViewPetActivity.class);
                        intent.putExtra(DoctorVetApp.INTENT_VALUES.PET_ID.name(), id_pet);
                        intent.putExtra(DoctorVetApp.INTENT_UPDATE_LAST_VIEW, 1);
                        ctx.startActivity(intent);
                    }
                });
            }
        } else {
            holder.horizontalScrollView.setVisibility(View.GONE);
            Resources r = holder.bottom_line.getResources();
            int px = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    24,
                    r.getDisplayMetrics()
            );
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.bottom_line.getLayoutParams();
            params.topMargin = px;
        }
    }
    private void bindSearch(Owner owner, SearchAdapterViewHolder holder, int position) {
        holder.txt_name.setText(owner.getName());
        holder.txt_email_telefono.setText(owner.getEmailTelefono());
        DoctorVetApp.get().setThumb(owner.getThumb_url(), holder.img_image, R.drawable.ic_account_circle_light);
    }
    private void bindDebtors(Owner owner, DebtorHolder holder, int position) {
        holder.txt_name.setText(owner.getName());
        holder.txt_email.setText(owner.getEmail());

        DoctorVetApp.get().setThumb(owner.getThumb_url(), holder.img_image, R.drawable.ic_account_circle_light);

        holder.txt_total_balance.setText("Balance: " + DoctorVetApp.get().toCurrency(owner.getBalance()));

        holder.txt_details.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (debtDetailsHandler != null)
                    debtDetailsHandler.onClick(owner, view, position);
            }
        });

        holder.txt_pay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (debtPayHandler != null)
                    debtPayHandler.onClick(owner, view, position);
            }
        });
    }
    private void bindRemove(Owner owner, RemoveViewHolder holder, int position) {
        holder.txt_item_name.setText(owner.getName());
    }

    public ArrayList<Owner> getArrayList() {
        return owners;
    }
    public OwnersAutocompleteAdapter getAutocompleteAdapter(Context ctx) {
        return new OwnersAutocompleteAdapter(ctx, new ArrayList<>(owners));
    }
    public void setList(ArrayList<Owner> list) {
        owners.clear();
        owners.addAll(list);
        notifyDataSetChanged();
    }
    public void addItems(ArrayList<Owner> items) {
        owners.addAll(items);
        notifyDataSetChanged();
    }
    public void add(Owner owner) {
        for (Owner o:owners) {
            if (o.getId().equals(owner.getId()))
                return;
        }

        owners.add(owner);
        notifyDataSetChanged();
    }
    public void remove(Owner owner) {
        owners.remove(owner);
        notifyDataSetChanged();
    }

    public void setAdapterType(DoctorVetApp.Adapter_types adapterType) {
        int size = owners.size();
        owners.clear();
        notifyItemRangeRemoved(0, size);
        this.adapterType = adapterType;
        //owners.addAll(owners);
        notifyDataSetChanged();
    }
    public void setOnClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }
    public void setOnAddEmailClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.add_email_clickHandler = clickHandler;
    }
    public void setOnAddTelefonoClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.add_telefono_clickHandler = clickHandler;
    }
    public void setOnDebtDetailsClickHandler(HelperClass.AdapterOnClickHandler debtDetailsHandler) {
        this.debtDetailsHandler = debtDetailsHandler;
    }
    public void setOnDebtPayClickHandler(HelperClass.AdapterOnClickHandler debtPayHandler) {
        this.debtPayHandler = debtPayHandler;
    }
    public void setOnRemoveClickHandler(HelperClass.AdapterOnCancelClickHandler removeClickHandler) {
        this.removeClickHandler = removeClickHandler;
    }
    public static ArrayList<Owner> searchOwners(String userInput, List<Owner> owners) {
        ArrayList<Owner> filterList = new ArrayList<>();
        String name;
        String email;
        String phone;
        //String identif;

        userInput = HelperClass.normalizeString(userInput);

        for (Owner owner : owners) {
            name = HelperClass.normalizeString(owner.getName());
            if (name.contains(userInput)) {
                filterList.add(owner);
                continue;
            }
            email = owner.getEmail();
            if (email != null && email.toLowerCase().contains(userInput)) {
                filterList.add(owner);
                continue;
            }
            phone = owner.getPhone();
            if (phone != null && phone.contains(userInput)) {
                filterList.add(owner);
                continue;
            }
//            identif = propietario.getIdentificacion_regional();
//            if (identif != null && identif.contains(userInput)) {
//                filterList.add(propietario);
//                continue;
//            }
            for (Pet pet : owner.getPets()) {
                if (HelperClass.normalizeString(pet.getName()).contains(userInput)) {
                    filterList.add(owner);
                }
            }
        }
        return filterList;
    }

    public class ExtendedAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public ImageView img_image;
        public TextView txt_name;
        public TextView txt_last_visit;
        public TextView txt_email_no_email;
        public TextView txt_phone_no_telefono;
        public LinearLayout linear_pets;
        private HorizontalScrollView horizontalScrollView;
        private View bottom_line;
        private TextView txt_reason;
        private TextView txt_user;

        public ExtendedAdapterViewHolder(View view) {
            super(view);
            img_image = view.findViewById(R.id.img_thumb);
            txt_name = view.findViewById(R.id.txt_title);
            txt_last_visit = view.findViewById(R.id.txt_last_visit);
            txt_email_no_email = view.findViewById(R.id.txt_email_no_email);
            txt_phone_no_telefono = view.findViewById(R.id.txt_phone_no_telefono);
            linear_pets = view.findViewById(R.id.linear_pets);
            horizontalScrollView = view.findViewById(R.id.horizontalScrollView);
            bottom_line = view.findViewById(R.id.div_line);
            txt_reason = view.findViewById(R.id.txt_reason);
            txt_user = view.findViewById(R.id.txt_user);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler == null)
                return;

            int pos = getAdapterPosition();
            Owner owner = owners.get(pos);
            clickHandler.onClick(owner, this.itemView, pos);
        }
    }
    public class CompactAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public ImageView img_image;
        public TextView txt_name;
        public TextView txt_last_visit;
        public TextView txt_email_no_email;
        public TextView txt_phone_no_telefono;
        public LinearLayout linear_pets;
        private final HorizontalScrollView horizontalScrollView;
        private final View bottom_line;
        private final TextView txt_reason;
        private final TextView txt_user;

        public CompactAdapterViewHolder(View view) {
            super(view);
            img_image = view.findViewById(R.id.img_thumb);
            txt_name = view.findViewById(R.id.txt_title);
            txt_last_visit = view.findViewById(R.id.txt_last_visit);
            txt_email_no_email = view.findViewById(R.id.txt_email_no_email);
            txt_phone_no_telefono = view.findViewById(R.id.txt_phone_no_telefono);
            linear_pets = view.findViewById(R.id.linear_pets);
            horizontalScrollView = view.findViewById(R.id.horizontalScrollView);
            bottom_line = view.findViewById(R.id.div_line);
            txt_reason = view.findViewById(R.id.txt_reason);
            txt_user = view.findViewById(R.id.txt_user);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler == null) return;

            int pos = getAdapterPosition();
            Owner owner = owners.get(pos);
            clickHandler.onClick(owner, this.itemView, pos);
        }
    }
    public class SearchAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public ImageView img_image;
        public TextView txt_name;
        public TextView txt_email_telefono;

        public SearchAdapterViewHolder(View view) {
            super(view);
            img_image = view.findViewById(R.id.img_thumb);
            txt_name = view.findViewById(R.id.txt_title);
            txt_email_telefono = view.findViewById(R.id.txt_subtitle);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            Owner owner = owners.get(pos);
            clickHandler.onClick(owner, this.itemView, pos);
        }
    }
    public class DebtorHolder extends RecyclerView.ViewHolder implements OnClickListener {
        private final ImageView img_image;
        private final TextView txt_name;
        private final TextView txt_email;
        private final TextView txt_details;
        private final TextView txt_pay;
        private final TextView txt_total_balance;

        public DebtorHolder(View view) {
            super(view);
            img_image = view.findViewById(R.id.img_thumb);
            txt_name = view.findViewById(R.id.txt_title);
            txt_email = view.findViewById(R.id.txt_subtitle);
            txt_details = view.findViewById(R.id.txt_debt_details);
            txt_pay = view.findViewById(R.id.txt_debt_pay);
            txt_total_balance = view.findViewById(R.id.txt_total_balance);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler == null)
                return;

            int pos = getAdapterPosition();
            Owner owner = owners.get(pos);
            clickHandler.onClick(owner, this.itemView, pos);
        }
    }
    public class RemoveViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView txt_item_name;

        public RemoveViewHolder(View view) {
            super(view);
            txt_item_name = view.findViewById(R.id.txt_item_name);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            Owner owner = owners.get(pos);
            remove(owner);

            if (removeClickHandler != null)
                removeClickHandler.onCancelClick(owner, this.itemView, pos);
        }
    }

}

