package com.xionce.doctorvetServices.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.xionce.doctorvetServices.R;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.util.ArrayList;

public class MovementsAdapter extends RecyclerView.Adapter<MovementsAdapter.MovementsHolder> {

    private HelperClass.AdapterOnClickHandler clickHandler;
    private HelperClass.AdapterOnClickHandler onCheckClickHandler;
    private final ArrayList<Movement> movements;
    public enum AdapterTypes { SHOW, IN_TRANSIT }
    private AdapterTypes type;

    public MovementsAdapter(ArrayList<Movement> movements) {
        this.movements = movements;
    }

    public MovementsAdapter(ArrayList<Movement> movements, HelperClass.AdapterOnClickHandler clickHandler, AdapterTypes type) {
        this.movements = movements;
        this.clickHandler = clickHandler;
        this.type = type;
    }

    public void setOnClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }
    public void setOnCheckClickHandler(HelperClass.AdapterOnClickHandler onCheckClickHandler) {
        this.onCheckClickHandler = onCheckClickHandler;
    }

    @Override
    public MovementsHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_movement;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new MovementsHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovementsHolder movementsHolder, int position) {
        Movement movement = this.movements.get(position);
        movementsHolder.txt_date.setText(HelperClass.getDateInLocaleShort(movement.getDate()));
        movementsHolder.txt_movement_number.setText(movement.getReceipt());

        movementsHolder.button_check_in_transit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onCheckClickHandler == null)
                    return;

                onCheckClickHandler.onClick(movement, view, movementsHolder.getAdapterPosition());
            }
        });
        if (movement.getAccepted_user() != null)
            movementsHolder.button_check_in_transit.setVisibility(View.GONE);

        if (this.type == AdapterTypes.SHOW)
            movementsHolder.button_check_in_transit.setVisibility(View.GONE);
    }

    public void addItems(ArrayList<Movement> items) {
        this.movements.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return movements.size();
    }

    public class MovementsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView txt_date;
        private TextView txt_movement_number;
        private final ImageView button_check_in_transit;

        public MovementsHolder(View view) {
            super(view);
            txt_date = view.findViewById(R.id.txt_date);
            txt_movement_number = view.findViewById(R.id.txt_movement_number);
            button_check_in_transit = view.findViewById(R.id.img_check);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler == null)
                return;

            int pos = getAdapterPosition();
            Movement sell = movements.get(pos);
            clickHandler.onClick(sell, this.itemView, pos);
        }
    }
}