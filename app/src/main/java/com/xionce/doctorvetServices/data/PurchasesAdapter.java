package com.xionce.doctorvetServices.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.xionce.doctorvetServices.R;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.security.Provider;
import java.util.ArrayList;

public class PurchasesAdapter extends RecyclerView.Adapter<PurchasesAdapter.PurchasesHolder> {

    private HelperClass.AdapterOnClickHandler clickHandler;
    private final ArrayList<Purchase> purchases;

    public PurchasesAdapter(ArrayList<Purchase> purchases) {
        this.purchases = purchases;
    }

    public PurchasesAdapter(ArrayList<Purchase> purchases, HelperClass.AdapterOnClickHandler clickHandler) {
        this.purchases = purchases;
        this.clickHandler = clickHandler;
    }

    public void setOnClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    @Override
    public PurchasesHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_purchase;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new PurchasesHolder(view);
    }

    @Override
    public void onBindViewHolder(final PurchasesHolder purchasesHolder, int position) {
        Purchase purchase = this.purchases.get(position);
        purchasesHolder.txt_date.setText(HelperClass.getDateInLocaleShort(purchase.getDate()));

        String receipt = "";
        if (purchase.getReceipt() != null)
            receipt = purchase.getReceipt() + " ";

        if (purchase.getDeleted() == 1)
            receipt += "ELIMINADO";

        purchasesHolder.txt_receipt.setText(receipt);
        if (receipt.isEmpty())
            purchasesHolder.txt_receipt.setVisibility(View.INVISIBLE);

        purchasesHolder.txt_total.setText("Total: " + HelperClass.formatCurrency(purchase.getTotal()));
        purchasesHolder.txt_balance.setText("Balance: " + HelperClass.formatCurrency(purchase.getBalance()));

        //provider
        Product_provider provider = purchase.getProvider();
        if (provider != null) {
            purchasesHolder.txt_provider.setVisibility(View.VISIBLE);
            purchasesHolder.txt_provider.setText("A: " + provider.getName());
        } else {
            purchasesHolder.txt_provider.setVisibility(View.GONE);
        }
    }

    public void addItems(ArrayList<Purchase> items) {
        this.purchases.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return purchases.size();
    }

    public class PurchasesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView txt_date;
        private final TextView txt_receipt;
        private final TextView txt_total;
        private final TextView txt_balance;
        private final TextView txt_provider;

        public PurchasesHolder(View view) {
            super(view);
            txt_date = view.findViewById(R.id.txt_date);
            txt_receipt = view.findViewById(R.id.txt_receipt);
            txt_total = view.findViewById(R.id.txt_total);
            txt_balance = view.findViewById(R.id.txt_balance);
            txt_provider = view.findViewById(R.id.txt_provider);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler == null)
                return;

            int pos = getAdapterPosition();
            Purchase sell = purchases.get(pos);
            clickHandler.onClick(sell, this.itemView, pos);
        }
    }
}