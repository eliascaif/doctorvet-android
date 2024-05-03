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

public class CashMovementsAdapter extends RecyclerView.Adapter<CashMovementsAdapter.CashMovementsHolder> {

    private HelperClass.AdapterOnClickHandler clickHandler;
    private ArrayList<Cash_movement> cashMovements;

    public CashMovementsAdapter(ArrayList<Cash_movement> cashMovements) {
        this.cashMovements = cashMovements;
    }
    public void setOnClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    @Override
    public CashMovementsHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_cash_movement;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new CashMovementsHolder(view);
    }

    @Override
    public void onBindViewHolder(final CashMovementsHolder holder, int position) {
        Cash_movement cashMovement = this.cashMovements.get(position);
        holder.txt_date.setText(HelperClass.getDateInLocaleShort(cashMovement.getDate()));

        String type = "";
        if (cashMovement.getType() == Cash_movement.manual_cash_type.MANUAL_CASH_IN)
            type = "Ingreso ";
        else
            type = "Egreso ";

        if (cashMovement.getDeleted() == 1)
            type += "ELIMINADO";

        holder.txt_type.setText(type);
        holder.txt_total.setText("Total: " + HelperClass.formatCurrency(cashMovement.getAmount()));

        holder.txt_reason.setVisibility(View.GONE);
        if (cashMovement.getReason() != null) {
            holder.txt_reason.setVisibility(View.VISIBLE);
            holder.txt_reason.setText(cashMovement.getReason());
        }
    }

    public void addItems(ArrayList<Cash_movement> items) {
        this.cashMovements.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return cashMovements.size();
    }

    public void updateList(ArrayList<Cash_movement> filterList) {
        cashMovements = new ArrayList<Cash_movement>();
        cashMovements.addAll(filterList);
        notifyDataSetChanged();
    }

    public class CashMovementsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView txt_date;
        private final TextView txt_type;
        private final TextView txt_total;
        private final TextView txt_reason;

        public CashMovementsHolder(View view) {
            super(view);
            txt_date = view.findViewById(R.id.txt_date);
            txt_type = view.findViewById(R.id.txt_type);
            txt_total = view.findViewById(R.id.txt_total);
            txt_reason = view.findViewById(R.id.txt_reason);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler == null)
                return;

            int pos = getAdapterPosition();
            Cash_movement cashMovement = cashMovements.get(pos);
            clickHandler.onClick(cashMovement, this.itemView, pos);
        }
    }
}