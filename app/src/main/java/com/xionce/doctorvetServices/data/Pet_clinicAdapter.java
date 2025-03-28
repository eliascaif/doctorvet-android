package com.xionce.doctorvetServices.data;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xionce.doctorvetServices.DoctorVetApp;
import com.xionce.doctorvetServices.R;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.util.ArrayList;
import java.util.Date;

public class Pet_clinicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public enum ClinicaAdapter_types { CLINIC, STUDY, RECIPE, SUPPLY, CLINIC2 }
    private final ArrayList<Pet_clinic_root> clinics;
    private HelperClass.AdapterOnClickHandler clickHandler;
    private HelperClass.AdapterOnLongClickHandler longClickHandler;

    private Activity activity;

    public Pet_clinicAdapter(ArrayList<Pet_clinic_root> clinics, Activity activity) {
        this.clinics = clinics;
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        ClinicaAdapter_types clinica_view_type = ClinicaAdapter_types.values()[viewType];
        switch (clinica_view_type) {
            case CLINIC:
                View view_clinica = inflater.inflate(R.layout.clinic_normal_item, viewGroup, false);
                return new ClinicHolder(view_clinica);
            case SUPPLY:
                View view_suministro = inflater.inflate(R.layout.clinic_supply_item, viewGroup, false);
                return new SupplyHolder(view_suministro);
            case STUDY:
                View view_estudio = inflater.inflate(R.layout.clinic_study_item, viewGroup, false);
                return new StudyHolder(view_estudio);
            case RECIPE:
                View view_recipe = inflater.inflate(R.layout.clinic_recipe_item, viewGroup, false);
                return new RecipeHolder(view_recipe);
            case CLINIC2:
                View view_clinica2 = inflater.inflate(R.layout.clinic2_normal_item, viewGroup, false);
                return new Clinic2Holder(view_clinica2);
            default:
                return null;
                //throw new Exception();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Pet_clinic_root clinica = clinics.get(position);
        ClinicaAdapter_types clinica_view_type = ClinicaAdapter_types.values()[holder.getItemViewType()];

        switch (clinica_view_type) {
            case CLINIC:
                bindClinic(clinica.getClinic(), (ClinicHolder)holder);
                break;
            case SUPPLY:
                bindSupply(clinica.getSupply(), (SupplyHolder)holder);
                break;
            case STUDY:
                bindStudy(clinica.getStudy(), (StudyHolder)holder);
                break;
            case RECIPE:
                bindRecipe(clinica.getRecipe(), (RecipeHolder)holder);
                break;
            case CLINIC2:
                bindClinic2(clinica.getClinic2(), (Clinic2Holder)holder);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return clinics.size();
    }

    @Override
    public int getItemViewType(int position) {
        Pet_clinic_root clinica = clinics.get(position);

        if (clinica.getClinic() != null)
            return ClinicaAdapter_types.CLINIC.ordinal();
        else if (clinica.getStudy() != null)
            return  ClinicaAdapter_types.STUDY.ordinal();
        else if (clinica.getRecipe() != null)
            return  ClinicaAdapter_types.RECIPE.ordinal();
        else if (clinica.getSupply() != null)
            return ClinicaAdapter_types.SUPPLY.ordinal();
        else if (clinica.getClinic2() != null)
            return  ClinicaAdapter_types.CLINIC2.ordinal();

        return -1;
    }

    public ArrayList<Pet_clinic_root> getArrayList() {
        return clinics;
    }

    private void bindClinic(final Pet_clinic clinica, final ClinicHolder holder) {
        String titulo = clinica.getUser().getName();
        String subTitulo = HelperClass.getDateTimeInLocale(clinica.getDate(), holder.txt_title.getContext());
        String text = clinica.getDescription();

        if (clinica.getTemp() != null)
            subTitulo += " - " + clinica.getTemp().toString() + clinica.getTemp_unit().getName();

        if (clinica.getWeight() != null)
            subTitulo += " - " + clinica.getWeight().toString() + clinica.getWeight_unit().getName();

        holder.txt_title.setText(titulo);
        holder.txt_subtitle.setText(subTitulo);
        holder.txt_descripcion.setText(text);

        holder.txt_age.setVisibility(View.GONE);
        if (clinica.getAge() != null) {
            holder.txt_age.setText("Edad:" + clinica.getAge());
            holder.txt_age.setVisibility(View.VISIBLE);
        }

        DoctorVetApp.get().setThumb(clinica.getUser().getThumb_url(), holder.img_image, R.drawable.ic_account_circle_light);

        ResourcesAdapter pet_clinic_resourcesAdapter = new ResourcesAdapter(clinica.getResources(), false, activity);
        holder.recyclerView.setAdapter(pet_clinic_resourcesAdapter);
    }
    private void bindSupply(final Pet_supply supply, final SupplyHolder holder) {
        holder.txt_title.setText(supply.getUser().getName());
        Context dateCtx = holder.txt_title.getContext();
        Date fecha_suministro = (Date)supply.getDate_supply();
        holder.txt_subtitle.setText(HelperClass.getDateTimeInLocale(fecha_suministro, dateCtx));

        holder.txt_age.setVisibility(View.GONE);
        if (supply.getAge() != null) {
            holder.txt_age.setText("Edad:" + supply.getAge());
            holder.txt_age.setVisibility(View.VISIBLE);
        }

        holder.txt_product.setText(supply.getProduct().getName());

        DoctorVetApp.get().setThumb(supply.getUser().getThumb_url(), holder.img_image, R.drawable.ic_account_circle_light);
        DoctorVetApp.get().setThumb(supply.getProduct().getThumb_url(), holder.img_thumb, R.drawable.ic_product_light);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (longClickHandler == null) return false;
                longClickHandler.onLongClick(supply, v, holder.getAdapterPosition());
                return true;
            }
        });
    }
    private void bindStudy(final Pet_study study, final StudyHolder holder) {
        String titulo = study.getUser().getName();
        String subTitulo = HelperClass.getDateTimeInLocale(study.getDate(), holder.txt_title.getContext());
        String notes = study.getNotas();
        holder.txt_title.setText(titulo);
        holder.txt_subtitle.setText(subTitulo);

        holder.txt_age.setVisibility(View.GONE);
        if (study.getAge() != null) {
            holder.txt_age.setText("Edad:" + study.getAge());
            holder.txt_age.setVisibility(View.VISIBLE);
        }

        holder.txt_name_estudio.setText(study.getProduct().getName());

        holder.txt_notes.setText(notes);
        if (notes == null)
            holder.txt_notes.setVisibility(View.GONE);

        DoctorVetApp.get().setThumb(study.getUser().getThumb_url(), holder.img_image, R.drawable.ic_account_circle_light);

        Pet_study_valuesAdapter estudioItemsAdapter = new Pet_study_valuesAdapter(study.getValues(), DoctorVetApp.AdapterSelectTypes.NORMAL);
        holder.recyclerView_study_items.setAdapter(estudioItemsAdapter);

        ResourcesAdapter pet_clinic_resourcesAdapter = new ResourcesAdapter(study.getResources(), false, activity);
        holder.recyclerView_resources.setAdapter(pet_clinic_resourcesAdapter);
    }
    private void bindRecipe(final Pet_recipe recipe, final RecipeHolder holder) {
        String titulo = recipe.getUser().getName();
        String subTitulo = HelperClass.getDateTimeInLocale(recipe.getDate(), holder.txt_title.getContext());

        holder.txt_title.setText(titulo);
        holder.txt_subtitle.setText(subTitulo);

        holder.txt_age.setVisibility(View.GONE);
        if (recipe.getAge() != null) {
            holder.txt_age.setText("Edad:" + recipe.getAge());
            holder.txt_age.setVisibility(View.VISIBLE);
        }

        DoctorVetApp.get().setThumb(recipe.getUser().getThumb_url(), holder.img_image, R.drawable.ic_account_circle_light);

        if (recipe.getTreatment() != null) {
            holder.txt_treatment.setText(recipe.getTreatment().getName());
        } else {
            holder.txt_treatment.setVisibility(View.GONE);
        }

        if (recipe.getNotes() != null) {
            holder.txt_notes.setText(recipe.getNotes());
        } else {
            holder.txt_notes.setVisibility(View.GONE);
        }

        Pet_recipe_itemsAdapter petRecipeItemsAdapter = new Pet_recipe_itemsAdapter(recipe.getItems(), Pet_recipe_itemsAdapter.RecipeItemsAdapterTypes.SHOW);
        holder.recycler_products.setAdapter(petRecipeItemsAdapter);
    }
    private void bindClinic2(final Pet_clinic2 clinic2, final Clinic2Holder holder) {
        String titulo = clinic2.getUser().getName();
        String subTitulo = HelperClass.getDateTimeInLocale(clinic2.getDate(), holder.txt_title.getContext());

        holder.txt_title.setText(titulo);
        holder.txt_subtitle.setText(subTitulo);

        holder.txt_age.setVisibility(View.GONE);
        if (clinic2.getAge() != null) {
            holder.txt_age.setText("Edad:" + clinic2.getAge());
            holder.txt_age.setVisibility(View.VISIBLE);
        }

        String anamnesis = clinic2.getAnamnesis();
        if (anamnesis != null && !anamnesis.isEmpty()) {
            holder.txtAnamnesis.setText(anamnesis);
        } else {
            holder.label_anamnesis.setVisibility(View.GONE);
            holder.txtAnamnesis.setVisibility(View.GONE);
        }

        if (clinic2.getTemp() != null) {
            holder.txt_temp.setText(clinic2.getTemp().toString() + clinic2.getTemp_unit().getName());
        } else {
            holder.label_temp.setVisibility(View.GONE);
            holder.txt_temp.setVisibility(View.GONE);
        }

        if (clinic2.getWeight() != null) {
            holder.txt_weight.setText(clinic2.getWeight().toString() + clinic2.getWeight_unit().getName());
        } else {
            holder.label_weight.setVisibility(View.GONE);
            holder.txt_weight.setVisibility(View.GONE);
        }

        String pulse = clinic2.getPulse();
        if (pulse != null && !pulse.isEmpty()) {
            holder.txt_pulse.setText(pulse);
        } else {
            holder.label_pulse.setVisibility(View.GONE);
            holder.txt_pulse.setVisibility(View.GONE);
        }

        String respiratory_rate = clinic2.getRespiratory_rate();
        if (respiratory_rate != null && !respiratory_rate.isEmpty()) {
            holder.txt_respiratory_rate.setText(respiratory_rate);
        } else {
            holder.label_respiratory_rate.setVisibility(View.GONE);
            holder.txt_respiratory_rate.setVisibility(View.GONE);
        }

        String visit_reason = clinic2.getVisit_reason();
        if (visit_reason != null && !visit_reason.isEmpty()) {
            holder.txt_visit_reason.setText(visit_reason);
        } else {
            holder.label_visit_reason.setVisibility(View.GONE);
            holder.txt_visit_reason.setVisibility(View.GONE);
        }

        String inspection = clinic2.getInspection();
        if (inspection != null && !inspection.isEmpty()) {
            holder.txt_inspection.setText(inspection);
        } else {
            holder.label_inspection.setVisibility(View.GONE);
            holder.txt_inspection.setVisibility(View.GONE);
        }

        String palpation = clinic2.getPalpation();
        if (palpation != null && !palpation.isEmpty()) {
            holder.txt_palpation.setText(palpation);
        } else {
            holder.label_palpation.setVisibility(View.GONE);
            holder.txt_palpation.setVisibility(View.GONE);
        }

        String auscultation = clinic2.getAuscultation();
        if (auscultation != null && !auscultation.isEmpty()) {
            holder.txt_auscultation.setText(auscultation);
        } else {
            holder.label_auscultation.setVisibility(View.GONE);
            holder.txt_auscultation.setVisibility(View.GONE);
        }

        String helper_methods = clinic2.getHelper_methods();
        if (helper_methods != null && !helper_methods.isEmpty()) {
            holder.txt_helper_methods.setText(helper_methods);
        } else {
            holder.label_helper_methods.setVisibility(View.GONE);
            holder.txt_helper_methods.setVisibility(View.GONE);
        }

        String presumptive_diagnostic = clinic2.getPresumptive_diagnostic();
        if (presumptive_diagnostic != null && !presumptive_diagnostic.isEmpty()) {
            holder.txt_presumptive_diagnostic.setText(presumptive_diagnostic);
        } else {
            holder.label_presumptive_diagnostic.setVisibility(View.GONE);
            holder.txt_presumptive_diagnostic.setVisibility(View.GONE);
        }

        String symptoms = android.text.TextUtils.join(", ", clinic2.getSymptoms());
        if (symptoms != null && !symptoms.isEmpty()) {
            holder.txt_symptoms.setText(symptoms);
        } else {
            holder.label_symptoms.setVisibility(View.GONE);
            holder.txt_symptoms.setVisibility(View.GONE);
        }

        if (clinic2.getDiagnostic() != null) {
            holder.txt_diagnostic.setText(clinic2.getDiagnostic().getName());
        } else {
            holder.label_diagnostic.setVisibility(View.GONE);
            holder.txt_diagnostic.setVisibility(View.GONE);
        }

        if (clinic2.getTreatment() != null) {
            holder.txt_treatment.setText(clinic2.getTreatment().getName());
        } else {
            holder.label_treatment.setVisibility(View.GONE);
            holder.txt_treatment.setVisibility(View.GONE);
        }

        DoctorVetApp.get().setThumb(clinic2.getUser().getThumb_url(), holder.img_image, R.drawable.ic_account_circle_light);

        ResourcesAdapter pet_clinic_resourcesAdapter = new ResourcesAdapter(clinic2.getResources(), false, activity);
        holder.recyclerView.setAdapter(pet_clinic_resourcesAdapter);
    }

    public void addItems(ArrayList<Pet_clinic_root> items) {
        this.clinics.addAll(items);
        notifyDataSetChanged();
    }

    public void setOnClickHandler(HelperClass.AdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }
    public void setOnLongClickHandler(HelperClass.AdapterOnLongClickHandler longClickHandler) {
        this.longClickHandler = longClickHandler;
    }

    //onclicks implementados en viewHolders
    public class ClinicHolder extends RecyclerView.ViewHolder implements OnClickListener, View.OnLongClickListener {
        private final ImageView img_image;
        private final TextView txt_title;
        private final TextView txt_subtitle;
        private final TextView txt_age;
        private final TextView txt_descripcion;
        private final RecyclerView recyclerView;

        public ClinicHolder(View view) {
            super(view);
            txt_title = view.findViewById(R.id.txt_title);
            txt_subtitle = view.findViewById(R.id.txt_subtitle);
            txt_age = view.findViewById(R.id.txt_age);
            img_image = view.findViewById(R.id.img_thumb);
            txt_descripcion = view.findViewById(R.id.txt_description);
            recyclerView = view.findViewById(R.id.recyclerview);

            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(recyclerView.getContext(), 2);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler == null)
                return;

            int pos = getAdapterPosition();
            Pet_clinic_root clinic = clinics.get(pos);
            clickHandler.onClick(clinic.getClinic(), this.itemView, pos);
        }

        @Override
        public boolean onLongClick(View view) {
            if (longClickHandler == null)
                return false;

            int pos = getAdapterPosition();
            Pet_clinic_root clinic = clinics.get(pos);
            longClickHandler.onLongClick(clinic.getClinic(), this.itemView, pos);
            return true;
        }
    }
    public class SupplyHolder extends RecyclerView.ViewHolder implements OnClickListener, View.OnLongClickListener {
        private final ImageView img_image;
        private final TextView txt_title;
        private final TextView txt_subtitle;
        private final TextView txt_age;
        private final ImageView img_thumb;
        private final TextView txt_product;

        public SupplyHolder(View view) {
            super(view);
            txt_title = view.findViewById(R.id.txt_title);
            txt_subtitle = view.findViewById(R.id.txt_subtitle);
            txt_age = view.findViewById(R.id.txt_age);
            img_image = view.findViewById(R.id.img_thumb);
            img_thumb = view.findViewById(R.id.img_product);
            txt_product = view.findViewById(R.id.txt_name_product);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickHandler == null) return;
            int pos = getAdapterPosition();
            Pet_clinic_root clinica = clinics.get(pos);
            clickHandler.onClick(clinica.getSupply(), this.itemView, pos);
        }

        @Override
        public boolean onLongClick(View view) {
            if (longClickHandler == null)
                return false;

            int pos = getAdapterPosition();
            Pet_clinic_root clinica = clinics.get(pos);
            longClickHandler.onLongClick(clinica.getSupply(), this.itemView, pos);
            return true;
        }
    }
    public class StudyHolder extends RecyclerView.ViewHolder implements OnClickListener, View.OnLongClickListener {
        private final ImageView img_image;
        private final TextView txt_title;
        private final TextView txt_subtitle;
        private final TextView txt_age;
        private final TextView txt_name_estudio;
        private final TextView txt_notes;
        private final RecyclerView recyclerView_study_items;
        private final RecyclerView recyclerView_resources;

        public StudyHolder(@NonNull View view) {
            super(view);
            txt_title = view.findViewById(R.id.txt_title);
            txt_subtitle = view.findViewById(R.id.txt_subtitle);
            txt_age = view.findViewById(R.id.txt_age);
            img_image = view.findViewById(R.id.img_thumb);
            txt_name_estudio = view.findViewById(R.id.txt_name_estudio);
            txt_notes = view.findViewById(R.id.txt_notes);
            recyclerView_study_items = view.findViewById(R.id.recyclerview_study_items);
            recyclerView_resources = view.findViewById(R.id.recyclerview_resources);

            RecyclerView.LayoutManager layoutManager_items = new LinearLayoutManager(recyclerView_study_items.getContext());
            recyclerView_study_items.setLayoutManager(layoutManager_items);
            recyclerView_study_items.setHasFixedSize(true);

            RecyclerView.LayoutManager layoutManager_resources = new GridLayoutManager(recyclerView_resources.getContext(), 2);
            recyclerView_resources.setLayoutManager(layoutManager_resources);
            recyclerView_resources.setHasFixedSize(true);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler == null)
                return;

            int pos = getAdapterPosition();
            Pet_clinic_root clinica = clinics.get(pos);
            clickHandler.onClick(clinica.getStudy(), this.itemView, pos);
        }

        @Override
        public boolean onLongClick(View view) {
            if (longClickHandler == null)
                return false;

            int pos = getAdapterPosition();
            Pet_clinic_root clinica = clinics.get(pos);
            longClickHandler.onLongClick(clinica.getStudy(), this.itemView, pos);
            return true;
        }
    }
    public class RecipeHolder extends RecyclerView.ViewHolder implements OnClickListener, View.OnLongClickListener {
        private final ImageView img_image;
        private final TextView txt_title;
        private final TextView txt_subtitle;
        private final TextView txt_age;
        private final TextView txt_treatment;
        private final TextView txt_notes;
        private final RecyclerView recycler_products;

        public RecipeHolder(View view) {
            super(view);
            txt_title = view.findViewById(R.id.txt_title);
            txt_subtitle = view.findViewById(R.id.txt_subtitle);
            txt_age = view.findViewById(R.id.txt_age);
            img_image = view.findViewById(R.id.img_thumb);
            txt_treatment = view.findViewById(R.id.txt_treatment);
            txt_notes = view.findViewById(R.id.txt_notes);

            recycler_products = view.findViewById(R.id.recycler_products);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
            recycler_products.setLayoutManager(layoutManager);
            recycler_products.setHasFixedSize(true);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler == null)
                return;

            int pos = getAdapterPosition();
            Pet_clinic_root clinica = clinics.get(pos);
            clickHandler.onClick(clinica.getRecipe(), this.itemView, pos);
        }

        @Override
        public boolean onLongClick(View view) {
            if (longClickHandler == null)
                return false;

            int pos = getAdapterPosition();
            Pet_clinic_root clinica = clinics.get(pos);
            longClickHandler.onLongClick(clinica.getRecipe(), this.itemView, pos);
            return true;
        }
    }
    public class Clinic2Holder extends RecyclerView.ViewHolder implements OnClickListener, View.OnLongClickListener {
        private final ImageView img_image;
        private final TextView txt_title;
        private final TextView txt_subtitle;
        private final TextView txt_age;
        private final TextView label_anamnesis;
        private final TextView txtAnamnesis;
        private final TextView label_temp;
        private final TextView txt_temp;
        private final TextView label_weight;
        private final TextView txt_weight;
        private final TextView label_pulse;
        private final TextView txt_pulse;
        private final TextView label_respiratory_rate;
        private final TextView txt_respiratory_rate;
        private final TextView label_symptoms;
        private final TextView txt_symptoms;
        private final TextView label_diagnostic;
        private final TextView txt_diagnostic;
        private final TextView label_treatment;
        private final TextView txt_treatment;
        private final TextView label_visit_reason;
        private final TextView txt_visit_reason;
        private final TextView label_inspection;
        private final TextView txt_inspection;
        private final TextView label_palpation;
        private final TextView txt_palpation;
        private final TextView label_auscultation;
        private final TextView txt_auscultation;
        private final TextView label_helper_methods;
        private final TextView txt_helper_methods;
        private final TextView label_presumptive_diagnostic;
        private final TextView txt_presumptive_diagnostic;
        private final RecyclerView recyclerView;

        public Clinic2Holder(View view) {
            super(view);
            txt_title = view.findViewById(R.id.txt_title);
            txt_subtitle = view.findViewById(R.id.txt_subtitle);
            txt_age = view.findViewById(R.id.txt_age);
            img_image = view.findViewById(R.id.img_thumb);
            label_anamnesis = view.findViewById(R.id.label_anamnesis);
            txtAnamnesis = view.findViewById(R.id.txt_anamnesis);
            txt_temp = view.findViewById(R.id.txt_temp);
            label_temp = view.findViewById(R.id.label_temp);
            txt_weight = view.findViewById(R.id.txt_weight);
            label_weight = view.findViewById(R.id.label_weight);
            txt_pulse = view.findViewById(R.id.txt_pulse);
            label_pulse = view.findViewById(R.id.label_pulse);
            txt_respiratory_rate = view.findViewById(R.id.txt_respiratory_rate);
            label_respiratory_rate = view.findViewById(R.id.label_respiratory_rate);
            label_symptoms = view.findViewById(R.id.label_symptoms);
            txt_symptoms = view.findViewById(R.id.txt_symptoms);
            label_diagnostic = view.findViewById(R.id.label_diagnostic);
            txt_diagnostic = view.findViewById(R.id.txt_diagnostic);
            label_treatment = view.findViewById(R.id.label_treatment);
            txt_treatment = view.findViewById(R.id.txt_treatment);
            label_visit_reason = view.findViewById(R.id.label_visit_reason);
            txt_visit_reason = view.findViewById(R.id.txt_visit_reason);
            label_inspection = view.findViewById(R.id.label_inspection);
            txt_inspection = view.findViewById(R.id.txt_inspection);
            label_palpation = view.findViewById(R.id.label_palpation);
            txt_palpation = view.findViewById(R.id.txt_palpation);
            label_auscultation = view.findViewById(R.id.label_auscultation);
            txt_auscultation = view.findViewById(R.id.txt_auscultation);
            label_helper_methods = view.findViewById(R.id.label_helper_methods);
            txt_helper_methods = view.findViewById(R.id.txt_helper_methods);
            label_presumptive_diagnostic = view.findViewById(R.id.label_presumptive_diagnostic);
            txt_presumptive_diagnostic = view.findViewById(R.id.txt_presumptive_diagnostic);

            recyclerView = view.findViewById(R.id.recyclerview);

            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(recyclerView.getContext(), 2);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler == null)
                return;

            int pos = getAdapterPosition();
            Pet_clinic_root clinica = clinics.get(pos);
            clickHandler.onClick(clinica.getClinic2(), this.itemView, pos);
        }

        @Override
        public boolean onLongClick(View view) {
            if (longClickHandler == null)
                return false;

            int pos = getAdapterPosition();
            Pet_clinic_root clinica = clinics.get(pos);
            longClickHandler.onLongClick(clinica.getClinic2(), this.itemView, pos);
            return true;
        }
    }

}