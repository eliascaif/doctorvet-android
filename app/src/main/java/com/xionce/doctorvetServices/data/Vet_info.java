package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

public class Vet_info {
    @Expose
    private Integer is_multi_deposit_vet;
    @Expose
    private Integer is_multi_sell_point_vet;
    @Expose
    private Integer is_multi_movement_point_vet;

    public Integer getIs_multi_movement_point_vet() {
        return is_multi_movement_point_vet;
    }
    public void setIs_multi_movement_point_vet(Integer is_multi_movement_point_vet) {
        this.is_multi_movement_point_vet = is_multi_movement_point_vet;
    }
    public Vet_point getDefault_sell_point() {
        return default_vet_point;
    }
    public void setDefault_sell_point(Vet_point default_vet_point) {
        this.default_vet_point = default_vet_point;
    }
    private Vet_point default_vet_point;
    public Integer getIs_multi_deposit_vet() {
        return is_multi_deposit_vet;
    }
    public void setIs_multi_deposit_vet(Integer is_multi_deposit_vet) {
        this.is_multi_deposit_vet = is_multi_deposit_vet;
    }
    public Integer getIs_multi_sell_point_vet() {
        return is_multi_sell_point_vet;
    }
    public void setIs_multi_sell_point_vet(Integer is_multi_sell_point_vet) {
        this.is_multi_sell_point_vet = is_multi_sell_point_vet;
    }

}
