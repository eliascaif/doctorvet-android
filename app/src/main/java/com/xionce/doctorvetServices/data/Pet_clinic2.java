package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;
import com.xionce.doctorvetServices.DoctorVetApp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public class Pet_clinic2 implements DoctorVetApp.IResourceObject, Cloneable {
    @Expose
    private Integer id;
    @Expose
    private Date date;
    @Expose
    private String visit_reason;
    @Expose
    private String anamnesis;
    @Expose
    private BigDecimal temp;
    @Expose
    private BigDecimal weight;
    @Expose
    private String pulse;
    @Expose
    private String respiratory_rate;
    @Expose
    private String inspection;
    @Expose
    private String palpation;
    @Expose
    private String auscultation;
    @Expose
    private String helper_methods;
    @Expose
    private String presumptive_diagnostic;
    @Expose
    private Pet pet;
    @Expose
    private ArrayList<Symptom> symptoms;
    @Expose
    private Diagnostic diagnostic;
    @Expose
    private Treatment treatment;
    @Expose
    private ArrayList<Resource> resources;

    private String age;
    private User user;
    private Vet vet;
    private Product_unit temp_unit;
    private Product_unit weight_unit;

    public Pet_clinic2() {
    }

    public User getUser() {
        return user;
    }
    public Vet getVet() {
        return vet;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public String getAnamnesis() {
        return anamnesis;
    }
    public void setAnamnesis(String anamnesis) {
        this.anamnesis = anamnesis;
    }
    public BigDecimal getTemp() {
        return temp;
    }
    public void setTemp(BigDecimal temp) {
        this.temp = temp;
    }
    public BigDecimal getWeight() {
        return weight;
    }
    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
    public String getPulse() {
        return pulse;
    }
    public void setPulse(String pulse) {
        this.pulse = pulse;
    }
    public String getRespiratory_rate() {
        return respiratory_rate;
    }
    public void setRespiratory_rate(String respiratory_rate) {
        this.respiratory_rate = respiratory_rate;
    }
    public ArrayList<Symptom> getSymptoms() {
        if (symptoms == null)
            symptoms = new ArrayList<>();

        return symptoms;
    }
    public void setSymptoms(ArrayList<Symptom> symptoms) {
        this.symptoms = symptoms;
    }
    public Diagnostic getDiagnostic() {
        return diagnostic;
    }
    public void setDiagnostic(Diagnostic diagnostic) {
        this.diagnostic = diagnostic;
    }
    public Treatment getTreatment() {
        return treatment;
    }
    public void setTreatment(Treatment treatment) {
        this.treatment = treatment;
    }
    public String getVisit_reason() {
        return visit_reason;
    }
    public void setVisit_reason(String visit_reason) {
        this.visit_reason = visit_reason;
    }
    public String getInspection() {
        return inspection;
    }
    public void setInspection(String inspection) {
        this.inspection = inspection;
    }
    public String getPalpation() {
        return palpation;
    }
    public void setPalpation(String palpation) {
        this.palpation = palpation;
    }
    public String getAuscultation() {
        return auscultation;
    }
    public void setAuscultation(String auscultation) {
        this.auscultation = auscultation;
    }
    public String getHelper_methods() {
        return helper_methods;
    }
    public void setHelper_methods(String helper_methods) {
        this.helper_methods = helper_methods;
    }
    public String getPresumptive_diagnostic() {
        return presumptive_diagnostic;
    }
    public void setPresumptive_diagnostic(String presumptive_diagnostic) {
        this.presumptive_diagnostic = presumptive_diagnostic;
    }
    public Pet getPet() {
        return pet;
    }
    public void setPet(Pet pet) {
        this.pet = pet;
    }
    public Product_unit getTemp_unit() {
        return temp_unit;
    }
    public void setTemp_unit(Product_unit temp_unit) {
        this.temp_unit = temp_unit;
    }
    public Product_unit getWeight_unit() {
        return weight_unit;
    }
    public void setWeight_unit(Product_unit weight_unit) {
        this.weight_unit = weight_unit;
    }
    public void setResources(ArrayList<Resource> resources) {
        this.resources = resources;
    }
    public String getAge() {
        return age;
    }

    @Override
    public void setThumb_url(String thumb_url) {

    }

    @Override
    public void setPhoto_url(String photo_url) {

    }

    @Override
    public String getPhoto_url() {
        return null;
    }

    @Override
    public void setThumb_deleted(Integer value) {

    }

    @Override
    public ArrayList<Resource> getResources() {
        if (this.resources == null)
            this.resources = new ArrayList<Resource>();

        return resources;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return null;
    }

    public class Clinic2ForInput {
        private ArrayList<Symptom> symptoms;
        private ArrayList<Diagnostic> diagnostics;
        private ArrayList<Treatment> treatments;

        public ArrayList<Symptom> getSymptoms() {
            return symptoms;
        }
        public void setSymptoms(ArrayList<Symptom> symptoms) {
            this.symptoms = symptoms;
        }
        public ArrayList<Diagnostic> getDiagnostics() {
            return diagnostics;
        }
        public void setDiagnostics(ArrayList<Diagnostic> diagnostics) {
            this.diagnostics = diagnostics;
        }
        public ArrayList<Treatment> getTreatments() {
            return treatments;
        }
        public void setTreatments(ArrayList<Treatment> treatments) {
            this.treatments = treatments;
        }
    }

}
