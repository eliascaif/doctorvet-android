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

public class SpendingsAdapter extends RecyclerView.Adapter<SpendingsAdapter.SpendingsHolder> {

    private HelperClass.AdapterOnClickHandler clickHandler;
    private ArrayList<Spending> spendings;

    public SpendingsAdapter(ArrayList<Spending> spendings, HelperClass.AdapterOnClickHandler clickHandler) {
        this.spendings = spendings;
        this.clickHandler = clickHandler;
    }

    public void setOnClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    @Override
    public SpendingsHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_spending;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new SpendingsHolder(view);
    }

    @Override
    public void onBindViewHolder(final SpendingsHolder holder, int position) {
        Spending spending = this.spendings.get(position);
        holder.txt_date.setText(HelperClass.getDateInLocaleShort(spending.getDate()));

        String receipt = "";
//        if (spending.getReceipt() != null)
//            receipt = spending.getReceipt() + " ";

        if (spending.getDeleted() == 1)
            receipt += " ELIMINADO";

        holder.txt_date.append(receipt);
//        if (receipt.isEmpty())
//            holder.txt_receipt.setVisibility(View.INVISIBLE);

        holder.txt_total.setText("Total: " + HelperClass.formatCurrency(spending.getAmount()));

        holder.txt_reason.setVisibility(View.GONE);
        if (spending.getReason() != null) {
            holder.txt_reason.setVisibility(View.VISIBLE);
            holder.txt_reason.setText(spending.getReason());
        }
    }

    public void addItems(ArrayList<Spending> items) {
        this.spendings.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return spendings.size();
    }

    public class SpendingsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView txt_date;
        //private final TextView txt_receipt;
        private final TextView txt_total;
        private final TextView txt_reason;

        public SpendingsHolder(View view) {
            super(view);
            txt_date = view.findViewById(R.id.txt_date);
            //txt_receipt = view.findViewById(R.id.txt_receipt);
            txt_total = view.findViewById(R.id.txt_total);
            txt_reason = view.findViewById(R.id.txt_reason);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler == null)
                return;

            int pos = getAdapterPosition();
            Spending spending = spendings.get(pos);
            clickHandler.onClick(spending, this.itemView, pos);
        }
    }
}