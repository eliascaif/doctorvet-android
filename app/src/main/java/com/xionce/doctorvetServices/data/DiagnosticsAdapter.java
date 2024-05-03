package com.xionce.doctorvetServices.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.xionce.doctorvetServices.DoctorVetApp;
import com.xionce.doctorvetServices.R;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.util.ArrayList;

public class DiagnosticsAdapter extends RecyclerView.Adapter<DiagnosticsAdapter.DiagnosticAdapterViewHolder>
        implements DoctorVetApp.IGetIdByValueAdapter, DoctorVetApp.IGetSelectedObject {

    private final ArrayList<Diagnostic> diagnostics;
    private HelperClass.AdapterOnClickHandler clickHandler;

    public DiagnosticsAdapter(ArrayList<Diagnostic> diagnostics) {
        this.diagnostics = diagnostics;
    }

    @Override
    public DiagnosticAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_simple;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new DiagnosticAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DiagnosticAdapterViewHolder DiagnosticAdapterViewHolder, int position) {
        Diagnostic diagnostic = this.diagnostics.get(position);
        DiagnosticAdapterViewHolder.txt_title.setText(diagnostic.getName());
    }

    @Override
    public int getItemCount() {
        return diagnostics.size();
    }

    @Override
    public Integer getIdByValue(String value) {
        for (Diagnostic diagnostic : diagnostics) {
            if (diagnostic.getName().equals(value))
                return diagnostic.getId();
        }
        return null;
    }
    @Override
    public Object getObjectByName(String name) {
        for (Diagnostic r: diagnostics) {
            if (r.getName().equals(name))
                return r;
        }
        return null;
    }

    public ArrayAdapter<Diagnostic> getArrayAdapter(Context ctx) {
        ArrayAdapter<Diagnostic> arrayAdapter = new ArrayAdapter<Diagnostic>(ctx, android.R.layout.simple_dropdown_item_1line, diagnostics);
        return arrayAdapter;
    }

    public ArrayList<Diagnostic> getArrayList() {
        return diagnostics;
    }

    public void setList(ArrayList<Diagnostic> list) {
        diagnostics.clear();
        diagnostics.addAll(list);
        notifyDataSetChanged();
    }
    public void setOnClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }
    public void addItems(ArrayList<Diagnostic> diagnostics) {
        this.diagnostics.addAll(diagnostics);
        notifyDataSetChanged();
    }

    public class DiagnosticAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView txt_title;

        public DiagnosticAdapterViewHolder(View view) {
            super(view);
            txt_title = view.findViewById(R.id.txt_title);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler == null)
                return;

            int pos = getAdapterPosition();
            Diagnostic diagnostic = diagnostics.get(pos);
            clickHandler.onClick(diagnostic, this.itemView, pos);
        }
    }
}
