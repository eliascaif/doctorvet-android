package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

public class Get_pagination {
    @Expose
    private Integer page;
    @Expose
    private Integer results_per_page;
    @Expose
    private Integer total_results;
    @Expose
    private Integer total_pages;

    public Integer getPage() {
        return page;
    }
    public void setPage(Integer page) {
        this.page = page;
    }
    public Integer getResults_per_page() {
        return results_per_page;
    }
    public void setResults_per_page(Integer results_per_page) {
        this.results_per_page = results_per_page;
    }
    public Integer getTotal_results() {
        return total_results;
    }
    public void setTotal_results(Integer total_results) {
        this.total_results = total_results;
    }
    public Integer getTotal_pages() {
        return total_pages;
    }
    public void setTotal_pages(Integer total_pages) {
        this.total_pages = total_pages;
    }

}
