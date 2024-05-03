package com.xionce.doctorvetServices.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.xionce.doctorvetServices.DoctorVetApp;
import com.xionce.doctorvetServices.R;
import com.xionce.doctorvetServices.utilities.HelperClass;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.util.ArrayList;

public class DailyCashAdapter extends RecyclerView.Adapter<DailyCashAdapter.SellsAdapterViewHolder> {

    private final HelperClass.AdapterOnClickHandler clickHandler;
    private final ArrayList<Daily_cash> daily_cashes;

    public DailyCashAdapter(ArrayList<Daily_cash> daily_cashes, HelperClass.AdapterOnClickHandler clickHandler) {
        this.daily_cashes = daily_cashes;
        this.clickHandler = clickHandler;
    }

    @Override
    public SellsAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_daily_cash;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new SellsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SellsAdapterViewHolder holder, int position) {
        Daily_cash daily_cash = this.daily_cashes.get(position);
        String title = getTitle(daily_cash);
        
        //thumb
        Context ctx = holder.img_thumb.getContext();
        String thumb_url;
        switch (daily_cash.getType()) {
            case "SELL":
                Glide.with(ctx).load(R.drawable.ic_account_circle_light).apply(RequestOptions.fitCenterTransform()).into(holder.img_thumb);
                if (daily_cash.getOwner() != null)
                    DoctorVetApp.get().setThumb(daily_cash.getOwner().getThumb_url(), holder.img_thumb, R.drawable.ic_account_circle_light);
                break;
            case "SPENDING":
            case "MANUAL_CASH_IN":
            case "MANUAL_CASH_OUT":
                DoctorVetApp.get().setThumb(daily_cash.getUser().getThumb_url(), holder.img_thumb, R.drawable.ic_account_circle_light);
                break;
            case "PURCHASE":
                Glide.with(ctx).load(R.drawable.ic_provider).apply(RequestOptions.fitCenterTransform()).into(holder.img_thumb);
                if (daily_cash.getProvider() != null)
                    DoctorVetApp.get().setThumb(daily_cash.getProvider().getThumb_url(), holder.img_thumb, R.drawable.ic_provider);
                break;
        }

        holder.txt_title.setText(title);
        holder.txt_subtitle.setText(daily_cash.getUser().getName());
        String hour = HelperClass.getTimeInLocale(daily_cash.getDate(), holder.txt_hour.getContext());
        holder.txt_hour.setText(hour);
        holder.txt_total.setText(getTotal(daily_cash));
        holder.txt_payment_type.setText(daily_cash.getPayment_type());

        holder.txt_reason.setVisibility(View.GONE);
        if (daily_cash.getReason() != null) {
            holder.txt_reason.setVisibility(View.VISIBLE);
            holder.txt_reason.setText(daily_cash.getReason());
        }
    }

    private String getTotal(Daily_cash daily_cash) {
        String total_str = HelperClass.formatCurrency(BigDecimal.ZERO);
        if (daily_cash.getDeleted() == 0) {
            total_str = "+" + HelperClass.formatCurrency(daily_cash.getAmount());
            if (!daily_cash.is_entry())
                total_str = "-" + HelperClass.formatCurrency(daily_cash.getAmount());
        }

        return total_str;
    }
    private String getTitle(Daily_cash daily_cash) {
        String title = "";

        switch (daily_cash.getType()) {
            case "SELL":
                if (daily_cash.getOwner() != null)
                    title = "Ingreso por venta a " + daily_cash.getOwner().getName();
                else
                    title = "Ingreso por venta";
                break;
            case "SPENDING":
                title = "Gasto";
                break;
            case "MANUAL_CASH_IN":
                title = "Ingreso manual";
                break;
            case "MANUAL_CASH_OUT":
                title = "Egreso manual";
                break;
            case "PURCHASE":
                if (daily_cash.getProvider() != null)
                    title = "Egreso por compra a " + daily_cash.getProvider().getName();
                else
                    title = "Egreso por compra";
        }

        if (daily_cash.getDeleted() == 1)
            title += " ELIMINADO";

        return title;
    }

    public void addItems(ArrayList<Daily_cash> items) {
        this.daily_cashes.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return daily_cashes.size();
    }

    public class SellsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView img_thumb;
        private final TextView txt_hour;
        private final TextView txt_title;
        private final TextView txt_subtitle;
        private final TextView txt_total;
        private final TextView txt_payment_type;
        private final TextView txt_reason;

        public SellsAdapterViewHolder(View view) {
            super(view);
            img_thumb = view.findViewById(R.id.img_thumb);
            txt_hour = view.findViewById(R.id.txt_hour);
            txt_title = view.findViewById(R.id.txt_title);
            txt_subtitle = view.findViewById(R.id.txt_subtitle);
            txt_total = view.findViewById(R.id.txt_total);
            txt_payment_type = view.findViewById(R.id.txt_payment_type);
            txt_reason = view.findViewById(R.id.txt_reason);
            view.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler == null)
                return;

            int pos = getAdapterPosition();
            Daily_cash daily_cash = daily_cashes.get(pos);
            clickHandler.onClick(daily_cash, this.itemView, pos);
        }
    }
}