package com.xionce.doctorvetServices.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xionce.doctorvetServices.R;

import java.util.ArrayList;

public class FinanceTypesReceiptsAdapter extends RecyclerView.Adapter<FinanceTypesReceiptsAdapter.FinanceTypeReceiptHolder> {

    private final ArrayList<Finance_types_receipts> financeTypesReceipts;

    public FinanceTypesReceiptsAdapter(ArrayList<Finance_types_receipts> financeTypesReceipts) {
        this.financeTypesReceipts = financeTypesReceipts;
    }

    @Override
    public FinanceTypeReceiptHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_sell_point;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new FinanceTypeReceiptHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FinanceTypeReceiptHolder holder, int position) {
        Finance_types_receipts financeTypesReceipts = this.financeTypesReceipts.get(position);
        holder.txt_title.setText(financeTypesReceipts.getDenomination());
    }

    @Override
    public int getItemCount() {
        return financeTypesReceipts.size();
    }

    public ArrayAdapter<Finance_types_receipts> getArrayAdapter(Context ctx) {
        return new ArrayAdapter<>(ctx, android.R.layout.simple_dropdown_item_1line, financeTypesReceipts);
    }

    public class FinanceTypeReceiptHolder extends RecyclerView.ViewHolder {
        private final TextView txt_title;

        public FinanceTypeReceiptHolder(View view) {
            super(view);
            txt_title = view.findViewById(R.id.txt_title);
        }
    }
}