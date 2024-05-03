package com.xionce.doctorvetServices.data;

public class Vet_structure {

    private Integer id;
    private Integer id_vet;
    private String room_type;
    private Integer room_number;
    private String vet_issued_name;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getId_vet() {
        return id_vet;
    }
    public void setId_vet(Integer id_vet) {
        this.id_vet = id_vet;
    }
    public String getRoom_type() {
        return room_type;
    }
    public void setRoom_type(String room_type) {
        this.room_type = room_type;
    }
    public Integer getRoom_number() {
        return room_number;
    }
    public void setRoom_number(Integer room_number) {
        this.room_number = room_number;
    }
    public String getVet_issued_name() {
        return vet_issued_name;
    }
    public void setVet_issued_name(String vet_issued_name) {
        this.vet_issued_name = vet_issued_name;
    }
}
