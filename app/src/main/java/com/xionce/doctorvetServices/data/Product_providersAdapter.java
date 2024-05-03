package com.xionce.doctorvetServices.data;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xionce.doctorvetServices.DoctorVetApp;
import com.xionce.doctorvetServices.R;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.util.ArrayList;

public class Product_providersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<Product_provider> providers;
    private final DoctorVetApp.Adapter_types adapterType;
    private HelperClass.AdapterOnClickHandler clickHandler;
    private HelperClass.AdapterOnClickHandler debtDetailsHandler;
    private HelperClass.AdapterOnClickHandler debtPayHandler;

    public Product_providersAdapter(ArrayList<Product_provider> providers, DoctorVetApp.Adapter_types adapterType) {
        this.providers = providers;
        this.adapterType = adapterType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch (adapterType) {
            case SEARCH:
                View view_search = inflater.inflate(R.layout.list_item_med, viewGroup, false);
                return new SearchHolder(view_search);
            case PROVIDERS_CREDITORS:
                View view_creditor = inflater.inflate(R.layout.list_item_provider_creditor, viewGroup, false);
                return new CreditorHolder(view_creditor);
            default:
                throw new RuntimeException("non-existent viewType");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Product_provider provider = providers.get(position);
        switch (adapterType) {
            case SEARCH:
                bindSearch(provider, (SearchHolder)holder, position);
                break;
            case PROVIDERS_CREDITORS:
                bindCreditors(provider, (CreditorHolder)holder, position);
                break;
        }
    }

    private void bindSearch(Product_provider product_provider, SearchHolder holder, int position) {
        holder.txt_title.setText(product_provider.getName());
        holder.txt_subtitle.setText(product_provider.getEmail());
        DoctorVetApp.get().setThumb(product_provider.getThumb_url(), holder.img_thumb, R.drawable.ic_provider);
    }
    private void bindCreditors(Product_provider provider, CreditorHolder holder, int position) {
        holder.txt_name.setText(provider.getName());
        holder.txt_email.setText(provider.getEmail());

        DoctorVetApp.get().setThumb(provider.getThumb_url(), holder.img_image, R.drawable.ic_provider);

        holder.txt_total_balance.setText("Balance: " + DoctorVetApp.get().toCurrency(provider.getBalance()));

        holder.txt_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (debtDetailsHandler != null)
                    debtDetailsHandler.onClick(provider, view, position);
            }
        });

        holder.txt_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (debtPayHandler != null)
                    debtPayHandler.onClick(provider, view, position);
            }
        });
    }

    public void setOnDebtDetailsClickHandler(HelperClass.AdapterOnClickHandler debtDetailsHandler) {
        this.debtDetailsHandler = debtDetailsHandler;
    }
    public void setOnDebtPayClickHandler(HelperClass.AdapterOnClickHandler debtPayHandler) {
        this.debtPayHandler = debtPayHandler;
    }


    @Override
    public int getItemCount() {
        return providers.size();
    }

    public void setOnClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    public void addItems(ArrayList<Product_provider> items) {
        providers.addAll(items);
        notifyDataSetChanged();
    }

    public class SearchHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView txt_title;
        private final TextView txt_subtitle;
        private final ImageView img_thumb;

        public SearchHolder(View view) {
            super(view);
            txt_title = view.findViewById(R.id.txt_title);
            txt_subtitle = view.findViewById(R.id.txt_subtitle);
            img_thumb = view.findViewById(R.id.img_thumb);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler != null) {
                int pos = getAdapterPosition();
                Product_provider provider = providers.get(pos);
                clickHandler.onClick(provider, this.itemView, pos);
            }
        }

    }
    public class CreditorHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView img_image;
        private final TextView txt_name;
        private final TextView txt_email;
        private final TextView txt_details;
        private final TextView txt_pay;
        private final TextView txt_total_balance;

        public CreditorHolder(View view) {
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
            Product_provider provider = providers.get(pos);
            clickHandler.onClick(provider, this.itemView, pos);
        }
    }

}
