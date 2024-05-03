package com.xionce.doctorvetServices.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xionce.doctorvetServices.R;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Finance_payment_methodsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public enum Finance_payment_methodsAdapter_types { REMOVE, NONE }
    private final Finance_payment_methodsAdapter_types adapter_type;
    private final ArrayList<Finance_payment_method> finance_payment_methods;
    private HelperClass.AdapterOnRemoveItemHandler removeItemHandler;

    public Finance_payment_methodsAdapter(ArrayList<Finance_payment_method> finance_payment_methods, Finance_payment_methodsAdapter_types adapter_type) {
        this.finance_payment_methods = finance_payment_methods;
        this.adapter_type = adapter_type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch (adapter_type) {
            case REMOVE:
                View view_delete = inflater.inflate(R.layout.list_item_simple_remove, viewGroup, false);
                return new PaymentMethodRemoveAdapterViewHolder(view_delete);
            case NONE:
                View view_none = inflater.inflate(R.layout.list_item_simple, viewGroup, false);
                return new PaymentMethodNoneAdapterViewHolder(view_none);
            default:
                throw new RuntimeException("non-existent viewType");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Finance_payment_method finance_payment_method = this.finance_payment_methods.get(position);

        switch (adapter_type) {
            case REMOVE:
                bindRemove(finance_payment_method, (PaymentMethodRemoveAdapterViewHolder)holder, position);
                break;
            case NONE:
                bindNone(finance_payment_method, (PaymentMethodNoneAdapterViewHolder)holder, position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return finance_payment_methods.size();
    }

    private void bindRemove(Finance_payment_method payment_method, PaymentMethodRemoveAdapterViewHolder holder, int position) {
        String short_date = HelperClass.getShortDate_inStr(payment_method.getDate(), holder.txt_title.getContext());
        String deleted = "";
        if (payment_method.getDeleted() == 1) {
            deleted = " ELIMINADO ";
            holder.img_remove.setVisibility(View.GONE);
        }
        String title = short_date + ". " + payment_method.getName() + ": " + HelperClass.formatCurrency(payment_method.getAmount()) + deleted;
        holder.txt_title.setText(title);
    }
    private void bindNone(Finance_payment_method payment_method, PaymentMethodNoneAdapterViewHolder holder, int position) {
        String short_date = HelperClass.getShortDate_inStr(payment_method.getDate(), holder.txt_title.getContext());
        String deleted = "";
        if (payment_method.getDeleted() == 1)
            deleted = " ELIMINADO ";
        String title = short_date + ". " + payment_method.getName() + ": " + HelperClass.formatCurrency(payment_method.getAmount()) + deleted;
        holder.txt_title.setText(title);
    }

    public void addItem(Finance_payment_method payment_method) {
        finance_payment_methods.add(payment_method);
        notifyDataSetChanged();
    }
    public void addItems(ArrayList<Finance_payment_method> cashMovements) {
        this.finance_payment_methods.addAll(cashMovements);
        notifyDataSetChanged();
    }
    public void addItemFirst(Finance_payment_method payment_method) {
        finance_payment_methods.add(0, payment_method);
        notifyDataSetChanged();
        //notifyItemInserted(0);
    }
    public void removeItem(int pos) {
        finance_payment_methods.remove(pos);
        //notifyDataSetChanged();
        notifyItemRemoved(pos);
    }

    public int getItemIndex(Finance_payment_method finance_payment_method) {
        //return finance_payment_methods.indexOf(finance_payment_method); //returns -1
        for (int i = 0; i < finance_payment_methods.size(); i++) {
            if (finance_payment_methods.get(i).getName().equalsIgnoreCase(finance_payment_method.getName()))
                return i;
        }

        return -1;
    }
    public ArrayAdapter<Finance_payment_method> getArrayAdapter(Context ctx) {
        return new ArrayAdapter<>(ctx, android.R.layout.simple_dropdown_item_1line, finance_payment_methods);
    }
    public void setOnRemoveItemHandler(HelperClass.AdapterOnRemoveItemHandler removeItemHandler) {
        this.removeItemHandler = removeItemHandler;
    }
    public boolean isPossiblePayment(Finance_payment_method payment, BigDecimal totalToPay) {
        BigDecimal sumOfPayments = Sell.calculateSumOfPayments(finance_payment_methods).add(payment.getAmount());
        BigDecimal balance = totalToPay.subtract(sumOfPayments);
        if (balance.compareTo(BigDecimal.ZERO) < 0)
            return false;

        return true;
    }
    public ArrayList<Finance_payment_method> getArrayList() {
        return finance_payment_methods;
    }

    public class PaymentMethodRemoveAdapterViewHolder extends RecyclerView.ViewHolder {
        private final TextView txt_title;
        protected ImageView img_remove;

        public PaymentMethodRemoveAdapterViewHolder(View view) {
            super(view);
            txt_title = view.findViewById(R.id.txt_item_name);

            img_remove = view.findViewById(R.id.img_remove);
            img_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (removeItemHandler == null) return;

                    int pos = getAdapterPosition();
                    Finance_payment_method finance_payment_method = finance_payment_methods.get(pos);
                    removeItemHandler.onRemoveItem(finance_payment_method, view, pos);
                }
            });

        }
    }
    public class PaymentMethodNoneAdapterViewHolder extends RecyclerView.ViewHolder {
        private final TextView txt_title;

        public PaymentMethodNoneAdapterViewHolder(View view) {
            super(view);
            txt_title = view.findViewById(R.id.txt_title);
        }
    }

}
