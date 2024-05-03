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

public class DailyCashDetailsAdapter extends RecyclerView.Adapter<DailyCashDetailsAdapter.DailyCashDetailsViewHolder> {

    private final ArrayList<DailyCashDetails.DailyCashDetailsItem> daily_cash_items;

    public DailyCashDetailsAdapter(ArrayList<DailyCashDetails.DailyCashDetailsItem> daily_cashes) {
        this.daily_cash_items = daily_cashes;
    }

    @Override
    public DailyCashDetailsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_daily_cash_details;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new DailyCashDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DailyCashDetailsViewHolder holder, int position) {
        DailyCashDetails.DailyCashDetailsItem daily_cash = this.daily_cash_items.get(position);

        String type = "";
        switch (daily_cash.getType()) {
            case "SELL":
                type = "Ingresos por ventas. ";
                break;
            case "MANUAL_CASH_IN":
                type = "Ingresos manuales. ";
                break;
            case "MANUAL_CASH_OUT":
                type = "Egresos manuales. ";
                break;
            case "SPENDING":
                type = "Gastos. ";
                break;
            case "PURCHASE":
                type = "Egresos por compras. ";
                break;
        }
        type += daily_cash.getName();
        holder.txt_title.setText(type);

        holder.txt_total.setText(HelperClass.formatCurrency(daily_cash.getAmount()));
    }

    @Override
    public int getItemCount() {
        return daily_cash_items.size();
    }

    public class DailyCashDetailsViewHolder extends RecyclerView.ViewHolder {
        private final TextView txt_title;
        private final TextView txt_total;

        public DailyCashDetailsViewHolder(View view) {
            super(view);
            txt_title = view.findViewById(R.id.txt_title);
            txt_total = view.findViewById(R.id.txt_total);
        }
    }
}