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

public class FinanceTypesFiscalAdapter extends RecyclerView.Adapter<FinanceTypesFiscalAdapter.FinanceTypeFiscalHolder> {

    private final ArrayList<Finance_types_fiscal> financeTypesFiscal;

    public FinanceTypesFiscalAdapter(ArrayList<Finance_types_fiscal> financeTypesFiscal) {
        this.financeTypesFiscal = financeTypesFiscal;
    }

    @Override
    public FinanceTypeFiscalHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_sell_point;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new FinanceTypeFiscalHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FinanceTypeFiscalHolder holder, int position) {
        Finance_types_fiscal financeTypesReceipts = this.financeTypesFiscal.get(position);
        holder.txt_title.setText(financeTypesReceipts.getName());
    }

    @Override
    public int getItemCount() {
        return financeTypesFiscal.size();
    }

    public int getPositionByName(String name) {
        for (Finance_types_fiscal typesFiscal : financeTypesFiscal) {
            if (typesFiscal.getName().equalsIgnoreCase(name))
                return financeTypesFiscal.indexOf(typesFiscal);
        }
        return -1;
    }

    public ArrayAdapter<Finance_types_fiscal> getArrayAdapter(Context ctx) {
        return new ArrayAdapter<>(ctx, android.R.layout.simple_dropdown_item_1line, financeTypesFiscal);
    }

    public class FinanceTypeFiscalHolder extends RecyclerView.ViewHolder {
        private final TextView txt_title;

        public FinanceTypeFiscalHolder(View view) {
            super(view);
            txt_title = view.findViewById(R.id.txt_title);
        }
    }
}