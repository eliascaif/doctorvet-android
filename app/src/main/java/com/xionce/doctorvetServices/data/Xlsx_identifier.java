package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;
import com.xionce.doctorvetServices.DoctorVetApp;

public class Xlsx_identifier {

    @Expose
    private Integer startRow;
    @Expose
    private Integer endRow;
    @Expose
    private String file_identifier;
    @Expose
    private DoctorVetApp.products_prices price_format;
    @Expose
    private Integer id_products_category;
    @Expose
    private Integer start_row;
    @Expose
    private Integer end_row;
    @Expose
    private DoctorVetApp.products_columns_xlsx bar_code_column;
    @Expose
    private DoctorVetApp.products_columns_xlsx name_column;
    @Expose
    private DoctorVetApp.products_columns_xlsx category_column;
    @Expose
    private DoctorVetApp.products_columns_xlsx unit_column;
    @Expose
    private DoctorVetApp.products_columns_xlsx complex_quantity_column;
    @Expose
    private DoctorVetApp.products_columns_xlsx cost_column;
    @Expose
    private DoctorVetApp.products_columns_xlsx tax_column;
    @Expose
    private DoctorVetApp.products_columns_xlsx min_quantity_column;
    @Expose
    private DoctorVetApp.products_columns_xlsx expires_column;
    @Expose
    private DoctorVetApp.products_columns_xlsx fixed_price_1_column;
    @Expose
    private DoctorVetApp.products_columns_xlsx format_price_1_column;
    @Expose
    private DoctorVetApp.products_columns_xlsx margin_price_1_column;
    @Expose
    private DoctorVetApp.products_columns_xlsx fixed_price_2_column;
    @Expose
    private DoctorVetApp.products_columns_xlsx format_price_2_column;
    @Expose
    private DoctorVetApp.products_columns_xlsx margin_price_2_column;
    @Expose
    private DoctorVetApp.products_columns_xlsx fixed_price_3_column;
    @Expose
    private DoctorVetApp.products_columns_xlsx format_price_3_column;
    @Expose
    private DoctorVetApp.products_columns_xlsx margin_price_3_column;

    @Expose
    private DoctorVetApp.products_columns_xlsx reset_quantity_column;

    public void setFile_identifier(String file_identifier) {
        this.file_identifier = file_identifier;
    }
    public String getFile_identifier() {
        return file_identifier;
    }
    public DoctorVetApp.products_prices getPrice_format() {
        return price_format;
    }
    public void setPrice_format(DoctorVetApp.products_prices price_format) {
        this.price_format = price_format;
    }
    public Integer getId_products_category() {
        return id_products_category;
    }
    public void setId_products_category(Integer id_products_category) {
        this.id_products_category = id_products_category;
    }
    public Integer getStart_row() {
        return start_row;
    }
    public void setStart_row(Integer start_row) {
        this.start_row = start_row;
    }
    public Integer getEnd_row() {
        return end_row;
    }
    public void setEnd_row(Integer end_row) {
        this.end_row = end_row;
    }
    public DoctorVetApp.products_columns_xlsx getBar_code_column() {
        return bar_code_column;
    }

