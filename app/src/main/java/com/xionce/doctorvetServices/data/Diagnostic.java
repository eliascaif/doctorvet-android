package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;
import com.xionce.doctorvetServices.DoctorVetApp;

import java.util.ArrayList;

public class Diagnostic {
    @Expose
    private Integer id;
    @Expose
    private String name;
//    @Expose
//    private DoctorVetApp.Sdt_type type = DoctorVetApp.Sdt_type.DIAGNOSTIC;

    public Diagnostic() {
    }
    public Diagnostic(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
//    public DoctorVetApp.Sdt_type getType() {
//        return type;
//    }

    @Override
    public String toString() {
        return name;
    }

    public class Get_pagination_diagnostics extends Get_pagination {
        @Expose
        private ArrayList<Diagnostic> content;

        public ArrayList<Diagnostic> getContent() {
            return content;
        }
        public void setContent(ArrayList<Diagnostic> content) {
            this.content = content;
        }
    }

}
