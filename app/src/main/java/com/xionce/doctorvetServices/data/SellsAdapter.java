package com.xionce.doctorvetServices.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.xionce.doctorvetServices.R;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.util.ArrayList;

public class SellsAdapter extends RecyclerView.Adapter<SellsAdapter.SellsHolder> {

    private HelperClass.AdapterOnClickHandler clickHandler;
    private final ArrayList<Sell> sells;

    public SellsAdapter(ArrayList<Sell> sells) {
        this.sells = sells;
    }

    public SellsAdapter(ArrayList<Sell> sells, HelperClass.AdapterOnClickHandler clickHandler) {
        this.sells = sells;
        this.clickHandler = clickHandler;
    }

    public void setOnClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    @Override
    public SellsHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_sell;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new SellsHolder(view);
    }

    @Override
    public void onBindViewHolder(final SellsHolder sellsHolder, int position) {
        Sell sell = this.sells.get(position);
        sellsHolder.txt_date.setText(HelperClass.getDateInLocaleShort(sell.getDate()));

        String receipt = sell.getReceipt(); //sell.getSell_point_number().toString() + "-" + sell.getReceipt_number().toString();

        if (sell.getDeleted() == 1)
            receipt += " ELIMINADO";

        sellsHolder.txt_sell_number.setText(receipt);

        sellsHolder.txt_total.setText("Total: " + HelperClass.formatCurrency(sell.getTotal()));
        sellsHolder.txt_balance.setText("Balance: " + HelperClass.formatCurrency(sell.getBalance()));
    }

    public void addItems(ArrayList<Sell> items) {
        this.sells.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return sells.size();
    }

    public class SellsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView txt_date;
        private final TextView txt_sell_number;
        private final TextView txt_total;
        private final TextView txt_balance;

        public SellsHolder(View view) {
            super(view);
            txt_date = view.findViewById(R.id.txt_date);
            txt_sell_number = view.findViewById(R.id.txt_sell_number);
            txt_total = view.findViewById(R.id.txt_total);
            txt_balance = view.findViewById(R.id.txt_balance);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler == null)
                return;

            int pos = getAdapterPosition();
            Sell sell = sells.get(pos);
            clickHandler.onClick(sell, this.itemView, pos);
        }
    }
}