    public void setBar_code_column(DoctorVetApp.products_columns_xlsx bar_code_column) {
        this.bar_code_column = bar_code_column;
    }
    public DoctorVetApp.products_columns_xlsx getName_column() {
        return name_column;
    }
    public void setName_column(DoctorVetApp.products_columns_xlsx name_column) {
        this.name_column = name_column;
    }
    public DoctorVetApp.products_columns_xlsx getCost_column() {
        return cost_column;
    }
    public void setCost_column(DoctorVetApp.products_columns_xlsx cost_column) {
        this.cost_column = cost_column;
    }
    public DoctorVetApp.products_columns_xlsx getTax_column() {
        return tax_column;
    }
    public void setTax_column(DoctorVetApp.products_columns_xlsx tax_column) {
        this.tax_column = tax_column;
    }
    public DoctorVetApp.products_columns_xlsx getMargin_price_1_column() {
        return margin_price_1_column;
    }
    public void setMargin_price_1_column(DoctorVetApp.products_columns_xlsx margin_price_1_column) {
        this.margin_price_1_column = margin_price_1_column;
    }
    public DoctorVetApp.products_columns_xlsx getFixed_price_1_column() {
        return fixed_price_1_column;
    }
    public void setFixed_price_1_column(DoctorVetApp.products_columns_xlsx fixed_price_1_column) {
        this.fixed_price_1_column = fixed_price_1_column;
    }
    public DoctorVetApp.products_columns_xlsx getUnit_column() {
        return unit_column;
    }
    public void setUnit_column(DoctorVetApp.products_columns_xlsx unit_column) {
        this.unit_column = unit_column;
    }
    public DoctorVetApp.products_columns_xlsx getComplex_quantity_column() {
        return complex_quantity_column;
    }
    public void setComplex_quantity_column(DoctorVetApp.products_columns_xlsx complex_quantity_column) {
        this.complex_quantity_column = complex_quantity_column;
    }
    public void setStartRow(Integer startRow) {
        this.startRow = startRow;
    }
    public void setEndRow(Integer endRow) {
        this.endRow = endRow;
    }
    public DoctorVetApp.products_columns_xlsx getCategory_column() {
        return category_column;
    }
    public void setCategory_column(DoctorVetApp.products_columns_xlsx category_column) {
        this.category_column = category_column;
    }
    public DoctorVetApp.products_columns_xlsx getReset_quantity_column() {
        return reset_quantity_column;
    }
    public void setReset_quantity_column(DoctorVetApp.products_columns_xlsx reset_quantity_column) {
        this.reset_quantity_column = reset_quantity_column;
    }
    public DoctorVetApp.products_columns_xlsx getMin_quantity_column() {
        return min_quantity_column;
    }
    public void setMin_quantity_column(DoctorVetApp.products_columns_xlsx min_quantity_column) {
        this.min_quantity_column = min_quantity_column;
    }
    public DoctorVetApp.products_columns_xlsx getExpires_column() {
        return expires_column;
    }
    public void setExpires_column(DoctorVetApp.products_columns_xlsx expires_column) {
        this.expires_column = expires_column;
    }
    public DoctorVetApp.products_columns_xlsx getFormat_price_1_column() {
        return format_price_1_column;
    }
    public void setFormat_price_1_column(DoctorVetApp.products_columns_xlsx format_price_1_column) {
        this.format_price_1_column = format_price_1_column;
    }
    public Integer getStartRow() {
        return startRow;
    }
    public Integer getEndRow() {
        return endRow;
    }
    public DoctorVetApp.products_columns_xlsx getFixed_price_2_column() {
        return fixed_price_2_column;
    }
    public void setFixed_price_2_column(DoctorVetApp.products_columns_xlsx fixed_price_2_column) {
        this.fixed_price_2_column = fixed_price_2_column;
    }
    public DoctorVetApp.products_columns_xlsx getFormat_price_2_column() {
        return format_price_2_column;
    }
    public void setFormat_price_2_column(DoctorVetApp.products_columns_xlsx format_price_2_column) {
        this.format_price_2_column = format_price_2_column;
    }
    public DoctorVetApp.products_columns_xlsx getMargin_price_2_column() {
        return margin_price_2_column;
    }
    public void setMargin_price_2_column(DoctorVetApp.products_columns_xlsx margin_price_2_column) {
        this.margin_price_2_column = margin_price_2_column;
    }
    public DoctorVetApp.products_columns_xlsx getFixed_price_3_column() {
        return fixed_price_3_column;
    }
    public void setFixed_price_3_column(DoctorVetApp.products_columns_xlsx fixed_price_3_column) {
        this.fixed_price_3_column = fixed_price_3_column;
    }
    public DoctorVetApp.products_columns_xlsx getFormat_price_3_column() {
        return format_price_3_column;
    }
    public void setFormat_price_3_column(DoctorVetApp.products_columns_xlsx format_price_3_column) {
        this.format_price_3_column = format_price_3_column;
    }
    public DoctorVetApp.products_columns_xlsx getMargin_price_3_column() {
        return margin_price_3_column;
    }
    public void setMargin_price_3_column(DoctorVetApp.products_columns_xlsx margin_price_3_column) {
        this.margin_price_3_column = margin_price_3_column;
    }

}
